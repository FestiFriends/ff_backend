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
}
