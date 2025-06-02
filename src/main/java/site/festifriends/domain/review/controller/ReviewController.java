package site.festifriends.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.review.dto.UserReviewResponse;
import site.festifriends.domain.review.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController implements ReviewApi {

    private final ReviewService reviewService;

    @Override
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseWrapper<List<UserReviewResponse>>> getUserReviews(
            @PathVariable Long userId) {
        
        List<UserReviewResponse> reviews = reviewService.getUserReviews(userId);
        
        return ResponseEntity.ok(
                ResponseWrapper.success("성공적으로 데이터를 불러왔습니다.", reviews)
        );
    }
}
