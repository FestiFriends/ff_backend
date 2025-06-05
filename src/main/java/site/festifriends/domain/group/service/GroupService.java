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
import site.festifriends.domain.group.dto.GroupDetailResponse;
import site.festifriends.domain.group.dto.GroupMemberResponse;
import site.festifriends.domain.group.dto.GroupMembersResponse;
import site.festifriends.domain.group.dto.GroupResponse;
import site.festifriends.domain.group.dto.GroupUpdateRequest;
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

    /**
     * 모임 기본 정보 조회
     */
    public GroupDetailResponse getGroupDetail(Long groupId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        // 해시태그 로딩
        group.getHashTags().size();

        // 공연 정보 로딩
        Performance performance = group.getPerformance();

        // 방장 정보 조회
        MemberGroup hostMemberGroup = applicationRepository.findHostsByGroupIds(
                Collections.singletonList(groupId))
            .get(groupId);

        // 현재 멤버 수 조회
        Map<Long, Long> memberCountMap = applicationRepository.findConfirmedMemberCountsByGroupIds(
            Collections.singletonList(groupId));
        int currentMemberCount = memberCountMap.getOrDefault(groupId, 0L).intValue();

        // 방장 평점 조회
        Double hostRating = 0.0;
        GroupDetailResponse.Host host = null;

        if (hostMemberGroup != null) {
            Long hostId = hostMemberGroup.getMember().getId();
            List<Map<String, Object>> ratingResults = reviewRepository.findAverageRatingsByMemberIds(
                Collections.singletonList(hostId));

            if (!ratingResults.isEmpty()) {
                hostRating = (Double) ratingResults.get(0).get("avgRating");
            }

            host = GroupDetailResponse.Host.builder()
                .id(hostId.toString())
                .name(hostMemberGroup.getMember().getNickname())
                .rating(hostRating)
                .build();
        }

        // 공연 정보 구성
        GroupDetailResponse.Performance performanceInfo = GroupDetailResponse.Performance.builder()
            .id(performance.getId().toString())
            .title(performance.getTitle())
            .poster(performance.getPoster())
            .build();

        return GroupDetailResponse.builder()
            .id(group.getId().toString())
            .performance(performanceInfo)
            .title(group.getTitle())
            .category(group.getGatherType().getDescription())
            .gender(group.getGenderType().getDescription())
            .startAge(group.getStartAge())
            .endAge(group.getEndAge())
            .location(group.getLocation())
            .startDate(group.getStartDate().format(DATE_FORMATTER))
            .endDate(group.getEndDate().format(DATE_FORMATTER))
            .memberCount(currentMemberCount)
            .maxMembers(group.getCount())
            .description(group.getIntroduction())
            .hashtag(group.getHashTags())
            .host(host)
            .build();
    }

    /**
     * 모임 기본 정보 수정
     */
    @Transactional
    public void updateGroup(Long groupId, GroupUpdateRequest request, Long memberId) {
        // 모임 존재 여부 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        // 방장 권한 확인
        if (!applicationRepository.isGroupHost(groupId, memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "모임을 수정할 권한이 없습니다.");
        }

        // 날짜 유효성 검증
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "시작 날짜가 종료 날짜보다 늦을 수 없습니다.");
        }

        // 연령 유효성 검증
        if (request.getStartAge() > request.getEndAge()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "시작 연령이 종료 연령보다 클 수 없습니다.");
        }

        // 현재 참여 인원 확인
        Map<Long, Long> memberCountMap = applicationRepository.findConfirmedMemberCountsByGroupIds(
            Collections.singletonList(groupId));
        int currentMemberCount = memberCountMap.getOrDefault(groupId, 0L).intValue();

        // 최대 인원수가 현재 참여 인원보다 적은지 확인
        if (request.getMaxMembers() < currentMemberCount) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                "최대 인원수는 현재 참여 인원(" + currentMemberCount + "명)보다 적을 수 없습니다.");
        }

        // 모임 정보 수정
        group.updateGroupInfo(
            request.getTitle(),
            request.getCategoryEnum(),
            request.getGenderEnum(),
            request.getStartAge(),
            request.getEndAge(),
            request.getLocation(),
            request.getStartDate(),
            request.getEndDate(),
            request.getMaxMembers(),
            request.getDescription(),
            request.getHashtag()
        );
    }

    /**
     * 모임원 목록 조회
     */
    public GroupMembersResponse getGroupMembers(Long groupId, Long cursorId, int size, Long memberId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        // 요청자가 모임에 참가했는지 확인 (확정된 멤버이거나 방장이어야 함)
        if (!applicationRepository.isGroupParticipant(groupId, memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "모임에 참가한 사용자만 모임원 목록을 조회할 수 있습니다.");
        }

        Pageable pageable = PageRequest.of(0, size);
        var memberSlice = applicationRepository.findGroupMembersWithSlice(groupId, cursorId, pageable);

        List<MemberGroup> members = memberSlice.getContent();

        List<GroupMemberResponse> memberResponses = members.stream()
            .map(memberGroup -> GroupMemberResponse.builder()
                .memberId(memberGroup.getMember().getId().toString())
                .name(memberGroup.getMember().getNickname())
                .profileImage(memberGroup.getMember().getProfileImageUrl())
                .role(memberGroup.getRole())
                .build())
            .collect(Collectors.toList());

        // 전체 모임원 수 조회
        int totalMemberCount = applicationRepository.countGroupMembers(groupId);

        Long nextCursorId = null;
        if (memberSlice.hasNext() && !members.isEmpty()) {
            MemberGroup lastMember = members.get(members.size() - 1);
            nextCursorId = lastMember.getId();
        }

        return GroupMembersResponse.builder()
            .groupId(group.getId().toString())
            .performanceId(group.getPerformance().getId().toString())
            .memberCount(totalMemberCount)
            .members(memberResponses)
            .cursorId(nextCursorId)
            .hasNext(memberSlice.hasNext())
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
            .id(group.getId().toString())
            .title(group.getTitle())
            .category(group.getGatherType())
            .gender(group.getGenderType())
            .startAge(group.getStartAge())
            .endAge(group.getEndAge())
            .location(group.getLocation())
            .startDate(group.getStartDate().format(DATE_FORMATTER))
            .endDate(group.getEndDate().format(DATE_FORMATTER))
            .memberCount(memberCount)
            .maxMembers(group.getCount())
            .hashtag(group.getHashTags())
            .isFavorite(isFavorite)
            .host(host)
            .build();
    }
}
