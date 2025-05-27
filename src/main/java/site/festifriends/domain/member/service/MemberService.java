package site.festifriends.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.domain.auth.KakaoUserInfo;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.entity.Member;
import site.festifriends.entity.enums.Gender;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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

    public void saveRefreshToken(Member member, String refreshToken) {
        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);
    }
}
