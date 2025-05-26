package site.festifriends.entity.enums;

/**
 * 모임 신청 상태를 나타내는 Enum
 */
public enum ApplicationStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거절됨"),
    CANCELLED("취소됨");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}