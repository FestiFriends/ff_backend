package site.festifriends.domain.notifications.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationEvent {

    private final String message;
    private LocalDateTime createdAt;
}
