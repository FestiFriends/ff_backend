package site.festifriends.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMyIdResponse {

    private Long userId;

}
