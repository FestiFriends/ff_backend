package site.festifriends.domain.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetReportDetailResponse {

    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
    private String category;
    private String reason;
    private List<ReportImages> snapshots;
    private String details;
    private String status;

    @Getter
    @Builder
    public static class ReportImages {

        private Long id;
        private String alt;
        private String src;
    }
}
