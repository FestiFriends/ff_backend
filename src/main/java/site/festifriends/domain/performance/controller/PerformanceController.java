package site.festifriends.domain.performance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.performance.dto.PerformanceResponse;
import site.festifriends.domain.performance.dto.PerformanceSearchRequest;
import site.festifriends.domain.performance.dto.PerformanceSearchResponse;
import site.festifriends.domain.performance.service.PerformanceService;

@RestController
@RequestMapping("/api/v1/performances")
@RequiredArgsConstructor
public class PerformanceController implements PerformanceApi {

    private final PerformanceService performanceService;

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
        ResponseWrapper<PerformanceResponse> response = performanceService.getPerformanceDetail(performanceId, memberId);
        return ResponseEntity.ok(response);
    }
} 
