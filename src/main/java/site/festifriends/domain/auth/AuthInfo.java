package site.festifriends.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthInfo {

    private final String accessToken;
    private final String refreshToken;
    private final Boolean isNewUser;
    
}
