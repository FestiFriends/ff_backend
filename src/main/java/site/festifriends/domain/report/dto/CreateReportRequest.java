package site.festifriends.domain.report.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.ReportReasonType;
import site.festifriends.entity.enums.ReportType;

@Getter
@Builder
public class CreateReportRequest {

    private Long targetId;
    private ReportType category;
    private ReportReasonType reason;
    private List<ReportImages> snapshots;
    private String details;

    @Getter
    @Builder
    public static class ReportImages {

        private String alt;
        private String src;
    }
}
