package site.festifriends.domain.notifications.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.notifications.dto.GetNotificationsResponse;
import site.festifriends.domain.notifications.dto.NotificationDto;
import site.festifriends.domain.notifications.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public CursorResponseWrapper<GetNotificationsResponse> getNotifications(Long memberId, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        Slice<NotificationDto> slice = notificationRepository.getNotifications(memberId, cursorId, pageable);

        if (slice.isEmpty()) {
            return CursorResponseWrapper.empty("알림 조회 성공");
        }

        List<GetNotificationsResponse> response = new ArrayList<>();

        for (NotificationDto notification : slice.getContent()) {
            response.add(GetNotificationsResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
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
}
