package site.festifriends.domain.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.application.dto.ApplicationListResponse;
import site.festifriends.domain.application.service.ApplicationService;

@RestController
@RequestMapping("/api/v1/managements")
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApi {

    private final ApplicationService applicationService;

    @Override
    @GetMapping("/applications")
    public ResponseEntity<CursorResponseWrapper<ApplicationListResponse>> getApplications(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size
    ) {
        CursorResponseWrapper<ApplicationListResponse> response = 
                applicationService.getApplicationsWithSlice(memberId, cursorId, size);
        
        return ResponseEntity.ok(response);
    }
} 