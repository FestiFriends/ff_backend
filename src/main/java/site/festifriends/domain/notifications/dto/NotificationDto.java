package site.festifriends.domain.notifications.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationDto {

    private Long id;
    private String message;
    private String type;
    private LocalDateTime createdAt;
    private Boolean isRead;
    private Long targetId;
    private Long subTargetId;
}
