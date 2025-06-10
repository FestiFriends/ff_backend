package site.festifriends.domain.notifications.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.notifications.dto.GetNotificationsResponse;
import site.festifriends.domain.notifications.dto.GetNotificationsResponse.TargetDto;
import site.festifriends.domain.notifications.dto.NotificationDto;
import site.festifriends.domain.notifications.repository.NotificationRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public CursorResponseWrapper<GetNotificationsResponse> getNotifications(Long memberId, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        Slice<NotificationDto> slice = notificationRepository.getNotifications(memberId, cursorId, pageable);

        if (slice.isEmpty()) {
            return CursorResponseWrapper.empty("알림 조회 성공");
        }

        List<GetNotificationsResponse> response = new ArrayList<>();

        for (NotificationDto notification : slice.getContent()) {
            TargetDto targetDto = createTargetDto(notification);

            response.add(GetNotificationsResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.getIsRead())
                .target(targetDto)
                .build());
        }

        Long nextCursorId = null;
        if (slice.hasNext()) {
            response.remove(response.size() - 1);
            nextCursorId = slice.getContent().get(size).getId();
        }

        return CursorResponseWrapper.success(
            "알림 조회 성공",
            response,
            nextCursorId,
            slice.hasNext()
        );
    }

    public SseEmitter subscribe(Long memberId) {
        SseEmitter oldEmitter = emitters.get(memberId);
        if (oldEmitter != null) {
            oldEmitter.complete();
            emitters.remove(memberId);
        }

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(memberId, sseEmitter);

        sseEmitter.onCompletion(() -> emitters.remove(memberId));
        sseEmitter.onTimeout(() -> emitters.remove(memberId));
        sseEmitter.onError((ex) -> {
            emitters.remove(memberId);
            log.error("SSE connection error for member: {}", memberId, ex);
        });

        try {
            sseEmitter.send(SseEmitter.event()
                .name("connect")
                .data("연결이 성공되었습니다."));
        } catch (IOException e) {
            log.error("Failed to send initial message", e);
            emitters.remove(memberId);
            sseEmitter.completeWithError(e);
        }

        return sseEmitter;
    }

    private TargetDto createTargetDto(NotificationDto notification) {
        if (notification.getTargetId() == null) {
            return null;
        }

        if ("POST".equals(notification.getType())) {
            return TargetDto.builder()
                .postId(notification.getTargetId())
                .groupId(notification.getSubTargetId())
                .build();
        } else if ("GROUP".equals(notification.getType()) || "SCHEDULE".equals(notification.getType())) {
            return TargetDto.builder()
                .groupId(notification.getTargetId())
                .build();
        }

        return null;
    }

    @Transactional
    public void readAllNotifications(Long memberId) {
        notificationRepository.readAllNotifications(memberId);
    }

    @Transactional
    public void readNotification(Long memberId, Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "알림이 존재하지 않습니다.");
        }
        notificationRepository.readNotification(memberId, notificationId);
    }

    @Transactional
    public void deleteAllNotifications(Long memberId) {
        notificationRepository.deleteAllNotifications(memberId);
    }

    @Transactional
    public void deleteNotification(Long memberId, Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "알림이 존재하지 않습니다.");
        }
        notificationRepository.deleteNotification(memberId, notificationId);
    }

    public boolean existUnreadNotification(Long memberId) {
        return notificationRepository.existsByMemberIdAndIsReadFalseAndDeletedIsNull(memberId);
    }
}
