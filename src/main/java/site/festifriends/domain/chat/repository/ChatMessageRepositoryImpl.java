package site.festifriends.domain.chat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import site.festifriends.domain.chat.dto.ChatMessageDto;
import site.festifriends.domain.image.dto.ImageDto;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepositoryCustom {

    private final EntityManager em;

    @Override
    public Slice<ChatMessageDto> getChatMessages(Long chatRoomId, Long cursorId, Pageable pageable) {
        int pageSize = pageable.getPageSize() + 1;

        String sql = """
            SELECT cm.chat_message_id, m.member_id, m.nickname, mi.member_image_id, mi.src, mi.alt, cm.content, cm.created_at
            FROM chat_message cm
            JOIN member m ON cm.member_id = m.member_id
            LEFT JOIN member_image mi ON m.member_id = mi.member_id
            WHERE cm.chat_room_id = :chatRoomId
            AND (:cursorId IS NULL OR cm.chat_message_id <= :cursorId)
            AND cm.deleted IS NULL
            ORDER BY cm.chat_message_id DESC
            LIMIT :pageSize
            """;

        Query query = em.createNativeQuery(sql);

        query.setParameter("chatRoomId", chatRoomId);
        query.setParameter("cursorId", cursorId);
        query.setParameter("pageSize", pageSize);

        List<Object[]> results = query.getResultList();

        List<ChatMessageDto> dtos = results.stream()
            .map(result -> ChatMessageDto.builder()
                .chatId(((Number) result[0]).longValue())
                .senderId(((Number) result[1]).longValue())
                .senderName((String) result[2])
                .senderImage(result[3] != null ? ImageDto.builder()
                    .id(((String) result[3]))
                    .src((String) result[4])
                    .alt((String) result[5])
                    .build() : null)
                .content((String) result[6])
                .createdAt(((java.sql.Timestamp) result[7]).toLocalDateTime())
                .build())
            .toList();

        boolean hasNext = dtos.size() == pageSize;

        return new SliceImpl<>(dtos, pageable, hasNext);
    }
}
