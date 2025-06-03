package site.festifriends.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.festifriends.entity.Review;

import java.util.Map;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    @Query("""
        SELECT r.reviewee.id AS memberId, AVG(r.score) AS avgRating
        FROM Review r
        WHERE r.reviewee.id IN :memberIds
        AND r.deleted IS NULL
        GROUP BY r.reviewee.id
        """)
    List<Map<String, Object>> findAverageRatingsByMemberIds(@Param("memberIds") List<Long> memberIds);
} 