package site.festifriends.domain.member.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.domain.member.dto.LikedMemberDto;
import site.festifriends.domain.member.dto.LikedPerformanceDto;

public interface MemberRepositoryCustom {

    Slice<LikedMemberDto> getMyLikedMembers(Long memberId, Long cursorId, Pageable pageable);

    Long countMyLikedMembers(Long memberId);

    Slice<LikedPerformanceDto> getMyLikedPerformances(Long memberId, Long cursorId, Pageable pageable);

    Object[] getMemberProfile(Long targetId);

    Object[] getMemberExtraData(Long memberId, Long targetId);
}
