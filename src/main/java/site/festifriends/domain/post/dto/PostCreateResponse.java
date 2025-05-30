package site.festifriends.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateResponse {

    private boolean result;

    public static PostCreateResponse success() {
        return PostCreateResponse.builder()
                .result(true)
                .build();
    }

    public static PostCreateResponse fail() {
        return PostCreateResponse.builder()
                .result(false)
                .build();
    }
}
