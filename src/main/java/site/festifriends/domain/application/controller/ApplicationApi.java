package site.festifriends.domain.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.application.dto.ApplicationListResponse;

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
} 