package site.festifriends.entity.enums;

/**
 * 알림 타입을 나타내는 Enum
 */
public enum NotificationType {
    APPLICATION("모임 가입 신청"),
    APPLIED("모임 가입 승인"),
    REJECTED("모임 가입 거절"),
    BANNED("모임 차단됨"),
    GROUP("모임"),
    MY_PROFILE("나에 대한 리뷰"),
    REVIEW("그룹 리뷰 요청"),
    POST("모임 내 새 게시글"),
    SCHEDULE("모임 새 일정");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}