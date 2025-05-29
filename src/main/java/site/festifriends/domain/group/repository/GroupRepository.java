package site.festifriends.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}