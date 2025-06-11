package site.festifriends.domain.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
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

    public AuthInfo handleDevOAuthCallback(String code) {
        JsonNode attributes = kakaoOAuthProvider.getDevUserInfo(code);
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);

        Member member = memberService.loginOrSignUp(kakaoUserInfo);

        String accessToken = accessTokenProvider.generateToken(member.getId());
        String refreshToken = refreshTokenProvider.generateToken(member.getId());

        memberService.saveRefreshToken(member.getId(), refreshToken);

        return new AuthInfo(accessToken, refreshToken, member.getGender() == Gender.ALL);
    }

    public AuthInfo reissueAccessToken(HttpServletRequest request) {
        String refreshToken = TokenResolver.extractRefreshToken(request);

        validateRefreshToken(refreshToken);

        blackListTokenService.addBlackListToken(refreshToken);

        Long memberId = Long.valueOf(refreshTokenProvider.getSubject(refreshToken));
        String newAccessToken = accessTokenProvider.generateToken(memberId);
        String newRefreshToken = refreshTokenProvider.generateToken(memberId);

        memberService.saveRefreshToken(memberId, newRefreshToken);

        return new AuthInfo(newAccessToken, newRefreshToken, false);
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "리프레시 토큰이 존재하지 않습니다..");
        }
        if (!refreshTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");
        }

        if (blackListTokenService.isBlackListed(refreshToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "금지된 리프레시 토큰입니다.");
        }
    }

    public void logout(Long memberId, HttpServletRequest request) {
        String accessToken = TokenResolver.extractAccessToken(request);
        String refreshToken = TokenResolver.extractRefreshToken(request);

        if (!accessTokenProvider.getSubject(accessToken).equals(memberId.toString()) ||
            !refreshTokenProvider.getSubject(refreshToken).equals(memberId.toString())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "권한이 없습니다.");
        }

        blackListTokenService.addBlackListToken(accessToken);
        blackListTokenService.addBlackListToken(refreshToken);
    }

    public String getDevAuthorizationUrl() {
        return kakaoOAuthProvider.getDevAuthorizationUrl();
    }
}
