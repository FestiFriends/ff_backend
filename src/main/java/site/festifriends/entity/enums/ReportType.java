package site.festifriends.entity.enums;

/**
 * 신고 타입을 나타내는 Enum
 */
public enum ReportType {
    GROUP("모임"),
    REVIEW("리뷰"),
    USER("사용자"),
    CHAT("채팅"),
    POST("게시글"),
    COMMENT("댓글");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}