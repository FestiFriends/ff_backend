package site.festifriends.domain.application.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.entity.MemberParty;
import site.festifriends.entity.enums.Role;

public interface ApplicationRepositoryCustom {

    Slice<MemberParty> findApplicationsWithSlice(Long hostId, Long cursorId, Pageable pageable);
    
    boolean existsByPartyIdAndMemberIdAndRole(Long partyId, Long memberId, Role role);
} 