package site.festifriends.domain.application.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.entity.MemberParty;
import site.festifriends.entity.enums.Role;

import java.util.List;
import java.util.Map;

public interface ApplicationRepositoryCustom {

    Slice<MemberParty> findApplicationsWithSlice(Long hostId, Long cursorId, Pageable pageable);
    
    Slice<MemberParty> findAppliedApplicationsWithSlice(Long memberId, Long cursorId, Pageable pageable);
    
    Slice<MemberParty> findJoinedGroupsWithSlice(Long memberId, Long cursorId, Pageable pageable);
    
    Map<Long, MemberParty> findHostsByPartyIds(List<Long> partyIds);
    
    Map<Long, Long> findConfirmedMemberCountsByPartyIds(List<Long> partyIds);
    
    boolean existsByPartyIdAndMemberIdAndRole(Long partyId, Long memberId, Role role);
} 