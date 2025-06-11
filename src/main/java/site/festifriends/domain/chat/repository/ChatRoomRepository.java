package site.festifriends.domain.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.ChatRoom;
import site.festifriends.entity.Group;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByGroup(Group group);
}
