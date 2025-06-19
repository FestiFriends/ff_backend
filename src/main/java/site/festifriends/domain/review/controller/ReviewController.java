package site.festifriends.domain.review.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
import site.festifriends.domain.review.service.ReviewService;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController implements ReviewApi {

    private final ReviewService reviewService;

    @Override
    @GetMapping("/{userId}")
    public ResponseEntity<CursorResponseWrapper<UserReviewResponse>> getUserReviews(
        @PathVariable Long userId,
        UserReviewRequest request
    ) {
        CursorResponseWrapper<UserReviewResponse> response = reviewService.getUserReviews(userId, request);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/written")
    public ResponseEntity<CursorResponseWrapper<WrittenReviewResponse>> getWrittenReviews
        (
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            WrittenReviewRequest request
        ) {
        Long userId = userDetails.getMemberId();
        CursorResponseWrapper<WrittenReviewResponse> response = reviewService.getWrittenReviews(userId, request);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    public ResponseEntity<ResponseWrapper<Void>> createReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody @Valid CreateReviewRequest request
    ) {
        Long reviewerId = userDetails.getMemberId();
        reviewService.createReview(reviewerId, request);

        return ResponseEntity.ok(
            ResponseWrapper.success("리뷰 작성이 완료되었습니다.")
        );
    }

    @Override
    @GetMapping("/writable")
    public ResponseEntity<CursorResponseWrapper<WritableReviewResponse>> getWritableReviews(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        WritableReviewRequest request
    ) {
        Long userId = userDetails.getMemberId();
        CursorResponseWrapper<WritableReviewResponse> response = reviewService.getWritableReviews(userId, request);

        return ResponseEntity.ok(response);
    }
}
