package site.festifriends.domain.performance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PerformanceFavoriteRequest {

    private Boolean isLiked;

    public PerformanceFavoriteRequest(Boolean isLiked) {
        this.isLiked = isLiked;
    }
}
