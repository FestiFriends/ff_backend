package site.festifriends.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {
    USER("일반 사용자"),
    ADMIN("관리자");

    private final String description;
}
