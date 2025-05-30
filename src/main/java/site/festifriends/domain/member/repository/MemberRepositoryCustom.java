package site.festifriends.domain.member.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.domain.member.dto.LikedMemberDto;

public interface MemberRepositoryCustom {

    Slice<LikedMemberDto> getMyLikedMembers(Long memberId, Long cursorId, Pageable pageable);

    Long countMyLikedMembers(Long memberId);
}
