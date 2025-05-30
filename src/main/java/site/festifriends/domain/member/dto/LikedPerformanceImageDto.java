package site.festifriends.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LikedPerformanceImageDto {

    private String id;
    private String src;
    private String alt;
}
