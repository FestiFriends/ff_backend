package site.festifriends.domain.report.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.ReportImage;

public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {

    List<ReportImage> findByReportId(Long reportId);
}
