package site.festifriends.domain.group.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.domain.application.repository.ApplicationRepository;
import site.festifriends.domain.group.dto.GroupCreateRequest;
import site.festifriends.domain.group.dto.GroupDetailResponse;
import site.festifriends.domain.group.dto.GroupMemberResponse;
import site.festifriends.domain.group.dto.GroupMembersResponse;
import site.festifriends.domain.group.dto.GroupResponse;
import site.festifriends.domain.group.dto.GroupUpdateRequest;
import site.festifriends.domain.group.dto.PerformanceGroupsData;
import site.festifriends.domain.group.dto.UpdateMemberRoleRequest;
import site.festifriends.domain.group.repository.GroupRepository;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.domain.performance.repository.PerformanceRepository;
import site.festifriends.domain.review.repository.ReviewRepository;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberGroup;
import site.festifriends.entity.Performance;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;
import site.festifriends.entity.enums.Role;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final ApplicationRepository applicationRepository;
    private final PerformanceRepository performanceRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * 모임 개설
     */
    @Transactional
    public void createGroup(GroupCreateRequest request, Long memberId) {
        validateGroupCreateRequest(request);

        Long performanceId = Long.parseLong(request.getPerformanceId());
        Performance performance = performanceRepository.findById(performanceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "존재하지 않는 공연입니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        GroupCategory category = convertCategory(request.getCategory());

        Group group = Group.builder()
            .title(request.getTitle())
            .gatherType(category)
            .genderType(request.getGender())
            .startAge(request.getStartAge())
            .endAge(request.getEndAge())
            .location(request.getLocation())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .count(request.getMaxMembers())
            .introduction(request.getDescription())
            .performance(performance)
            .build();

        if (request.getHashtag() != null && !request.getHashtag().isEmpty()) {
            group.getHashTags().addAll(request.getHashtag());
        }

        Group savedGroup = groupRepository.save(group);

        MemberGroup hostMemberGroup = MemberGroup.builder()
            .member(member)
            .group(savedGroup)
            .role(Role.HOST)
            .status(ApplicationStatus.CONFIRMED)
            .build();

        applicationRepository.save(hostMemberGroup);
    }

    private void validateGroupCreateRequest(GroupCreateRequest request) {
        if (request.getStartAge() > request.getEndAge()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "시작 연령이 종료 연령보다 클 수 없습니다.");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }

        if (request.getStartDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "시작 시간은 현재 시간보다 미래여야 합니다.");
        }

        if (request.getHashtag() != null) {
            for (String tag : request.getHashtag()) {
                if (tag.length() > 20) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "해시태그는 20자 이하로 입력해주세요.");
                }
            }
        }
    }

    private GroupCategory convertCategory(String category) {
        return switch (category) {
            case "같이 동행" -> GroupCategory.COMPANION;
            case "같이 탑승" -> GroupCategory.RIDE_SHARE;
            case "같이 숙박" -> GroupCategory.ROOM_SHARE;
            default -> throw new BusinessException(ErrorCode.BAD_REQUEST, "유효하지 않은 카테고리입니다.");
        };
    }

    /**
     * 공연별 모임 목록 조회 (기존 호환성)
     */
    public PerformanceGroupsData getGroupsByPerformanceId(Long performanceId, int page, int size, Long memberId) {
        return getGroupsByPerformanceId(performanceId, null, null, null, null, null, "date_desc", page, size, memberId);
    }

    /**
     * 공연별 모임 목록 조회 (검색 및 필터 기능 포함)
     */
    public PerformanceGroupsData getGroupsByPerformanceId(Long performanceId,
        GroupCategory category,
        String startDate,
        String endDate,
        String location,
        Gender gender,
        String sort,
        int page,
        int size,
        Long memberId) {
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        try {
            if (startDate != null && !startDate.trim().isEmpty()) {
                startDateTime = LocalDate.parse(startDate).atStartOfDay();
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "날짜 형식이 올바르지 않습니다. yyyy-MM-dd 형식을 사용해주세요.");
        }

        // 정렬 조건 설정 (모임 시작일 기준)
        Sort sortOrder;
        if ("date_asc".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.ASC, "startDate");
        } else {
            sortOrder = Sort.by(Sort.Direction.DESC, "startDate");
        }

        Pageable pageable = PageRequest.of(page - 1, size, sortOrder);

        Performance performance = performanceRepository.findById(performanceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "존재하지 않는 공연입니다."));

        Page<Group> groupPage = groupRepository.findByPerformanceIdWithFilters(
            performanceId, category, startDateTime, endDateTime, location, gender, pageable);

        List<Group> groups = groupPage.getContent();

        groups.forEach(group -> {
            group.getHashTags().size();
        });

        List<Long> groupIds = groups.stream()
            .map(Group::getId)
            .collect(Collectors.toList());

        Map<Long, MemberGroup> hostMap = applicationRepository.findHostsByGroupIds(groupIds);

        Map<Long, Long> memberCountMap = applicationRepository.findConfirmedMemberCountsByGroupIds(groupIds);

        List<Long> hostIds = hostMap.values().stream()
            .map(host -> host.getMember().getId())
            .collect(Collectors.toList());

        // 방장별 평점 조회
        final Map<Long, Double> finalHostRatingMap;
        if (!hostIds.isEmpty()) {
            finalHostRatingMap = reviewRepository.findAverageRatingsByMemberIds(hostIds).stream()
                .collect(Collectors.toMap(
                    result -> (Long) result.get("memberId"),
                    result -> (Double) result.get("avgRating")
                ));
        } else {
            finalHostRatingMap = Collections.emptyMap();
        }

        final Map<Long, Long> finalMemberCountMap = memberCountMap;

        List<GroupResponse> groupResponses = groups.stream()
            .map(group -> convertToGroupResponse(
                group,
                hostMap.get(group.getId()),
                finalMemberCountMap,
                finalHostRatingMap,
                memberId
            ))
            .collect(Collectors.toList());

        Long totalGroupCount = groupRepository.countByPerformanceIdWithFilters(
            performanceId, category, startDateTime, endDateTime, location, gender);

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

    /**
     * 모임원 권한 수정
     */
    @Transactional
    public void updateMemberRole(Long groupId, Long targetMemberId, UpdateMemberRoleRequest request, Long hostId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        if (!applicationRepository.isGroupHost(groupId, hostId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "권한이 없습니다. 모임장만 권한을 수정할 수 있습니다.");
        }

        MemberGroup targetMemberGroup = applicationRepository.findByGroupIdAndMemberId(groupId, targetMemberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임 또는 모임원을 찾을 수 없습니다."));

        if (hostId.equals(targetMemberId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "본인의 권한은 수정할 수 없습니다.");
        }

        Role newRole = request.getRole();

        if (newRole == Role.HOST) {
            MemberGroup currentHost = applicationRepository.findByGroupIdAndRole(groupId, Role.HOST)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "현재 모임장을 찾을 수 없습니다."));

            currentHost.changeRole(Role.MEMBER);
            targetMemberGroup.changeRole(Role.HOST);
        } else if (newRole == Role.MEMBER) {
            if (targetMemberGroup.getRole() == Role.HOST) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "호스트는 다른 사람을 호스트로 지정한 후에만 권한을 변경할 수 있습니다.");
            }
            targetMemberGroup.changeRole(Role.MEMBER);
        }
    }

    /**
     * 모임 탈퇴
     */
    @Transactional
    public void leaveGroup(Long groupId, Long memberId) {
        // 모임 존재 여부 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        // 탈퇴하려는 멤버가 해당 모임에 참가했는지 확인
        MemberGroup memberGroup = applicationRepository.findByGroupIdAndMemberId(groupId, memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "해당 모임에 참가하지 않았습니다."));

        // 현재 모임의 전체 멤버 수 조회
        int totalMemberCount = applicationRepository.countGroupMembers(groupId);

        // 모임장인 경우 탈퇴 조건 검증
        if (memberGroup.getRole() == Role.HOST) {
            if (totalMemberCount > 1) {
                throw new BusinessException(ErrorCode.BAD_REQUEST,
                    "모임장은 다른 멤버가 있을 때 탈퇴할 수 없습니다. 먼저 모임장을 위임한 후 탈퇴해주세요.");
            }

            // 모임장이 혼자 있는 경우 - 모임 삭제 (soft delete)
            if (totalMemberCount == 1) {
                // 멤버 그룹 삭제 (hard delete)
                applicationRepository.delete(memberGroup);

                // 모임 삭제 (soft delete)
                group.delete();
                return;
            }
        }

        // 일반 멤버 탈퇴 또는 조건을 만족하는 모임장 탈퇴
        applicationRepository.delete(memberGroup);

        // 탈퇴 후 모임에 아무도 없으면 모임 삭제 (이론적으로는 위에서 처리되지만 안전장치)
        int remainingMemberCount = applicationRepository.countGroupMembers(groupId);
        if (remainingMemberCount == 0) {
            group.delete();
        }
    }

    /**
     * 모임원 퇴출
     */
    @Transactional
    public void kickMember(Long groupId, Long targetMemberId, Long hostId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        if (!applicationRepository.isGroupHost(groupId, hostId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "권한이 없습니다. 모임장만 모임원을 퇴출할 수 있습니다.");
        }

        if (hostId.equals(targetMemberId)) {
            throw new BusinessException(ErrorCode.CONFLICT, "모임장은 자신을 퇴출시킬 수 없습니다.");
        }

        // 퇴출 대상 모임원 존재 여부 확인
        MemberGroup targetMemberGroup = applicationRepository.findByGroupIdAndMemberId(groupId, targetMemberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임 또는 모임원을 찾을 수 없습니다."));

        if (targetMemberGroup.getRole() == Role.HOST) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "호스트는 퇴출시킬 수 없습니다.");
        }

        // 멤버 퇴출 (hard delete)
        applicationRepository.delete(targetMemberGroup);
    }

    private GroupResponse convertToGroupResponse(Group group, MemberGroup hostMemberGroup,
        Map<Long, Long> memberCountMap, Map<Long, Double> hostRatingMap, Long memberId) {
        // 호스트 정보 설정
        GroupResponse.Host host = null;
        boolean isHost = false;

        if (hostMemberGroup != null) {
            Long hostId = hostMemberGroup.getMember().getId();
            Double hostRating = hostRatingMap.getOrDefault(hostId, 0.0);

            isHost = memberId != null && memberId.equals(hostId);

            host = GroupResponse.Host.builder()
                .hostId(hostId.toString())
                .name(hostMemberGroup.getMember().getNickname())
                .rating(hostRating)
                .profileImage(hostMemberGroup.getMember().getProfileImageUrl())
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
            .isHost(isHost)
            .host(host)
            .build();
    }
}
