package site.festifriends.domain.group.controller;

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
import site.festifriends.domain.application.dto.JoinedGroupResponse;
import site.festifriends.domain.group.service.GroupApplicationService;

@RestController
@RequestMapping("/api/v1/groups/managements")
@RequiredArgsConstructor
public class GroupApplicationController {

    private final GroupApplicationService groupApplicationService;

    @GetMapping("/applications")
    public ResponseEntity<CursorResponseWrapper<ApplicationListResponse>> getApplications(
        @AuthenticationPrincipal(expression = "member.id") Long memberId,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        CursorResponseWrapper<ApplicationListResponse> response =
            groupApplicationService.getApplicationsWithSlice(memberId, cursorId, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/applied")
    public ResponseEntity<CursorResponseWrapper<AppliedListResponse>> getAppliedApplications(
        @AuthenticationPrincipal(expression = "member.id") Long memberId,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        CursorResponseWrapper<AppliedListResponse> response =
            groupApplicationService.getAppliedGroupsWithSlice(memberId, cursorId, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/joined")
    public ResponseEntity<CursorResponseWrapper<JoinedGroupResponse>> getJoinedGroups(
        @AuthenticationPrincipal(expression = "member.id") Long memberId,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        CursorResponseWrapper<JoinedGroupResponse> response =
            groupApplicationService.getJoinedGroupsWithSlice(memberId, cursorId, size);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{applicationId}")
    public ResponseEntity<ResponseWrapper<ApplicationStatusResponse>> updateApplicationStatus(
        @AuthenticationPrincipal(expression = "member.id") Long memberId,
        @PathVariable Long applicationId,
        @RequestBody ApplicationStatusRequest request
    ) {
        ResponseWrapper<ApplicationStatusResponse> response =
            groupApplicationService.updateApplicationStatus(memberId, applicationId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/applied/{applicationId}")
    public ResponseEntity<ResponseWrapper<ApplicationStatusResponse>> confirmApplication(
        @AuthenticationPrincipal(expression = "member.id") Long memberId,
        @PathVariable Long applicationId
    ) {
        ResponseWrapper<ApplicationStatusResponse> response =
            groupApplicationService.confirmApplication(memberId, applicationId);

        return ResponseEntity.ok(response);
    }
}