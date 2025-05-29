package site.festifriends.domain.group.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.festifriends.entity.Group;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g WHERE g.performance.id = :performanceId AND g.deleted IS NULL")
    Page<Group> findByPerformanceId(@Param("performanceId") Long performanceId, Pageable pageable);

    @Query("SELECT COUNT(g) FROM Group g WHERE g.performance.id = :performanceId AND g.deleted IS NULL")
    Long countByPerformanceId(@Param("performanceId") Long performanceId);
}
