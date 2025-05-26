package site.festifriends.entity.enums;

/**
 * 알림 타입을 나타내는 Enum
 */
public enum NotificationType {
    PARTY_INVITATION("모임 초대"),
    PARTY_APPLICATION("모임 신청"),
    PARTY_APPROVAL("모임 승인"),
    PARTY_REJECTION("모임 거절"),
    NEW_NOTICE("새 공지"),
    NEW_COMMENT("새 댓글"),
    NEW_REVIEW("새 리뷰"),
    SCHEDULE_REMINDER("일정 알림");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}