package site.festifriends.entity.enums;

/**
 * 공연 상태를 나타내는 Enum
 */
public enum PerformanceState {
    UPCOMING("공연 예정"),
    ONGOING("공연 중"),
    COMPLETED("공연 종료");

    private final String description;

    PerformanceState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}