package site.festifriends.domain.performance.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.performance.dto.PerformanceFavoriteRequest;
import site.festifriends.domain.performance.dto.PerformanceFavoriteResponse;
import site.festifriends.domain.performance.dto.PerformanceResponse;
import site.festifriends.domain.performance.dto.PerformanceSearchRequest;
import site.festifriends.domain.performance.dto.PerformanceSearchResponse;
import site.festifriends.domain.performance.service.PerformanceService;
import site.festifriends.domain.review.dto.RecentReviewResponse;
import site.festifriends.domain.review.service.ReviewService;

@RestController
@RequestMapping("/api/v1/performances")
@RequiredArgsConstructor
public class PerformanceController implements PerformanceApi {

    private final PerformanceService performanceService;
    private final ReviewService reviewService;

    @Override
    @GetMapping
    public ResponseEntity<PerformanceSearchResponse> searchPerformances(
        PerformanceSearchRequest request,
        @AuthenticationPrincipal UserDetailsImpl user) {
        Long memberId = user != null ? user.getMemberId() : null;
        PerformanceSearchResponse response = performanceService.searchPerformances(request, memberId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{performanceId}")
    public ResponseEntity<ResponseWrapper<PerformanceResponse>> getPerformanceDetail(
        @PathVariable Long performanceId,
        @AuthenticationPrincipal UserDetailsImpl user) {
        Long memberId = user != null ? user.getMemberId() : null;
        ResponseWrapper<PerformanceResponse> response = performanceService.getPerformanceDetail(performanceId,
            memberId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{performanceId}/favorites")
    public ResponseEntity<ResponseWrapper<PerformanceFavoriteResponse>> togglePerformanceFavorite(
        @PathVariable Long performanceId,
        @Valid @RequestBody PerformanceFavoriteRequest request,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {
        PerformanceFavoriteResponse response = performanceService.togglePerformanceFavorite(
            performanceId, user.getMemberId(), request);

        String message = Boolean.TRUE.equals(request.getIsLiked())
            ? "공연을 찜했습니다."
            : "공연을 찜 취소했습니다.";

        return ResponseEntity.ok(ResponseWrapper.success(message, response));
    }

    @Override
    @GetMapping("/top-favorites")
    public ResponseEntity<ResponseWrapper<List<PerformanceResponse>>> getTopFavoriteUpcomingPerformances(
        @AuthenticationPrincipal UserDetailsImpl user) {
        Long memberId = user != null ? user.getMemberId() : null;
        ResponseWrapper<List<PerformanceResponse>> response = performanceService.getTopFavoriteUpcomingPerformances(
            memberId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/top-groups")
    public ResponseEntity<ResponseWrapper<List<PerformanceResponse>>> getTopGroupsUpcomingPerformances(
        @AuthenticationPrincipal UserDetailsImpl user) {
        Long memberId = user != null ? user.getMemberId() : null;
        ResponseWrapper<List<PerformanceResponse>> response = performanceService.getTopGroupsUpcomingPerformances(
            memberId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/recent-reviews")
    public ResponseEntity<ResponseWrapper<List<RecentReviewResponse>>> getRecentReviews() {
        List<RecentReviewResponse> reviews = reviewService.getRecentReviews();

        return ResponseEntity.ok(
            ResponseWrapper.success("최근 올라온 리뷰 top5 조회 성공", reviews)
        );
    }
} 
