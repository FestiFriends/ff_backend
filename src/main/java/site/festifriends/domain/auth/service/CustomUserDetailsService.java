package site.festifriends.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.entity.Member;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final MemberRepository memberRepository;

    public UserDetailsImpl loadUserById(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다 : " + memberId));

        if (member.getSuspendedAt() != null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 회원은 정지된 상태입니다.");
        }

        return UserDetailsImpl.of(member);
    }
}
