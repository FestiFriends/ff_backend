package site.festifriends.domain.performance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformanceFavoriteResponse {

    private String performanceId;
    private Boolean isLiked;

    public static PerformanceFavoriteResponse of(Long performanceId, Boolean isLiked) {
        return PerformanceFavoriteResponse.builder()
            .performanceId(performanceId.toString())
            .isLiked(isLiked)
            .build();
    }
}
