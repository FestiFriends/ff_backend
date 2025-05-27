package site.festifriends.domain.auth;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class KakaoUserInfo {

    private String socialId;
    private String name;
    private String email;
    private String profileImage;

    public KakaoUserInfo(JsonNode userInfo) {
        this.socialId = userInfo.get("id").asText();
        this.name = userInfo.get("properties").get("nickname").asText();
        this.email = userInfo.get("kakao_account").get("email").asText();
        this.profileImage = userInfo.get("properties").get("profile_image").asText();
    }
}
