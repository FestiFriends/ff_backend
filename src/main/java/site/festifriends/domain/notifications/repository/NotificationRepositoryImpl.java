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
            SELECT n.notification_id, n.message, n.type ,n.created_at, n.is_read, n.target_id, n.sub_target_id
            FROM notification n
            WHERE n.member_id = :memberId
            AND (:cursorId IS NULL OR n.notification_id <= :cursorId)
            AND n.deleted IS NULL
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
                .type((String) result[2])
                .createdAt(((Timestamp) result[3]).toLocalDateTime())
                .isRead((Boolean) result[4])
                .targetId(result[5] != null ? ((Number) result[5]).longValue() : null)
                .subTargetId(result[6] != null ? ((Number) result[6]).longValue() : null)
                .build())
            .toList();

        boolean hasNext = notifications.size() == pageSize;

        return new SliceImpl<>(notifications, pageable, hasNext);
    }
}
