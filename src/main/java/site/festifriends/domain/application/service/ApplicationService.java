package site.festifriends.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.application.dto.ApplicationListResponse;
import site.festifriends.domain.application.repository.ApplicationRepository;
import site.festifriends.domain.review.repository.ReviewRepository;
import site.festifriends.entity.MemberParty;

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
    public CursorResponseWrapper<ApplicationListResponse> getApplicationsWithSlice(Long hostId, Long cursorId, int size) {
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
} 