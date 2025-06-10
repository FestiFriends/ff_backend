package site.festifriends.entity.enums;

/**
 * 알림 타입을 나타내는 Enum
 */
public enum NotificationType {
    APPLICATION("모임에 가입 신청이 도착했어요. 수락 또는 거절을 선택해 주세요."),
    APPLIED("모임의 가입 신청이 수락되었습니다. 가입을 확정해 주세요!"),
    REJECTED("모임의 가입 신청이 거절되었습니다."),
    BANNED("모임에서 강퇴되었습니다."),
    GROUP("모임의 방장으로 임명되었습니다."),
    MY_PROFILE("님이 회원님에 대한 리뷰를 남겼어요."),
    REVIEW("모임의 활동이 종료되었습니다. 함께한 모임원에게 리뷰를 남겨 주세요."),
    POST("모임에 새로운 글이 올라왔어요. 확인해 보세요!"),
    SCHEDULE("모임에 새로운 일정이 등록되었어요. 확인이 필요해요.");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}