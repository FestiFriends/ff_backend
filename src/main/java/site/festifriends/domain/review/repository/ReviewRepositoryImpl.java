package site.festifriends.domain.review.repository;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.festifriends.entity.Member;
import site.festifriends.entity.Review;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Review> findRecentReviews(int limit) {
        String jpql = """
            SELECT r FROM Review r
            JOIN FETCH r.group g
            LEFT JOIN FETCH g.performance p
            LEFT JOIN FETCH r.tags
            WHERE r.deleted IS NULL
            AND g.deleted IS NULL
            ORDER BY r.createdAt DESC
            """;

        return entityManager.createQuery(jpql, Review.class)
            .setMaxResults(limit)
            .getResultList();
    }

    @Override
    public List<Review> findUserReviewsByRevieweeId(Long revieweeId) {
        String jpql = """
            SELECT r FROM Review r
            JOIN FETCH r.group g
            LEFT JOIN FETCH g.performance p
            LEFT JOIN FETCH r.tags
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
    public List<Review> findUserReviewsByRevieweeIdWithCursor(Long revieweeId, Long cursorId, int size) {
        // 1. 먼저 모임 ID들을 size+1 개만큼 조회 (hasNext 판단용)
        String groupQuery = """
            SELECT DISTINCT g.id FROM Review r
            JOIN r.group g
            WHERE r.reviewee.id = :revieweeId
            AND r.deleted IS NULL
            AND g.deleted IS NULL
            """ + (cursorId != null ? "AND g.id < :cursorId " : "") + """
            ORDER BY g.id DESC
            """;

        var groupIdQuery = entityManager.createQuery(groupQuery, Long.class)
            .setParameter("revieweeId", revieweeId)
            .setMaxResults(size + 1); // hasNext 판단을 위해 1개 추가 조회

        if (cursorId != null) {
            groupIdQuery.setParameter("cursorId", cursorId);
        }

        List<Long> groupIds = groupIdQuery.getResultList();

        if (groupIds.isEmpty()) {
            return List.of();
        }

        // 2. 해당 모임들의 모든 리뷰를 조회
        String reviewQuery = """
            SELECT r FROM Review r
            JOIN FETCH r.group g
            JOIN FETCH r.reviewer rv
            LEFT JOIN FETCH g.performance p
            LEFT JOIN FETCH r.tags
            WHERE r.reviewee.id = :revieweeId
            AND r.deleted IS NULL
            AND g.deleted IS NULL
            AND g.id IN :groupIds
            ORDER BY g.id DESC, r.createdAt DESC
            """;

        return entityManager.createQuery(reviewQuery, Review.class)
            .setParameter("revieweeId", revieweeId)
            .setParameter("groupIds", groupIds)
            .getResultList();
    }

    @Override
    public List<Review> findWrittenReviewsByReviewerId(Long reviewerId, Long cursorId, int size) {
        String jpql = """
            SELECT r FROM Review r
            JOIN FETCH r.group g
            JOIN FETCH r.reviewee re
            LEFT JOIN FETCH g.performance p
            LEFT JOIN FETCH r.tags
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

    @Override
    public List<Object[]> findWritableReviewGroups(Long userId, Long cursorId, int size) {
        String jpql = """
            SELECT DISTINCT g, p FROM Group g
            LEFT JOIN FETCH g.performance p
            WHERE g.id IN (
                SELECT mg.group.id FROM MemberGroup mg
                WHERE mg.member.id = :userId
                AND mg.status IN ('ACCEPTED', 'CONFIRMED')
                AND mg.deleted IS NULL
            )
            AND g.endDate < :currentTime
            AND g.deleted IS NULL
            AND EXISTS (
                SELECT 1 FROM MemberGroup mg2
                WHERE mg2.group.id = g.id
                AND mg2.member.id != :userId
                AND mg2.status IN ('ACCEPTED', 'CONFIRMED')
                AND mg2.deleted IS NULL
                AND NOT EXISTS (
                    SELECT 1 FROM Review r
                    WHERE r.reviewer.id = :userId
                    AND r.reviewee.id = mg2.member.id
                    AND r.group.id = g.id
                    AND r.deleted IS NULL
                )
            )
            """ + (cursorId != null ? "AND g.id < :cursorId " : "") + """
            ORDER BY g.id DESC
            """;

        var query = entityManager.createQuery(jpql, Object[].class)
            .setParameter("userId", userId)
            .setParameter("currentTime", LocalDateTime.now())
            .setMaxResults(size + 1); // hasNext 판단을 위해 1개 추가 조회

        if (cursorId != null) {
            query.setParameter("cursorId", cursorId);
        }

        return query.getResultList();
    }

    @Override
    public List<Member> findUnreviewedMembersInGroup(Long userId, Long groupId) {
        String jpql = """
            SELECT m FROM Member m
            WHERE m.id IN (
                SELECT mg.member.id FROM MemberGroup mg
                WHERE mg.group.id = :groupId
                AND mg.member.id != :userId
                AND mg.status IN ('ACCEPTED', 'CONFIRMED')
                AND mg.deleted IS NULL
            )
            AND NOT EXISTS (
                SELECT 1 FROM Review r
                WHERE r.reviewer.id = :userId
                AND r.reviewee.id = m.id
                AND r.group.id = :groupId
                AND r.deleted IS NULL
            )
            ORDER BY m.id
            """;

        return entityManager.createQuery(jpql, Member.class)
            .setParameter("userId", userId)
            .setParameter("groupId", groupId)
            .getResultList();
    }

    @Override
    public Object[] getMemberReviewCount(Long targetId) {
        String countSql = """
            SELECT
                AVG(r.score) AS avgRating,
                COUNT(*) AS reviewCount
            FROM review r
            WHERE r.reviewee_id = :targetId
            AND r.deleted IS NULL
            """;

        return (Object[]) entityManager.createNativeQuery(countSql)
            .setParameter("targetId", targetId)
            .getSingleResult();
    }

    @Override
    public List<Object[]> countEachReviewTag(Long targetId) {
        String sql = """
            SELECT rt.tag, COUNT(*) AS tagCount
            FROM review r
            JOIN review_tags rt ON r.review_id = rt.review_id
            WHERE r.reviewee_id = :targetId
            AND r.deleted IS NULL
            GROUP BY rt.tag
            """;

        return entityManager.createNativeQuery(sql)
            .setParameter("targetId", targetId)
            .getResultList();
    }

    @Override
    public List<String> getMemberReviewContent(Long targetId) {
        String sql = """
            SELECT r.content
            FROM review r
            WHERE r.reviewee_id = :targetId
            AND r.deleted IS NULL
            """;

        return entityManager.createNativeQuery(sql)
            .setParameter("targetId", targetId)
            .getResultList();
    }
}
