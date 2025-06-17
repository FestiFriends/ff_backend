package site.festifriends.domain.report.repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import site.festifriends.entity.Report;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<Report> getReportPage(Pageable pageable) {
        String sql = """
            SELECT r
            FROM Report r
            WHERE r.deleted IS NULL
            AND r.status = 'PENDING'
            ORDER BY r.createdAt ASC
            """;

        String countSql = """
                SELECT COUNT(r)
                FROM Report r
                WHERE r.deleted IS NULL
                AND r.status = 'PENDING'
            """;

        List<Report> content = entityManager.createQuery(sql, Report.class)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        Long total = entityManager.createQuery(countSql, Long.class)
            .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
