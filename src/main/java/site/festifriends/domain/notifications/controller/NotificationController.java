package site.festifriends.domain.notifications.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.festifriends.common.response.CursorResponseWrapper;
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
    public ResponseEntity<CursorResponseWrapper<GetNotificationsResponse>> getNotifications(UserDetailsImpl userDetails,
        Long cursorId, int size) {
        return ResponseEntity.ok(notificationService.getNotifications(userDetails.getMemberId(), cursorId, size));
    }

    @Override
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(UserDetailsImpl userDetails) {
        return ResponseEntity.ok(notificationService.subscribe(userDetails.getMemberId()));
    }
}
