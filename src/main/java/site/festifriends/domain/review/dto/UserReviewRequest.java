package site.festifriends.domain.review.dto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReviewRequest {

    @Parameter(description = "커서 ID (기본값: 첫번째 요소)")
    private Long cursorId;

    @Parameter(description = "한 페이지당 모임 수 (기본값: 20)")
    private Integer size = 20;
}
