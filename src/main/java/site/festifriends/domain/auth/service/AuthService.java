package site.festifriends.domain.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.common.jwt.AccessTokenProvider;
import site.festifriends.common.jwt.RefreshTokenProvider;
import site.festifriends.domain.auth.AuthInfo;
import site.festifriends.domain.auth.KakaoOAuthProvider;
import site.festifriends.domain.auth.KakaoUserInfo;
import site.festifriends.domain.member.service.MemberService;
import site.festifriends.entity.Member;
import site.festifriends.entity.enums.Gender;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final KakaoOAuthProvider kakaoOAuthProvider;
    private final MemberService memberService;

    public String getAuthorizationUrl() {
        return kakaoOAuthProvider.getAuthorizationUrl();
    }

    public AuthInfo handleOAuthCallback(String code) {
        JsonNode attributes = kakaoOAuthProvider.getUserInfo(code);
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);

        Member member = memberService.loginOrSignUp(kakaoUserInfo);

        String accessToken = accessTokenProvider.generateToken(member.getId());
        String refreshToken = refreshTokenProvider.generateToken(member.getId());

        memberService.saveRefreshToken(member, refreshToken);

        return new AuthInfo(accessToken, refreshToken, member.getGender() == Gender.ALL);
    }
}
