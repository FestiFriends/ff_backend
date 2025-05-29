package site.festifriends.domain.performance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
            @ModelAttribute PerformanceSearchRequest request
    ) {
        PerformanceSearchResponse response = performanceService.searchPerformances(request);
        return ResponseEntity.ok(response);
    }
} 
