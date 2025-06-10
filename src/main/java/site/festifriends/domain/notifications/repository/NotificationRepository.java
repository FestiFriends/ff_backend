package site.festifriends.domain.notifications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

}
