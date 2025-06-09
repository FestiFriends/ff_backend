package site.festifriends.domain.group.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryCustom {

    private final EntityManager em;

    @Override
    public Object[] getMemberGroupCount(Long targetId) {
        String sql = """
            SELECT
                (SELECT COUNT(DISTINCT mg1.group_id)
                                FROM member_group mg1
                                WHERE mg1.member_id = :targetId
                                  AND mg1.status IN ('ACCEPTED', 'CONFIRMED')
                                  AND mg1.deleted IS NULL) AS joinedCount,
                (SELECT COUNT(DISTINCT mg2.group_id)
                                FROM member_group mg2
                                WHERE mg2.member_id = :targetId
                                  AND mg2.status IN ('ACCEPTED', 'CONFIRMED')) AS joinedCount,
                (SELECT COUNT(*)
                                FROM member_group mg3
                                WHERE mg3.member_id = :targetId
                                  AND mg3.role = 'HOST'
                                  AND mg3.status IN ('ACCEPTED', 'CONFIRMED')
                                  AND mg3.deleted IS NULL) AS createdCount
            """;

        return (Object[]) em.createNativeQuery(sql)
            .setParameter("targetId", targetId)
            .getSingleResult();
    }
}
