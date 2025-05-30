package site.festifriends.domain.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    @JsonProperty("isNewUser")
    private Boolean isNewUser;
}
