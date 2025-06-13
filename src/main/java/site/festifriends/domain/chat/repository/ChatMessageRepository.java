package site.festifriends.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.ChatMessage;
import site.festifriends.entity.ChatRoom;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageRepositoryCustom {

    void deleteAllByChatRoom(ChatRoom chatRoom);
}
