package site.festifriends.entity.enums;

/**
 * 모임 내 역할을 나타내는 Enum
 */
public enum Role {
    HOST("방장"),
    MEMBER("일반");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}