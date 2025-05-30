package site.festifriends.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 북마크 타입을 나타내는 Enum
 */
@Getter
@AllArgsConstructor
public enum BookmarkType {
    MEMBER("회원"),
    PERFORMANCE("공연");

    private final String type;
}
