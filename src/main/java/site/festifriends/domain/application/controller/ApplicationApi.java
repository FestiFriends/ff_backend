package site.festifriends.domain.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.application.dto.ApplicationListResponse;
import site.festifriends.domain.application.dto.ApplicationStatusRequest;
import site.festifriends.domain.application.dto.ApplicationStatusResponse;
import site.festifriends.domain.application.dto.AppliedListResponse;
import site.festifriends.domain.application.dto.JoinedGroupResponse;

@Tag(name = "신청서 관리", description = "모임 신청서 관련 API")
public interface ApplicationApi {

    @Operation(
        summary = "신청서 목록 조회",
        description = "내가 방장인 모임에 온 신청서 목록을 Slice 기반 커서 페이지네이션으로 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "신청서 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    ResponseEntity<CursorResponseWrapper<ApplicationListResponse>> getApplications(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "이전 응답에서 받은 커서값, 없으면 첫 페이지 조회")
            @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 번에 가져올 신청서 개수, 기본값 20")
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(
        summary = "내가 신청한 모임 목록 조회",
        description = "내가 신청한 모임 목록을 Slice 기반 커서 페이지네이션으로 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "신청한 모임 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    ResponseEntity<CursorResponseWrapper<AppliedListResponse>> getAppliedApplications(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "이전 응답에서 받은 커서값, 없으면 첫 페이지 조회")
            @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 번에 가져올 신청서 개수, 기본값 20")
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(
        summary = "참가 중인 모임 목록 조회",
        description = "내가 참가 중인 모임 목록을 Slice 기반 커서 페이지네이션으로 조회합니다. (ApplicationStatus.CONFIRMED 상태)",
        responses = {
            @ApiResponse(responseCode = "200", description = "참가 중인 모임 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    ResponseEntity<CursorResponseWrapper<JoinedGroupResponse>> getJoinedGroups(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "이전 응답에서 받은 커서값, 없으면 첫 페이지 조회")
            @RequestParam(required = false) Long cursorId,
            @Parameter(description = "한 번에 가져올 모임 개수, 기본값 20")
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(
        summary = "모임 신청서 수락/거절",
        description = "방장이 모임 가입 신청을 수락하거나 거절합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "모임 신청서 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 사용자 (방장만 가능)"),
            @ApiResponse(responseCode = "404", description = "신청서를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    ResponseEntity<ResponseWrapper<ApplicationStatusResponse>> updateApplicationStatus(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "신청서 ID")
            @PathVariable Long applicationId,
            @RequestBody ApplicationStatusRequest request
    );

    @Operation(
        summary = "모임 가입 확정",
        description = "신청자가 수락된 모임 가입을 확정합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "모임 가입 확정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 사용자 (신청자만 가능)"),
            @ApiResponse(responseCode = "404", description = "신청서를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    ResponseEntity<ResponseWrapper<ApplicationStatusResponse>> confirmApplication(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "신청서 ID")
            @PathVariable Long applicationId
    );
} 