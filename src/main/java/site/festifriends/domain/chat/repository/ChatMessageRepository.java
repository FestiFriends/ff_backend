package site.festifriends.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageRepositoryCustom {

}
