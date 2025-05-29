package site.festifriends.domain.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.common.jwt.AccessTokenProvider;
import site.festifriends.common.jwt.RefreshTokenProvider;
import site.festifriends.common.jwt.TokenResolver;
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
    private final BlackListTokenService blackListTokenService;

    public String getAuthorizationUrl() {
        return kakaoOAuthProvider.getAuthorizationUrl();
    }

    public AuthInfo handleOAuthCallback(String code) {
        JsonNode attributes = kakaoOAuthProvider.getUserInfo(code);
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);

        Member member = memberService.loginOrSignUp(kakaoUserInfo);

        String accessToken = accessTokenProvider.generateToken(member.getId());
        String refreshToken = refreshTokenProvider.generateToken(member.getId());

        memberService.saveRefreshToken(member.getId(), refreshToken);

        return new AuthInfo(accessToken, refreshToken, member.getGender() == Gender.ALL);
    }

    public AuthInfo reissueAccessToken(Long memberId, HttpServletRequest request) {
        String refreshToken = TokenResolver.extractRefreshToken(request);

        validateRefreshToken(refreshToken, memberId);

        blackListTokenService.addBlackListToken(refreshToken);

        String newAccessToken = accessTokenProvider.generateToken(memberId);
        String newRefreshToken = refreshTokenProvider.generateToken(memberId);

        memberService.saveRefreshToken(memberId, newRefreshToken);

        return new AuthInfo(newAccessToken, newRefreshToken, false);
    }

    private void validateRefreshToken(String refreshToken, Long memberId) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token is missing.");
        }
        if (!refreshTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token.");
        }
        if (!refreshTokenProvider.getSubject(refreshToken).equals(memberId.toString())) {
            throw new IllegalArgumentException("Refresh token does not match the user ID.");
        }
        if (blackListTokenService.isBlackListed(refreshToken)) {
            throw new IllegalArgumentException("Refresh token is blacklisted.");
        }
    }

    public void logout(Long memberId, HttpServletRequest request) {
        String accessToken = TokenResolver.extractAccessToken(request);
        String refreshToken = TokenResolver.extractRefreshToken(request);

        if (!accessTokenProvider.getSubject(accessToken).equals(memberId.toString()) ||
            !refreshTokenProvider.getSubject(refreshToken).equals(memberId.toString())) {
            throw new IllegalArgumentException("Access token or refresh token does not match the user ID.");
        }

        blackListTokenService.addBlackListToken(accessToken);
        blackListTokenService.addBlackListToken(refreshToken);
    }
}
