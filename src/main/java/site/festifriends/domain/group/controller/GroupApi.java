package site.festifriends.domain.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.application.dto.ApplicationRequest;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.group.dto.GroupDetailResponse;
import site.festifriends.domain.group.dto.GroupMembersResponse;
import site.festifriends.domain.group.dto.GroupUpdateRequest;
import site.festifriends.domain.group.dto.PerformanceGroupsData;
import site.festifriends.domain.group.dto.UpdateMemberRoleRequest;

@Tag(name = "Group", description = "모임 관련 API")
public interface GroupApi {

    @Operation(
        summary = "공연 모임 목록 조회",
        description = """
            해당 공연의 모임 목록을 조회합니다.
            
            로그인한 사용자인 경우 모임 찜 여부도 조회됩니다.
            로그인하지 않은 사용자인 경우 찜 여부는 모두 false로 반환됩니다.
            """
    )
    @GetMapping("/api/v1/performances/{performanceId}/groups")
    ResponseEntity<ResponseWrapper<PerformanceGroupsData>> getGroupsByPerformanceId(
        @AuthenticationPrincipal UserDetailsImpl user,
        @Parameter(description = "공연 ID") @PathVariable Long performanceId,
        @Parameter(description = "페이지 번호 (기본값: 1)") @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "한 페이지당 항목 수 (기본값: 20)") @RequestParam(defaultValue = "20") Integer size
    );

    @Operation(
        summary = "모임 기본 정보 조회",
        description = """
            모임 ID로 모임의 기본 정보를 조회합니다.
            
            - 모임의 상세 정보 (제목, 카테고리, 인원, 날짜 등)
            - 관련 공연 정보 (ID, 제목, 포스터)
            - 방장 정보 (ID, 이름, 평점)
            """
    )
    @GetMapping("/api/v1/groups/{groupId}")
    ResponseEntity<ResponseWrapper<GroupDetailResponse>> getGroupDetail(
        @Parameter(description = "모임 ID") @PathVariable Long groupId
    );

    @Operation(
        summary = "모임 기본 정보 수정",
        description = """
            모임 ID로 모임의 기본 정보를 수정합니다.
            
            - 모임 방장만 수정 가능
            - 현재 참여 인원보다 적은 최대 인원수로 수정 불가
            - 시작 날짜가 종료 날짜보다 늦을 수 없음
            - 시작 연령이 종료 연령보다 클 수 없음
            """
    )
    @PatchMapping("/api/v1/groups/{groupId}")
    ResponseEntity<ResponseWrapper<Void>> updateGroup(
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @Valid @RequestBody GroupUpdateRequest request,
        @AuthenticationPrincipal UserDetailsImpl user
    );

    @Operation(
        summary = "모임 참가 신청",
        description = """
            모임에 참가 신청을 합니다.
            
            - 로그인이 필요합니다
            - 본인이 만든 모임에는 신청할 수 없습니다
            - 이미 신청한 모임에는 중복 신청할 수 없습니다
            - 신청 시 상태는 PENDING으로 설정됩니다
            """
    )
    @PostMapping("/api/v1/groups/{groupId}/join")
    ResponseEntity<ResponseWrapper<Void>> joinGroup(
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @Valid @RequestBody ApplicationRequest request,
        @AuthenticationPrincipal UserDetailsImpl user
    );

    @Operation(
        summary = "모임원 목록 조회",
        description = """
            모임원 목록을 커서 기반 페이징으로 조회합니다.
            
            - 모임에 참가한 사용자(CONFIRMED 상태)만 조회 가능합니다
            - 방장(HOST)이 먼저 나오고, 그 다음 멤버들이 최신순으로 나옵니다
            - 커서 기반 페이징을 사용합니다
            """
    )
    @GetMapping("/api/v1/groups/{groupId}/members")
    ResponseEntity<ResponseWrapper<GroupMembersResponse>> getGroupMembers(
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @Parameter(description = "커서 ID (기본값: 첫번째 요소)") @RequestParam(required = false) Long cursorId,
        @Parameter(description = "한 페이지당 항목 수 (기본값: 20)") @RequestParam(defaultValue = "20") int size,
        @AuthenticationPrincipal UserDetailsImpl user
    );

    @Operation(
        summary = "모임원 권한 수정",
        description = """
            모임원의 권한을 수정합니다.
            
            - 모임장(HOST)만 다른 모임원의 권한을 수정할 수 있습니다
            - 본인의 권한은 수정할 수 없습니다
            - HOST로 변경 시, 기존 HOST는 자동으로 MEMBER로 변경됩니다
            - HOST는 한 명만 존재할 수 있습니다
            """
    )
    @PatchMapping("/api/v1/groups/{groupId}/members/{memberId}/role")
    ResponseEntity<ResponseWrapper<Void>> updateMemberRole(
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @Parameter(description = "대상 회원 ID") @PathVariable Long memberId,
        @Valid @RequestBody UpdateMemberRoleRequest request,
        @AuthenticationPrincipal UserDetailsImpl user
    );

    @Operation(
        summary = "모임 탈퇴",
        description = """
            모임에서 탈퇴합니다.
            
            - 일반 멤버는 언제든 탈퇴 가능합니다
            - 모임장의 경우:
              1. 모임에 본인만 있을 경우: 탈퇴 가능하며 모임이 삭제됩니다
              2. 다른 멤버가 있을 경우: 먼저 모임장을 위임한 후 탈퇴해야 합니다
            - 모든 멤버가 탈퇴하면 모임이 자동으로 삭제됩니다
            """
    )
    @DeleteMapping("/api/v1/groups/{groupId}/leave")
    ResponseEntity<ResponseWrapper<Void>> leaveGroup(
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @AuthenticationPrincipal UserDetailsImpl user
    );

    @Operation(
        summary = "모임원 퇴출",
        description = """
            모임원을 강제로 퇴출시킵니다.
            
            - 모임장(HOST)만 다른 모임원을 퇴출시킬 수 있습니다
            - 모임장은 자기 자신을 퇴출시킬 수 없습니다
            - 퇴출된 멤버는 즉시 모임에서 제거됩니다
            """
    )
    @DeleteMapping("/api/v1/groups/{groupId}/members/{memberId}")
    ResponseEntity<ResponseWrapper<Void>> kickMember(
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @Parameter(description = "퇴출할 멤버 ID") @PathVariable Long memberId,
        @AuthenticationPrincipal UserDetailsImpl user
    );
}

