package site.festifriends.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "작성 가능한 리뷰 목록 조회 요청")
public class WritableReviewRequest {

    @Schema(description = "커서 ID (기본값: 첫번째 요소)", example = "1234")
    private Long cursorId;

    @Schema(description = "한 페이지당 모임 수 (기본값: 20)", example = "20")
    private Integer size;

    public Long getCursorId() {
        return cursorId;
    }

    public Integer getSize() {
        return size;
    }
}
