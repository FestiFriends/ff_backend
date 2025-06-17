package site.festifriends.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.festifriends.entity.enums.ReportStatus;

@Getter
@AllArgsConstructor
public class HandleReportRequest {

    private ReportStatus reportStatus;
}
