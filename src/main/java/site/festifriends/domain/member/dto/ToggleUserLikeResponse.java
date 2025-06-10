package site.festifriends.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ToggleUserLikeResponse {

    @JsonProperty("isLiked")
    private Boolean isLiked;
    private Long userId;

}
