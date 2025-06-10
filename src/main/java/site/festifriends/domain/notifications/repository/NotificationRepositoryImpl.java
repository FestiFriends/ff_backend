package site.festifriends.domain.notifications.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import site.festifriends.domain.notifications.dto.NotificationDto;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final EntityManager em;

    @Override
    public Slice<NotificationDto> getNotifications(Long memberId, Long cursorId, Pageable pageable) {
        int pageSize = pageable.getPageSize() + 1;

        String sql = """
            SELECT n.notification_id, n.message, n.created_at
            FROM notification n
            WHERE n.member_id = :memberId
            AND n.is_read = false
            AND (:cursorId IS NULL OR n.notification_id <= :cursorId)
            ORDER BY n.notification_id DESC
            LIMIT :size
            """;

        Query query = em.createNativeQuery(sql);

        query.setParameter("memberId", memberId);
        query.setParameter("cursorId", cursorId);
        query.setParameter("size", pageSize);

        List<Object[]> results = query.getResultList();

        List<NotificationDto> notifications = results.stream()
            .map(result -> NotificationDto.builder()
                .id(((Number) result[0]).longValue())
                .message((String) result[1])
                .createdAt(((Timestamp) result[2]).toLocalDateTime())
                .build())
            .toList();

        boolean hasNext = notifications.size() == pageSize;

        return new SliceImpl<>(notifications, pageable, hasNext);
    }
}
