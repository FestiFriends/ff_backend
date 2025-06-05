package site.festifriends.domain.post.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostReactionRequest {

    @NotNull(message = "반응 여부는 필수입니다.")
    private Boolean hasReactioned;

    public PostReactionRequest(Boolean hasReactioned) {
        this.hasReactioned = hasReactioned;
    }
}
