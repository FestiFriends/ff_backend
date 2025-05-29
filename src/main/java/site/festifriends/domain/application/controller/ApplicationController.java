package site.festifriends.domain.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.application.dto.ApplicationListResponse;
import site.festifriends.domain.application.dto.ApplicationStatusRequest;
import site.festifriends.domain.application.dto.ApplicationStatusResponse;
import site.festifriends.domain.application.dto.AppliedListResponse;
import site.festifriends.domain.application.service.ApplicationService;

@RestController
@RequestMapping("/api/v1/managements")
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApi {

    private final ApplicationService applicationService;

    @Override
    @GetMapping("/applications")
    public ResponseEntity<CursorResponseWrapper<ApplicationListResponse>> getApplications(
        @AuthenticationPrincipal(expression = "member.id") Long memberId,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        CursorResponseWrapper<ApplicationListResponse> response =
            applicationService.getApplicationsWithSlice(memberId, cursorId, size);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/applied")
    public ResponseEntity<CursorResponseWrapper<AppliedListResponse>> getAppliedApplications(
        @AuthenticationPrincipal(expression = "member.id") Long memberId,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        CursorResponseWrapper<AppliedListResponse> response =
            applicationService.getAppliedApplicationsWithSlice(memberId, cursorId, size);

        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{applicationId}")
    public ResponseEntity<ResponseWrapper<ApplicationStatusResponse>> updateApplicationStatus(
        @AuthenticationPrincipal(expression = "member.id") Long memberId,
        @PathVariable Long applicationId,
        @RequestBody ApplicationStatusRequest request
    ) {
        ResponseWrapper<ApplicationStatusResponse> response =
            applicationService.updateApplicationStatus(memberId, applicationId, request);

        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/applied/{applicationId}")
    public ResponseEntity<ResponseWrapper<ApplicationStatusResponse>> confirmApplication(
        @AuthenticationPrincipal(expression = "member.id") Long memberId,
        @PathVariable Long applicationId
    ) {
        ResponseWrapper<ApplicationStatusResponse> response =
            applicationService.confirmApplication(memberId, applicationId);

        return ResponseEntity.ok(response);
    }
} 