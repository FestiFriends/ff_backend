package site.festifriends.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.MemberParty;

public interface ApplicationRepository extends JpaRepository<MemberParty, Long>, ApplicationRepositoryCustom {
} 