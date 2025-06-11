package site.festifriends.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.domain.image.dto.ImageDto;

@Getter
@Builder
public class ChatMessageResponse {

    private Long messageId;
    private Long senderId;
    private String senderName;
    private ImageDto senderImage;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

}
