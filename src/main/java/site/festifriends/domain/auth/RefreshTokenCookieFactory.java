package site.festifriends.domain.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenCookieFactory {

    public static ResponseCookie create(String refreshToken) {
        return ResponseCookie
            .from("Refresh-Token", refreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(60 * 60 * 24 * 7)
            .sameSite("Lax")
            .build();
    }
}
