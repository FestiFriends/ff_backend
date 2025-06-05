package site.festifriends.domain.group.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.application.dto.ApplicationRequest;
import site.festifriends.domain.application.service.ApplicationService;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.group.dto.GroupDetailResponse;
import site.festifriends.domain.group.dto.GroupMembersResponse;
import site.festifriends.domain.group.dto.GroupUpdateRequest;
import site.festifriends.domain.group.dto.PerformanceGroupsData;
import site.festifriends.domain.group.dto.UpdateMemberRoleRequest;
import site.festifriends.domain.group.service.GroupService;

@RestController
@RequiredArgsConstructor
public class GroupController implements GroupApi {

    private final GroupService groupService;
    private final ApplicationService applicationService;

    @Override
    @GetMapping("/api/v1/performances/{performanceId}/groups")
    public ResponseEntity<ResponseWrapper<PerformanceGroupsData>> getGroupsByPerformanceId
        (
            @AuthenticationPrincipal UserDetailsImpl user,
            @PathVariable Long performanceId,
            @RequestParam(required = false) site.festifriends.entity.enums.GroupCategory category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) site.festifriends.entity.enums.Gender gender,
            @RequestParam(required = false, defaultValue = "date_desc") String sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
        ) {

        Long memberId = user != null ? user.getMemberId() : null;
        PerformanceGroupsData data = groupService.getGroupsByPerformanceId(
            performanceId, category, startDate, endDate, location, gender, sort, page, size, memberId);

        return ResponseEntity.ok(ResponseWrapper.success("요청이 성공적으로 처리되었습니다.", data));
    }

    @Override
    @GetMapping("/api/v1/groups/{groupId}")
    public ResponseEntity<ResponseWrapper<GroupDetailResponse>> getGroupDetail(@PathVariable Long groupId) {
        GroupDetailResponse response = groupService.getGroupDetail(groupId);
        return ResponseEntity.ok(ResponseWrapper.success("모임 기본 정보 조회 성공.", response));
    }

    @Override
    public ResponseEntity<ResponseWrapper<Void>> updateGroup(
        @PathVariable Long groupId,
        GroupUpdateRequest request,
        @AuthenticationPrincipal UserDetailsImpl user) {

        Long memberId = user.getMemberId();
        groupService.updateGroup(groupId, request, memberId);

        return ResponseEntity.ok(ResponseWrapper.success("모임 정보가 성공적으로 수정되었습니다."));
    }

    @Override
    @PostMapping("/api/v1/groups/{groupId}/join")
    public ResponseEntity<ResponseWrapper<Void>> joinGroup(
        @PathVariable Long groupId,
        @RequestBody ApplicationRequest request,
        @AuthenticationPrincipal UserDetailsImpl user) {

        Long memberId = user.getMemberId();
        ResponseWrapper<Void> response = applicationService.applyToGroup(memberId, groupId, request);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/api/v1/groups/{groupId}/members")
    public ResponseEntity<ResponseWrapper<GroupMembersResponse>> getGroupMembers(
        @PathVariable Long groupId,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size,
        @AuthenticationPrincipal UserDetailsImpl user) {

        Long memberId = user.getMemberId();
        GroupMembersResponse response = groupService.getGroupMembers(groupId, cursorId, size, memberId);

        return ResponseEntity.ok(ResponseWrapper.success("모임원 목록 조회 성공", response));
    }

    @Override
    @PatchMapping("/api/v1/groups/{groupId}/members/{memberId}/role")
    public ResponseEntity<ResponseWrapper<Void>> updateMemberRole(
        @PathVariable Long groupId,
        @PathVariable Long memberId,
        @RequestBody UpdateMemberRoleRequest request,
        @AuthenticationPrincipal UserDetailsImpl user) {

        Long hostId = user.getMemberId();
        groupService.updateMemberRole(groupId, memberId, request, hostId);

        return ResponseEntity.ok(ResponseWrapper.success("모임원 권한이 성공적으로 수정되었습니다.", null));
    }

    @Override
    @DeleteMapping("/api/v1/groups/{groupId}/leave")
    public ResponseEntity<ResponseWrapper<Void>> leaveGroup(
        @PathVariable Long groupId,
        @AuthenticationPrincipal UserDetailsImpl user) {

        Long memberId = user.getMemberId();
        groupService.leaveGroup(groupId, memberId);

        return ResponseEntity.ok(ResponseWrapper.success("정상적으로 탈퇴하였습니다.", null));
    }

    @Override
    @DeleteMapping("/api/v1/groups/{groupId}/members/{memberId}")
    public ResponseEntity<ResponseWrapper<Void>> kickMember(
        @PathVariable Long groupId,
        @PathVariable Long memberId,
        @AuthenticationPrincipal UserDetailsImpl user) {

        Long hostId = user.getMemberId();
        groupService.kickMember(groupId, memberId, hostId);

        return ResponseEntity.ok(ResponseWrapper.success("모임원이 성공적으로 퇴출되었습니다.", null));
    }
}

