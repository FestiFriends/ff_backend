package site.festifriends.domain.member.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Map<Long, Integer> getCountByPerformanceIds(List<Long> performanceIds) {
        String sql = """
            SELECT b.target_id, COUNT(*)
            FROM bookmark b
            WHERE b.target_id IN :performanceIds
            AND b.type = 'PERFORMANCE'
            GROUP BY b.target_id
            """;

        Query query = em.createNativeQuery(sql);

        query.setParameter("performanceIds", performanceIds);

        List<Object[]> result = query.getResultList();

        Map<Long, Integer> countMap = new HashMap<>();
        for (Long id : performanceIds) {
            countMap.put(id, 0);
        }

        for (Object[] row : result) {
            Long targetId = ((Number) row[0]).longValue();
            Integer count = ((Number) row[1]).intValue();
            countMap.put(targetId, count);
        }

        return countMap;
    }
}
