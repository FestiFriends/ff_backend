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

    @Override
    public boolean existsByReviewerIdAndRevieweeIdAndGroupId(Long reviewerId, Long revieweeId, Long groupId) {
        String jpql = """
            SELECT COUNT(r) > 0 FROM Review r
            WHERE r.reviewer.id = :reviewerId
            AND r.reviewee.id = :revieweeId
            AND r.group.id = :groupId
            AND r.deleted IS NULL
            """;

        return entityManager.createQuery(jpql, Boolean.class)
            .setParameter("reviewerId", reviewerId)
            .setParameter("revieweeId", revieweeId)
            .setParameter("groupId", groupId)
            .getSingleResult();
    }

    @Override
    public boolean isUserParticipantInGroup(Long userId, Long groupId) {
        String jpql = """
            SELECT COUNT(mg) > 0 FROM MemberGroup mg
            WHERE mg.member.id = :userId
            AND mg.group.id = :groupId
            AND mg.status IN ('ACCEPTED', 'CONFIRMED')
            AND mg.deleted IS NULL
            """;

        return entityManager.createQuery(jpql, Boolean.class)
            .setParameter("userId", userId)
            .setParameter("groupId", groupId)
            .getSingleResult();
    }
}
