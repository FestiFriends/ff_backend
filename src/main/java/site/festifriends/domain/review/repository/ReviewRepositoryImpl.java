package site.festifriends.domain.review.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.festifriends.entity.Review;

import jakarta.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Review> findUserReviewsByRevieweeId(Long revieweeId) {
        String jpql = """
            SELECT r FROM Review r
            JOIN FETCH r.group g
            LEFT JOIN FETCH g.performance p
            WHERE r.reviewee.id = :revieweeId
            AND r.deleted IS NULL
            AND g.deleted IS NULL
            ORDER BY g.id DESC, r.createdAt DESC
            """;

        return entityManager.createQuery(jpql, Review.class)
            .setParameter("revieweeId", revieweeId)
            .getResultList();
    }

    @Override
    public List<Review> findWrittenReviewsByReviewerId(Long reviewerId, Long cursorId, int size) {
        String jpql = """
            SELECT r FROM Review r
            JOIN FETCH r.group g
            JOIN FETCH r.reviewee re
            LEFT JOIN FETCH g.performance p
            WHERE r.reviewer.id = :reviewerId
            AND r.deleted IS NULL
            AND g.deleted IS NULL
            """ + (cursorId != null ? "AND g.id < :cursorId " : "") + """
            ORDER BY g.id DESC, r.createdAt DESC
            """;

        var query = entityManager.createQuery(jpql, Review.class)
            .setParameter("reviewerId", reviewerId)
            .setMaxResults(size + 1); // hasNext 판단을 위해 1개 추가 조회

        if (cursorId != null) {
            query.setParameter("cursorId", cursorId);
        }

        return query.getResultList();
    }
}
