package site.festifriends.entity.enums;

/**
 * 리뷰 태그를 나타내는 Enum
 */
public enum ReviewTag {
    PUNCTUAL("시간 약속을 잘 지켜요"),
    POLITE("친절하고 매너가 좋아요"),
    COMFORTABLE("편안한 분위기였어요"),
    COMMUNICATIVE("대화가 잘 통했어요"),
    CLEAN("청결하고 깔끔했어요"),
    RESPONSIVE("소통이 잘 되고 응답이 빨라요"),
    RECOMMEND("다음에도 함께하고 싶어요");

    private final String description;

    ReviewTag(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}