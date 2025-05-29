package site.festifriends.entity.enums;

/**
 * 모임 신청 상태를 나타내는 Enum
 */
public enum ApplicationStatus {
    PENDING("대기"),
    ACCEPTED("수락"),
    REJECTED("거절"),
    CONFIRMED("확정");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
