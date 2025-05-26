package site.festifriends.entity.enums;

/**
 * 신고 타입을 나타내는 Enum
 */
public enum ReportType {
    POST("게시물"),
    CHAT("채팅"),
    PARTY("모임"),
    MEMBER("유저"),
    REVIEW("리뷰");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}