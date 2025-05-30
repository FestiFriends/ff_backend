package site.festifriends.domain.member.repository;

import org.springframework.data.domain.Pageable;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.member.dto.MemberDto;

public interface MemberRepositoryCustom {

    CursorResponseWrapper<MemberDto> getMyLikedMembers(Long memberId, Long cursorId, Pageable pageable);

}
