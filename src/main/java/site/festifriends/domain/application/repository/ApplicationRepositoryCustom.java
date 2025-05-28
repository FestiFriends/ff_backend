package site.festifriends.domain.application.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.entity.MemberParty;

public interface ApplicationRepositoryCustom {

    Slice<MemberParty> findApplicationsWithSlice(Long hostId, Long cursorId, Pageable pageable);
} 