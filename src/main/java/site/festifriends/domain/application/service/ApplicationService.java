package site.festifriends.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.application.dto.ApplicationListResponse;
import site.festifriends.domain.application.dto.ApplicationStatusRequest;
import site.festifriends.domain.application.dto.ApplicationStatusResponse;
import site.festifriends.domain.application.dto.AppliedListResponse;
import site.festifriends.domain.application.repository.ApplicationRepository;
import site.festifriends.domain.review.repository.ReviewRepository;
import site.festifriends.entity.MemberParty;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Role;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 신청서 목록 조회
     */
    public CursorResponseWrapper<ApplicationListResponse> getApplicationsWithSlice(Long hostId, Long cursorId,
        int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<MemberParty> slice = applicationRepository.findApplicationsWithSlice(hostId, cursorId, pageable);

        List<MemberParty> applications = slice.getContent();

        if (applications.isEmpty()) {
            return CursorResponseWrapper.empty("모임 신청서 목록이 정상적으로 조회되었습니다.");
        }

        // 신청자들의 평점 조회
        List<Long> memberIds = applications.stream()
            .map(app -> app.getMember().getId())
            .distinct()
            .collect(Collectors.toList());

        Map<Long, Double> ratingMap = reviewRepository.findAverageRatingsByMemberIds(memberIds)
            .stream()
            .collect(Collectors.toMap(
                result -> (Long) result.get("memberId"),
                result -> (Double) result.get("avgRating")
            ));

        // 모임별로 그룹화
        Map<Long, List<MemberParty>> groupedByParty = applications.stream()
            .collect(Collectors.groupingBy(app -> app.getParty().getId()));

        List<ApplicationListResponse> responses = groupedByParty.entrySet().stream()
            .map(entry -> {
                List<MemberParty> partyApplications = entry.getValue();
                MemberParty firstApp = partyApplications.get(0);

                List<ApplicationListResponse.ApplicationInfo> applicationInfos = partyApplications.stream()
                    .map(app -> ApplicationListResponse.ApplicationInfo.builder()
                        .applicationId(app.getId().toString())
                        .userId(app.getMember().getId().toString())
                        .nickname(app.getMember().getNickname())
                        .rating(ratingMap.getOrDefault(app.getMember().getId(), 0.0))
                        .gender(app.getMember().getGender())
                        .age(app.getMember().getAge())
                        .applicationText(app.getApplicationText())
                        .createdAt(app.getCreatedAt())
                        .build())
                    .collect(Collectors.toList());

                return ApplicationListResponse.builder()
                    .groupId(firstApp.getParty().getId().toString())
                    .groupName(firstApp.getParty().getTitle())
                    .poster(firstApp.getParty().getFestival().getPosterUrl())
                    .applications(applicationInfos)
                    .build();
            })
            .collect(Collectors.toList());

        // 다음 커서 ID 계산
        Long nextCursorId = null;
        if (slice.hasNext() && !applications.isEmpty()) {
            MemberParty lastApplication = applications.get(applications.size() - 1);
            nextCursorId = lastApplication.getId();
        }

        return CursorResponseWrapper.success(
            "모임 신청서 목록이 정상적으로 조회되었습니다.",
            responses,
            nextCursorId,
            slice.hasNext()
        );
    }

    /**
     * 내가 신청한 모임 목록 조회
     */
    public CursorResponseWrapper<AppliedListResponse> getAppliedApplicationsWithSlice(Long memberId, Long cursorId,
        int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<MemberParty> slice = applicationRepository.findAppliedApplicationsWithSlice(memberId, cursorId, pageable);

        List<MemberParty> applications = slice.getContent();

        if (applications.isEmpty()) {
            return CursorResponseWrapper.empty("신청서 목록이 정상적으로 조회되었습니다.");
        }

        // 파티별 방장 정보 조회
        List<Long> partyIds = applications.stream()
            .map(app -> app.getParty().getId())
            .distinct()
            .collect(Collectors.toList());

        Map<Long, MemberParty> hostInfoMap = applicationRepository.findHostsByPartyIds(partyIds);

        // 방장들의 평점 조회
        List<Long> hostIds = hostInfoMap.values().stream()
            .map(host -> host.getMember().getId())
            .distinct()
            .collect(Collectors.toList());

        Map<Long, Double> hostRatingMap = reviewRepository.findAverageRatingsByMemberIds(hostIds)
            .stream()
            .collect(Collectors.toMap(
                result -> (Long) result.get("memberId"),
                result -> (Double) result.get("avgRating")
            ));

        List<AppliedListResponse> responses = applications.stream()
            .map(app -> {
                MemberParty hostInfo = hostInfoMap.get(app.getParty().getId());
                String hostNickname = hostInfo != null ? hostInfo.getMember().getNickname() : "알 수 없음";
                Long hostId = hostInfo != null ? hostInfo.getMember().getId() : null;

                return AppliedListResponse.builder()
                    .applicationId(app.getId().toString())
                    .performanceId(app.getParty().getFestival().getId().toString())
                    .poster(app.getParty().getFestival().getPosterUrl())
                    .groupId(app.getParty().getId().toString())
                    .groupName(app.getParty().getTitle())
                    .leaderNickname(hostNickname)
                    .leaderRating(hostRatingMap.getOrDefault(hostId, 0.0))
                    .gender(app.getParty().getGenderType())
                    .applicationText(app.getApplicationText())
                    .createdAt(app.getCreatedAt())
                    .status(app.getStatus())
                    .build();
            })
            .collect(Collectors.toList());

        // 다음 커서 ID 계산
        Long nextCursorId = null;
        if (slice.hasNext() && !applications.isEmpty()) {
            MemberParty lastApplication = applications.get(applications.size() - 1);
            nextCursorId = lastApplication.getId();
        }

        return CursorResponseWrapper.success(
            "신청서 목록이 정상적으로 조회되었습니다.",
            responses,
            nextCursorId,
            slice.hasNext()
        );
    }

    /**
     * 모임 신청서 수락/거절
     */
    @Transactional
    public ResponseWrapper<ApplicationStatusResponse> updateApplicationStatus(
        Long hostId,
        Long applicationId,
        ApplicationStatusRequest request
    ) {
        MemberParty application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "신청서를 찾을 수 없습니다."));

        validateHostPermission(hostId, application);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "처리할 수 없는 신청서 상태입니다.");
        }

        String status = request.getStatus();
        String message;

        if ("accept".equals(status)) {
            application.approve();
            message = "모임 가입 신청을 수락하였습니다";
        } else if ("reject".equals(status)) {
            application.reject();
            message = "모임 가입 신청을 거절하였습니다";
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "잘못된 상태 값입니다.");
        }

        ApplicationStatusResponse response = ApplicationStatusResponse.builder()
            .result(true)
            .build();

        return ResponseWrapper.success(message, response);
    }

    /**
     * 모임 가입 확정
     */
    @Transactional
    public ResponseWrapper<ApplicationStatusResponse> confirmApplication(
        Long memberId,
        Long applicationId
    ) {
        MemberParty application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "신청서를 찾을 수 없습니다."));

        validateApplicantPermission(memberId, application);

        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "수락된 신청서만 확정할 수 있습니다.");
        }

        application.confirm();

        ApplicationStatusResponse response = ApplicationStatusResponse.builder()
            .result(true)
            .build();

        return ResponseWrapper.success("모임 가입을 확정하였습니다", response);
    }

    private void validateHostPermission(Long hostId, MemberParty application) {
        boolean isHost = applicationRepository.existsByPartyIdAndMemberIdAndRole(
            application.getParty().getId(),
            hostId,
            Role.HOST
        );

        if (!isHost) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "모임 방장만 신청서를 처리할 수 있습니다.");
        }
    }

    private void validateApplicantPermission(Long memberId, MemberParty application) {
        if (!application.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 신청서만 확정할 수 있습니다.");
        }
    }
} 