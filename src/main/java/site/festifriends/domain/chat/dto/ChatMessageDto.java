package site.festifriends.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.domain.image.dto.ImageDto;

@Getter
@Builder
public class ChatMessageDto {

    private Long messageId;
    private Long senderId;
    private String senderName;
    private ImageDto senderImage;
    private String content;
    private LocalDateTime createdAt;
}
