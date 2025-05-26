package site.festifriends.entity.enums;

/**
 * 성별을 나타내는 Enum
 */
public enum Gender {
    MALE("남자"),
    FEMALE("여자"),
    ALL("혼성");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}