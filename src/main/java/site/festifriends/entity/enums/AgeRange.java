package site.festifriends.entity.enums;

/**
 * 연령대를 나타내는 Enum
 */
public enum AgeRange {
    TEENS("10대"),
    TWENTIES("20대"),
    THIRTIES("30대"),
    FORTIES("40대"),
    FIFTIES("50대"),
    SIXTIES_PLUS("60대 이상");

    private final String description;

    AgeRange(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}