package site.festifriends.domain.notifications.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.domain.notifications.dto.NotificationDto;

public interface NotificationRepositoryCustom {

    Slice<NotificationDto> getNotifications(Long memberId, Long cursorId, Pageable pageable);

}
