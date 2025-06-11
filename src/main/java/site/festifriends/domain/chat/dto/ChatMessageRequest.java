package site.festifriends.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageRequest {

    private Long senderId;
    private String content;
}
