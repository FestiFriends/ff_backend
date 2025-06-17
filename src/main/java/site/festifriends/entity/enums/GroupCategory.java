package site.festifriends.entity.enums;

/**
 * 모임 카테고리를 나타내는 Enum
 */
public enum GroupCategory {
    COMPANION("같이 동행"),
    RIDE_SHARE("같이 탑승"),
    ROOM_SHARE("같이 숙소");

    private final String description;

    GroupCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}