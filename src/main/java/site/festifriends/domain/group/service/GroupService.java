package site.festifriends.domain.group.service;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.domain.application.repository.ApplicationRepository;
import site.festifriends.domain.group.dto.GroupResponse;
import site.festifriends.domain.group.dto.PerformanceGroupsData;
import site.festifriends.domain.group.repository.GroupBookmarkRepository;
import site.festifriends.domain.group.repository.GroupRepository;
import site.festifriends.domain.performance.repository.PerformanceRepository;
import site.festifriends.domain.review.repository.ReviewRepository;
import site.festifriends.entity.Group;
import site.festifriends.entity.MemberGroup;
import site.festifriends.entity.Performance;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupBookmarkRepository groupBookmarkRepository;
    private final ApplicationRepository applicationRepository;
    private final PerformanceRepository performanceRepository;
    private final ReviewRepository reviewRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * 공연별 모임 목록 조회
     */
    public PerformanceGroupsData getGroupsByPerformanceId(Long performanceId, int page, int size, Long memberId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Performance performance = performanceRepository.findById(performanceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "존재하지 않는 공연입니다."));
        Page<Group> groupPage = groupRepository.findByPerformanceId(performanceId, pageable);

        List<Group> groups = groupPage.getContent();

        groups.forEach(group -> {
            group.getHashTags().size();
        });

        // 모임 ID 목록 추출
        List<Long> groupIds = groups.stream()
            .map(Group::getId)
            .collect(Collectors.toList());

        // 모임별 방장 정보 조회
        Map<Long, MemberGroup> hostMap = applicationRepository.findHostsByGroupIds(groupIds);

        // 모임별 멤버 수 조회
        Map<Long, Long> memberCountMap = applicationRepository.findConfirmedMemberCountsByGroupIds(groupIds);

        // 방장 ID 목록 추출
        List<Long> hostIds = hostMap.values().stream()
            .map(host -> host.getMember().getId())
            .collect(Collectors.toList());

        // 방장별 평점 조회
        Map<Long, Double> hostRatingMap = Collections.emptyMap();
        if (!hostIds.isEmpty()) {
            hostRatingMap = reviewRepository.findAverageRatingsByMemberIds(hostIds).stream()
                .collect(Collectors.toMap(
                    result -> (Long) result.get("memberId"),
                    result -> (Double) result.get("avgRating")
                ));
        }

        // 로그인한 경우 찜 여부 조회
        final List<Long> bookmarkedGroupIds = memberId != null
            ? groupBookmarkRepository.findBookmarkedGroupIdsByMemberIdAndGroupIds(memberId, groupIds)
            : Collections.emptyList();

        final Map<Long, Long> finalMemberCountMap = memberCountMap;
        final Map<Long, Double> finalHostRatingMap = hostRatingMap;

        List<GroupResponse> groupResponses = groups.stream()
            .map(group -> convertToGroupResponse(
                group,
                hostMap.get(group.getId()),
                bookmarkedGroupIds.contains(group.getId()),
                finalMemberCountMap,
                finalHostRatingMap
            ))
            .collect(Collectors.toList());

        Long totalGroupCount = groupRepository.countByPerformanceId(performanceId);

        return PerformanceGroupsData.builder()
            .performanceId(performanceId.toString())
            .groupCount(totalGroupCount.intValue())
            .groups(groupResponses)
            .page(page)
            .size(size)
            .totalElements(groupPage.getTotalElements())
            .totalPages(groupPage.getTotalPages())
            .first(groupPage.isFirst())
            .last(groupPage.isLast())
            .build();
    }

    private GroupResponse convertToGroupResponse(Group group, MemberGroup hostMemberGroup, boolean isFavorite,
        Map<Long, Long> memberCountMap, Map<Long, Double> hostRatingMap) {
        // 호스트 정보 설정
        GroupResponse.Host host = null;
        if (hostMemberGroup != null) {
            Long hostId = hostMemberGroup.getMember().getId();
            Double hostRating = hostRatingMap.getOrDefault(hostId, 0.0);

            host = GroupResponse.Host.builder()
                .hostId(hostId.toString())
                .name(hostMemberGroup.getMember().getNickname())
                .rating(hostRating)
                .build();
        }

        int memberCount = memberCountMap.getOrDefault(group.getId(), 0L).intValue();

        return GroupResponse.builder()
            .groupId(group.getId().toString())
            .title(group.getTitle())
            .category(group.getGatherType())
            .gender(group.getGenderType())
            .startAge(group.getStartAge())
            .endAge(group.getEndAge())
            .location(group.getLocation())
            .date(group.getStartDate().format(DATE_FORMATTER))
            .memberCount(memberCount)
            .maxMembers(group.getCount())
            .hashtag(group.getHashTags())
            .isFavorite(isFavorite)
            .host(host)
            .build();
    }
}
