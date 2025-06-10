package site.festifriends.domain.notifications.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
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

    @Operation(
        summary = "알림 구독",
        description = "알림을 실시간으로 구독합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "알림 구독 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<SseEmitter> subscribe(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(
        summary = "모든 알림 읽음 처리",
        description = "모든 알림을 읽음 처리합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "모든 알림을 읽음 처리하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> readAllNotifications(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(
        summary = "개발 알림 읽음 처리",
        description = "개발 알림을 읽음 처리합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "알림을 읽음 처리하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> readNotification(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long notificationId
    );

    @Operation(
        summary = "알림 전체 삭제",
        description = "모든 알림을 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "모든 알림을 삭제하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> deleteAllNotifications(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(
        summary = "알림 개별 삭제",
        description = "개별 알림을 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "알림을 삭제하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> deleteNotification(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long notificationId
    );

    @Operation(
        summary = "새로운 알림 조회",
        description = "회원이 읽지 않은 새로운 알림이 있는지 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "알림 상태를 확인했습니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> existUnreadNotification(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}
