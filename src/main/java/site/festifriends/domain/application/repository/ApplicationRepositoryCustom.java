package site.festifriends.domain.application.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.entity.MemberGroup;
import site.festifriends.entity.enums.Role;

import java.util.List;
import java.util.Map;

public interface ApplicationRepositoryCustom {

    Slice<MemberGroup> findApplicationsWithSlice(Long hostId, Long cursorId, Pageable pageable);
    
    Slice<MemberGroup> findAppliedApplicationsWithSlice(Long memberId, Long cursorId, Pageable pageable);
    
    Slice<MemberGroup> findJoinedGroupsWithSlice(Long memberId, Long cursorId, Pageable pageable);
    
    Map<Long, MemberGroup> findHostsByGroupIds(List<Long> groupIds);
    
    Map<Long, Long> findConfirmedMemberCountsByGroupIds(List<Long> groupIds);
    
    boolean existsByGroupIdAndMemberIdAndRole(Long groupId, Long memberId, Role role);
} 