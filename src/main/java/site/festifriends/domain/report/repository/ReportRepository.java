package site.festifriends.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {

}
