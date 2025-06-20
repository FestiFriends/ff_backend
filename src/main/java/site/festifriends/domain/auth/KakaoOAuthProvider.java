package site.festifriends.domain.auth;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoOAuthProvider {

    @Qualifier("oAuthRestClient")
    private final RestClient restClient;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.kakao.admin-key}")
    private String adminKey;

    @Value("${oauth.kakao.dev.redirect-uri}")
    private String devRedirectUri;

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

    public JsonNode getDevToken(String code) {
        final String uri =
            UriComponentsBuilder
                .fromUriString(TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", devRedirectUri)
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

    public JsonNode getDevUserInfo(String code) {
        JsonNode token = getDevToken(code);
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

    public String getDevAuthorizationUrl() {
        return UriComponentsBuilder
            .fromUriString(AUTHORIZE_URL)
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", devRedirectUri)
            .build()
            .toUriString();
    }

    public boolean unlinkKakaoAccount(String socialId) {
        String uri = "https://kapi.kakao.com/v1/user/unlink";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("target_id_type", "user_id");
        formData.add("target_id", socialId);

        try {
            restClient
                .post()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(String.class);
            return true;
        } catch (Exception e) {
            log.info("Failed to unlink Kakao account: {}", e.getMessage());
            return false;
        }
    }
}
