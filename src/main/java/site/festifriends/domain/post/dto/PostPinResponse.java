package site.festifriends.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PostPinResponse extends PostUpdateDeleteResponse {

    @JsonProperty("isPinned")
    private boolean isPinned;

    public boolean isPinned() {
        return isPinned;
    }

    /**
     * 성공 응답 생성
     */
    public static PostPinResponse success(boolean isPinned) {
        PostPinResponse response = new PostPinResponse();
        response.setResult(true);
        response.setPinned(isPinned);
        return response;
    }
}
