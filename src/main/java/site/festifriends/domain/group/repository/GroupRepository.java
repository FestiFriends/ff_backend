package site.festifriends.domain.group.repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.festifriends.entity.Group;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;

public interface GroupRepository extends JpaRepository<Group, Long>, GroupRepositoryCustom {

    @Query("SELECT g FROM Group g WHERE g.performance.id = :performanceId AND g.deleted IS NULL")
    Page<Group> findByPerformanceId(@Param("performanceId") Long performanceId, Pageable pageable);

    @Query("SELECT COUNT(g) FROM Group g WHERE g.performance.id = :performanceId AND g.deleted IS NULL")
    Long countByPerformanceId(@Param("performanceId") Long performanceId);

    @Query("""
        SELECT g FROM Group g 
        WHERE g.performance.id = :performanceId 
        AND g.deleted IS NULL
        AND (:category IS NULL OR g.gatherType = :category)
        AND (:startDate IS NULL OR g.startDate >= :startDate)
        AND (:endDate IS NULL OR g.endDate <= :endDate)
        AND (:location IS NULL OR g.location LIKE %:location%)
        AND (:gender IS NULL OR g.genderType = :gender)
        """)
    Page<Group> findByPerformanceIdWithFilters(
        @Param("performanceId") Long performanceId,
        @Param("category") GroupCategory category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("location") String location,
        @Param("gender") Gender gender,
        Pageable pageable
    );

    @Query("""
        SELECT COUNT(g) FROM Group g 
        WHERE g.performance.id = :performanceId 
        AND g.deleted IS NULL
        AND (:category IS NULL OR g.gatherType = :category)
        AND (:startDate IS NULL OR g.startDate >= :startDate)
        AND (:endDate IS NULL OR g.endDate <= :endDate)
        AND (:location IS NULL OR g.location LIKE %:location%)
        AND (:gender IS NULL OR g.genderType = :gender)
        """)
    Long countByPerformanceIdWithFilters(
        @Param("performanceId") Long performanceId,
        @Param("category") GroupCategory category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("location") String location,
        @Param("gender") Gender gender
    );
}
