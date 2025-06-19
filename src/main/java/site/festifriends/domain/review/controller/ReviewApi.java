package site.festifriends.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.review.dto.CreateReviewRequest;
import site.festifriends.domain.review.dto.UserReviewRequest;
import site.festifriends.domain.review.dto.UserReviewResponse;
import site.festifriends.domain.review.dto.WritableReviewRequest;
import site.festifriends.domain.review.dto.WritableReviewResponse;
import site.festifriends.domain.review.dto.WrittenReviewRequest;
import site.festifriends.domain.review.dto.WrittenReviewResponse;

import java.util.List;

@Tag(name = "Review", description = "리뷰 관련 API")
public interface ReviewApi {

    @Operation(
        summary = "사용자 리뷰 조회",
        description = "특정 사용자가 받은 리뷰와 평가를 커서 기반 페이지네이션으로 조회합니다. 회원이면 누구나 조회할 수 있습니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공적으로 데이터를 불러왔습니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
        }
    )
    ResponseEntity<CursorResponseWrapper<UserReviewResponse>> getUserReviews(
        @Parameter(description = "조회할 사용자 ID", required = true)
        Long userId,
        UserReviewRequest request
    );

    @Operation(
        summary = "내가 작성한 리뷰 조회",
        description = "본인이 작성한 리뷰 목록을 모임별로 그룹화하여 커서 기반 페이지네이션으로 조회합니다. size 파라미터는 조회할 모임의 개수를 의미하며, 각 모임에서는 해당 모임의 모든 리뷰를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "내가 작성한 리뷰 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증이 필요합니다.")
        }
    )
    ResponseEntity<CursorResponseWrapper<WrittenReviewResponse>> getWrittenReviews(
        UserDetailsImpl userDetails,
        WrittenReviewRequest request
    );

    @Operation(
        summary = "리뷰 작성",
        description = "모임에 참여한 사용자에 대한 리뷰를 작성합니다. 모임 종료 후에만 작성 가능합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성이 완료되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "401", description = "인증이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "모임 또는 사용자를 찾을 수 없습니다.")
        }
    )
    ResponseEntity<ResponseWrapper<Void>> createReview(
        UserDetailsImpl userDetails,
        @RequestBody @Valid CreateReviewRequest request
    );

    @Operation(
        summary = "작성 가능한 리뷰 목록 조회",
        description = "로그인한 사용자가 참가한 모임 중에서 아직 리뷰를 남기지 않은 사용자들의 목록을 모임별로 그룹화하여 조회합니다. size 파라미터는 조회할 모임의 개수를 의미하며, 각 모임에서는 리뷰 작성 가능한 모든 사용자를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "작성 가능한 리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증이 필요합니다.")
        }
    )
    ResponseEntity<CursorResponseWrapper<WritableReviewResponse>> getWritableReviews(
        UserDetailsImpl userDetails,
        WritableReviewRequest request
    );
}
