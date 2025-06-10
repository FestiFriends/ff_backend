package site.festifriends.domain.notifications.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.notifications.dto.GetNotificationsResponse;
import site.festifriends.domain.notifications.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;

    @Override
    @GetMapping("")
    public ResponseEntity<CursorResponseWrapper<GetNotificationsResponse>> getNotifications(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(notificationService.getNotifications(userDetails.getMemberId(), cursorId, size));
    }

    @Override
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(notificationService.subscribe(userDetails.getMemberId()));
    }

    @Override
    @PatchMapping("")
    public ResponseEntity<?> readAllNotifications(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        notificationService.readAllNotifications(userDetails.getMemberId());
        return ResponseEntity.ok(ResponseWrapper.success("모든 알림을 읽음 처리하였습니다."));
    }

    @Override
    @PatchMapping("/{notificationId}")
    public ResponseEntity<?> readNotification(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long notificationId
    ) {
        notificationService.readNotification(userDetails.getMemberId(), notificationId);
        return ResponseEntity.ok(ResponseWrapper.success("알림을 읽음 처리하였습니다."));
    }

    @Override
    @DeleteMapping("")
    public ResponseEntity<?> deleteAllNotifications(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        notificationService.deleteAllNotifications(userDetails.getMemberId());
        return ResponseEntity.ok(ResponseWrapper.success("모든 알림을 삭제하였습니다."));
    }

    @Override
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam Long notificationId
    ) {
        notificationService.deleteNotification(userDetails.getMemberId(), notificationId);
        return ResponseEntity.ok(ResponseWrapper.success("알림을 삭제하였습니다."));
    }


}
