package site.festifriends.domain.performance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.PerformanceImage;

public interface PerformanceImageRepository extends JpaRepository<PerformanceImage, Long> {

}
