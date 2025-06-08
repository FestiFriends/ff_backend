package site.festifriends.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikedMemberCountResponse {

    private Long favoriteCount;

}
