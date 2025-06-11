package site.festifriends.domain.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.domain.chat.dto.ChatMessageDto;

public interface ChatMessageRepositoryCustom {

    Slice<ChatMessageDto> getChatMessages(Long chatRoomId, Long cursorId, Pageable pageable);
}
