package site.festifriends.domain.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthInfo {

    private final String accessToken;
    private final String refreshToken;

    @JsonProperty("isNewUser")
    private final Boolean isNewUser;

}
