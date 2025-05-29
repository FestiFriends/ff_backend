package site.festifriends.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.MemberGroup;

public interface ApplicationRepository extends JpaRepository<MemberGroup, Long>, ApplicationRepositoryCustom {
} 