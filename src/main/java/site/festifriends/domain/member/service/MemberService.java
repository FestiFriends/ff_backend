package site.festifriends.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.common.jwt.TokenResolver;
import site.festifriends.domain.auth.KakaoUserInfo;
import site.festifriends.domain.auth.service.BlackListTokenService;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.entity.Member;
import site.festifriends.entity.enums.Gender;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BlackListTokenService blackListTokenService;

    public Member loginOrSignUp(KakaoUserInfo userInfo) {

        return memberRepository.findBySocialId(userInfo.getSocialId())
            .orElseGet(() -> {
                Member newMember = Member.builder()
                    .socialId(userInfo.getSocialId())
                    .nickname(userInfo.getName())
                    .email(userInfo.getEmail())
                    .profileImageUrl(userInfo.getProfileImage())
                    .age(0)
                    .gender(Gender.ALL)
                    .introduction("")
                    .build();

                return memberRepository.save(newMember);
            });
    }

    public void saveRefreshToken(Long memberId, String refreshToken) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다"));
        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);
    }

    public void deleteMember(Long memberId, HttpServletRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다"));

        memberRepository.deleteMember(member);

        String accessToken = TokenResolver.extractAccessToken(request);
        String refreshToken = TokenResolver.extractRefreshToken(request);

        blackListTokenService.addBlackListToken(accessToken);
        blackListTokenService.addBlackListToken(refreshToken);
    }
}
