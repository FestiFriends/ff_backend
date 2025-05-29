package site.festifriends.domain.performance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.Performance;

public interface PerformanceRepository extends JpaRepository<Performance, Long>, PerformanceRepositoryCustom {
} 