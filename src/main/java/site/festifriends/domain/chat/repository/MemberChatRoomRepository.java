package site.festifriends.domain.chat.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.ChatRoom;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberChatRoom;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

    Optional<MemberChatRoom> findByMemberAndChatRoom(Member member, ChatRoom chatRoom);

    void deleteAllByChatRoom(ChatRoom chatRoom);

    List<MemberChatRoom> findAllByMember(Member member);

    void deleteAllByMember(Member member);
}
