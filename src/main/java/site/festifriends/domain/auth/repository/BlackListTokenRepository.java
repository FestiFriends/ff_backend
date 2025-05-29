package site.festifriends.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.BlackListToken;

public interface BlackListTokenRepository extends JpaRepository<BlackListToken, Long> {

    boolean existsByToken(String token);
}
