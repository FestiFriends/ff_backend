package site.festifriends.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.festifriends.entity.Report;

public interface ReportRepositoryCustom {

    Page<Report> getReportPage(Pageable pageable);
}
