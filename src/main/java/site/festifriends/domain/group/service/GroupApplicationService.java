package site.festifriends.domain.group.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import site.festifriends.domain.application.dto.JoinedGroupResponse;
import site.festifriends.domain.group.repository.MemberGroupRepository;
import site.festifriends.domain.review.repository.ReviewRepository;
import site.festifriends.entity.Group;
import site.festifriends.entity.MemberGroup;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Role;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupApplicationService {

    private final MemberGroupRepository memberGroupRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 신청서 목록 조회
     */
    public CursorResponseWrapper<ApplicationListResponse> getApplicationsWithSlice(Long hostId, Long cursorId,
        int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<MemberGroup> slice = memberGroupRepository.findApplicationsWithSlice(hostId, cursorId, pageable);

        List<MemberGroup> applications = slice.getContent();

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
        Map<Long, List<MemberGroup>> groupedByGroup = applications.stream()
            .collect(Collectors.groupingBy(app -> app.getGroup().getId()));

        List<ApplicationListResponse> responses = groupedByGroup.entrySet().stream()
            .map(entry -> {
                List<MemberGroup> groupApplications = entry.getValue();
                MemberGroup firstApp = groupApplications.get(0);

                List<ApplicationListResponse.ApplicationInfo> applicationInfos = groupApplications.stream()
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
                    .groupId(firstApp.getGroup().getId().toString())
                    .groupName(firstApp.getGroup().getTitle())
                    .poster(firstApp.getGroup().getPerformance().getPoster())
                    .applications(applicationInfos)
                    .build();
            })
            .collect(Collectors.toList());

        // 다음 커서 ID 계산
        Long nextCursorId = null;
        if (slice.hasNext() && !applications.isEmpty()) {
            MemberGroup lastApplication = applications.get(applications.size() - 1);
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
     * 신청한 모임 목록 조회
     */
    public CursorResponseWrapper<AppliedListResponse> getAppliedGroupsWithSlice(Long memberId, Long cursorId,
        int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<MemberGroup> slice = memberGroupRepository.findAppliedApplicationsWithSlice(memberId, cursorId, pageable);

        List<MemberGroup> applications = slice.getContent();

        if (applications.isEmpty()) {
            return CursorResponseWrapper.empty("신청한 모임 목록이 정상적으로 조회되었습니다.");
        }

        // 모임 ID 목록 추출
        List<Long> groupIds = applications.stream()
            .map(app -> app.getGroup().getId())
            .collect(Collectors.toList());

        // 모임별 방장 정보 조회
        Map<Long, MemberGroup> hostMap = memberGroupRepository.findHostsByGroupIds(groupIds);

        // 모임별 확정 인원 수 조회
        Map<Long, Long> confirmedCountMap = memberGroupRepository.findConfirmedMemberCountsByGroupIds(groupIds);

        // 응답 생성
        List<AppliedListResponse> responses = applications.stream()
            .map(app -> {
                Group group = app.getGroup();
                MemberGroup host = hostMap.get(group.getId());
                Long confirmedCount = confirmedCountMap.getOrDefault(group.getId(), 0L);

                return AppliedListResponse.builder()
                    .applicationId(app.getId().toString())
                    .groupId(group.getId().toString())
                    .groupName(group.getTitle())
                    .performanceId(group.getPerformance().getId().toString())
                    .poster(group.getPerformance().getPoster())
                    .leaderNickname(host != null ? host.getMember().getNickname() : null)
                    .leaderRating(0.0) // TODO: Get actual rating
                    .gender(group.getGenderType())
                    .applicationText(app.getApplicationText())
                    .status(app.getStatus())
                    .createdAt(app.getCreatedAt())
                    .build();
            })
            .collect(Collectors.toList());

        // 다음 커서 ID 계산
        Long nextCursorId = null;
        if (slice.hasNext() && !applications.isEmpty()) {
            MemberGroup lastApplication = applications.get(applications.size() - 1);
            nextCursorId = lastApplication.getId();
        }

        return CursorResponseWrapper.success(
            "신청한 모임 목록이 정상적으로 조회되었습니다.",
            responses,
            nextCursorId,
            slice.hasNext()
        );
    }

    /**
     * 참가 중인 모임 목록 조회
     */
    public CursorResponseWrapper<JoinedGroupResponse> getJoinedGroupsWithSlice(Long memberId, Long cursorId,
        int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<MemberGroup> slice = memberGroupRepository.findJoinedGroupsWithSlice(memberId, cursorId, pageable);

        List<MemberGroup> joinedGroups = slice.getContent();

        if (joinedGroups.isEmpty()) {
            return CursorResponseWrapper.empty("참가 중인 모임 목록이 정상적으로 조회되었습니다.");
        }

        // 모임 ID 목록 추출
        List<Long> groupIds = joinedGroups.stream()
            .map(app -> app.getGroup().getId())
            .collect(Collectors.toList());

        // 모임별 방장 정보 조회
        Map<Long, MemberGroup> hostMap = memberGroupRepository.findHostsByGroupIds(groupIds);

        // 모임별 확정 인원 수 조회
        Map<Long, Long> confirmedCountMap = memberGroupRepository.findConfirmedMemberCountsByGroupIds(groupIds);

        // 응답 생성
        List<JoinedGroupResponse> responses = joinedGroups.stream()
            .map(app -> {
                Group group = app.getGroup();
                MemberGroup host = hostMap.get(group.getId());
                Long confirmedCount = confirmedCountMap.getOrDefault(group.getId(), 0L);

                JoinedGroupResponse.Performance performance = JoinedGroupResponse.Performance.builder()
                    .id(group.getPerformance().getId().toString())
                    .poster(group.getPerformance().getPoster())
                    .build();

                JoinedGroupResponse.Host hostInfo = JoinedGroupResponse.Host.builder()
                    .id(host != null ? host.getMember().getId().toString() : null)
                    .name(host != null ? host.getMember().getNickname() : null)
                    .rating(0.0) // TODO: Get actual rating
                    .build();

                return JoinedGroupResponse.builder()
                    .id(group.getId().toString())
                    .performance(performance)
                    .title(group.getTitle())
                    .category(group.getGatherType())
                    .gender(group.getGenderType())
                    .startAge(group.getStartAge())
                    .endAge(group.getEndAge())
                    .location(group.getLocation())
                    .startDate(group.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME))
                    .endDate(group.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME))
                    .memberCount(confirmedCount.intValue())
                    .maxMembers(group.getCount())
                    .description(group.getIntroduction())
                    .hashtag(group.getHashTags())
                    .host(hostInfo)
                    .build();
            })
            .collect(Collectors.toList());

        // 다음 커서 ID 계산
        Long nextCursorId = null;
        if (slice.hasNext() && !joinedGroups.isEmpty()) {
            MemberGroup lastGroup = joinedGroups.get(joinedGroups.size() - 1);
            nextCursorId = lastGroup.getId();
        }

        return CursorResponseWrapper.success(
            "참가 중인 모임 목록이 정상적으로 조회되었습니다.",
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
        MemberGroup application = memberGroupRepository.findById(applicationId)
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
        MemberGroup application = memberGroupRepository.findById(applicationId)
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

    private void validateHostPermission(Long hostId, MemberGroup application) {
        boolean isHost = memberGroupRepository.existsByGroupIdAndMemberIdAndRole(
            application.getGroup().getId(),
            hostId,
            Role.HOST
        );

        if (!isHost) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "모임 방장만 신청서를 처리할 수 있습니다.");
        }
    }

    private void validateApplicantPermission(Long memberId, MemberGroup application) {
        if (!application.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 신청서만 확정할 수 있습니다.");
        }
    }
}
