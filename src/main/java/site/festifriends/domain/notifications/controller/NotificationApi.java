package site.festifriends.domain.notifications.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.notifications.dto.GetNotificationsResponse;

public interface NotificationApi {

    @Operation(
        summary = "알림 조회",
        description = "커서 기반으로 알림을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "알림 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<CursorResponseWrapper<GetNotificationsResponse>> getNotifications(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    );
}
