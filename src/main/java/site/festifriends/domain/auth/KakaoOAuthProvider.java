package site.festifriends.domain.auth;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KakaoOAuthProvider {

    @Qualifier("oAuthRestClient")
    private final RestClient restClient;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    private static final String AUTHORIZE_URL = "https://kauth.kakao.com/oauth/authorize";
    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    public String getAuthorizationUrl() {
        return UriComponentsBuilder
            .fromUriString(AUTHORIZE_URL)
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .build()
            .toUriString();
    }

    public JsonNode getToken(String code) {
        final String uri =
            UriComponentsBuilder
                .fromUriString(TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code", code)
                .build()
                .toUriString();

        return restClient
            .post()
            .uri(uri)
            .retrieve()
            .body(JsonNode.class);
    }

    public JsonNode getUserInfo(String code) {
        JsonNode token = getToken(code);
        return getUserInfoFromKakao(token.get("access_token").asText());
    }

    private JsonNode getUserInfoFromKakao(String accessToken) {
        return restClient
            .get()
            .uri(USER_INFO_URL)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .body(JsonNode.class);
    }

}
