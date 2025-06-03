package site.festifriends.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import site.festifriends.entity.enums.ReviewTag;

import java.util.List;

@Getter
@Setter
@Schema(description = "리뷰 작성 요청")
public class CreateReviewRequest {

    @NotBlank(message = "모임 ID는 필수입니다.")
    @Schema(description = "모임 ID", example = "3")
    private String groupId;

    @NotBlank(message = "리뷰 대상 사용자 ID는 필수입니다.")
    @Schema(description = "리뷰 대상 사용자 ID", example = "1")
    private String targetUserId;

    @NotNull(message = "별점은 필수입니다.")
    @DecimalMin(value = "1.0", message = "별점은 1.0 이상이어야 합니다.")
    @DecimalMax(value = "5.0", message = "별점은 5.0 이하여야 합니다.")
    @Schema(description = "별점 (1~5, 0.5단위)", example = "4.5")
    private Double rating;

    @Schema(description = "직접 작성한 리뷰 내용", example = "시간 약속 잘 지키고 대화도 편했어요!")
    private String content;

    @Schema(description = "선택한 기본 문구 리스트", example = "[\"PUNCTUAL\", \"COMMUNICATIVE\", \"RECOMMEND\"]")
    private List<ReviewTag> defaultTag;
}
