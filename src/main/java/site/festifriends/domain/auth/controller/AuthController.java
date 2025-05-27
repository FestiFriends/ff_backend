package site.festifriends.domain.auth.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.ResponseWrapper;
import site.festifriends.domain.auth.AuthInfo;
import site.festifriends.domain.auth.AuthResponse;
import site.festifriends.domain.auth.RefreshTokenCookieFactory;
import site.festifriends.domain.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    @GetMapping("/signup/kakao")
    public ResponseEntity<?> login() {
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(authService.getAuthorizationUrl()))
            .build();
    }

    @Override
    @GetMapping("/callback/kakao")
    public ResponseEntity<?> handleCallback(@RequestParam String code) {
        AuthInfo info = authService.handleOAuthCallback(code);

        HttpHeaders headers = new HttpHeaders();

        headers.add("Set-Cookie", RefreshTokenCookieFactory.create(info.getRefreshToken()).toString());

        return ResponseEntity.ok()
            .headers(headers)
            .body(ResponseWrapper.success(
                HttpStatus.OK,
                "로그인에 성공했습니다.",
                new AuthResponse(info.getAccessToken(), info.getIsNewUser())
            ));
    }
}
