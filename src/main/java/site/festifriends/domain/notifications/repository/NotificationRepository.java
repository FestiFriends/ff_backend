package site.festifriends.domain.notifications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import site.festifriends.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {


    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.member.id = :memberId AND n.isRead = false")
    int readAllNotifications(Long memberId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.member.id = :memberId AND n.id = :notificationId")
    int readNotification(Long memberId, Long notificationId);

    @Modifying
    @Query("UPDATE Notification n SET n.deleted = CURRENT_TIMESTAMP WHERE n.member.id = :memberId")
    int deleteAllNotifications(Long memberId);

    @Modifying
    @Query("UPDATE Notification n SET n.deleted = CURRENT_TIMESTAMP WHERE n.member.id = :memberId AND n.id = :notificationId")
    int deleteNotification(Long memberId, Long notificationId);

    boolean existsByMemberIdAndIsReadFalseAndDeletedIsNull(Long memberId);
}
