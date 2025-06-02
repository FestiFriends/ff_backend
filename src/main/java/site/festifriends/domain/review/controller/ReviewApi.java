package site.festifriends.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.review.dto.UserReviewResponse;

import java.util.List;

@Tag(name = "Review", description = "리뷰 관련 API")
public interface ReviewApi {

    @Operation(
            summary = "사용자 리뷰 조회",
            description = "특정 사용자가 받은 리뷰와 평가를 조회합니다. 회원이면 누구나 조회할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 데이터를 불러왔습니다."),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
            }
    )
    ResponseEntity<ResponseWrapper<List<UserReviewResponse>>> getUserReviews(
            @Parameter(description = "조회할 사용자 ID", required = true)
            Long userId
    );
}
