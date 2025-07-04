package site.festifriends.domain.application.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import site.festifriends.domain.application.dto.JoinedGroupResponse;
import site.festifriends.domain.application.dto.ManagementApplicationResponse;
import site.festifriends.domain.application.service.ApplicationService;
import site.festifriends.domain.auth.UserDetailsImpl;

@RestController
@RequestMapping("/api/v1/managements")
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApi {

    private final ApplicationService applicationService;

    @Override
    @GetMapping("/applications")
    public ResponseEntity<CursorResponseWrapper<ManagementApplicationResponse>> getApplications(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        CursorResponseWrapper<ManagementApplicationResponse> response =
            applicationService.getManagementApplicationsWithSlice(user.getMemberId(), cursorId, size);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/applied")
    public ResponseEntity<CursorResponseWrapper<AppliedListResponse>> getAppliedApplications(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        CursorResponseWrapper<AppliedListResponse> response =
            applicationService.getAppliedApplicationsWithSlice(user.getMemberId(), cursorId, size);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/joined")
    public ResponseEntity<CursorResponseWrapper<JoinedGroupResponse>> getJoinedGroups(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        CursorResponseWrapper<JoinedGroupResponse> response =
            applicationService.getJoinedGroupsWithSlice(user.getMemberId(), cursorId, size);

        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{applicationId}")
    public ResponseEntity<ResponseWrapper<Void>> updateApplicationStatus(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long applicationId,
        @Valid @RequestBody ApplicationStatusRequest request
    ) {
        ResponseWrapper<Void> response =
            applicationService.updateApplicationStatus(user.getMemberId(), applicationId, request);

        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/applied/{applicationId}")
    public ResponseEntity<ResponseWrapper<Void>> confirmApplication(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long applicationId,
        @Valid @RequestBody ApplicationStatusRequest request
    ) {
        ResponseWrapper<Void> response =
            applicationService.confirmApplication(user.getMemberId(), applicationId, request);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/applied/{applicationId}")
    public ResponseEntity<ResponseWrapper<ApplicationStatusResponse>> cancelApplication(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long applicationId) {
        return ResponseEntity.ok(applicationService.cancelApplication(user.getMemberId(), applicationId));
    }
} 
