package site.festifriends.domain.application.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberGroup;
import site.festifriends.entity.enums.Role;

public interface ApplicationRepositoryCustom {

    Slice<MemberGroup> findApplicationsWithSlice(Long hostId, Long cursorId, Pageable pageable);

    Slice<Group> findUnstartedGroupsWithPendingApplicationsSlice(Long hostId, Long cursorId, Pageable pageable);

    List<MemberGroup> findPendingApplicationsByGroupIds(List<Long> groupIds);

    Map<Long, Long> findConfirmedMemberCountsByGroupIds(List<Long> groupIds);

    Slice<MemberGroup> findAppliedApplicationsWithSlice(Long memberId, Long cursorId, Pageable pageable);

    Slice<MemberGroup> findJoinedGroupsWithSlice(Long memberId, Long cursorId, Pageable pageable);

    Map<Long, MemberGroup> findHostsByGroupIds(List<Long> groupIds);

    boolean existsByGroupIdAndMemberIdAndRole(Long groupId, Long memberId, Role role);

    boolean existsByMemberIdAndGroupId(Long memberId, Long groupId);

    boolean isGroupHost(Long groupId, Long memberId);

    boolean isGroupParticipant(Long groupId, Long memberId);

    Slice<MemberGroup> findGroupMembersWithSlice(Long groupId, Long cursorId, Pageable pageable);

    int countGroupMembers(Long groupId);

    Optional<MemberGroup> findByGroupIdAndMemberId(Long groupId, Long memberId);

    Optional<MemberGroup> findByGroupIdAndRole(Long groupId, Role role);

    Optional<Member> findHostByGroupId(Long groupId);

    List<Member> findMembersByGroupId(Long groupId);
}