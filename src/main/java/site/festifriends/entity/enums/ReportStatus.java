package site.festifriends.entity.enums;

/**
 * 신고 상태를 나타내는 Enum
 */
public enum ReportStatus {
    PENDING("접수"),
    APPROVED("미처리"),
    REJECTED("반려");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}