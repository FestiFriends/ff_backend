package site.festifriends.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ToggleUserLikeRequest {

    @NotEmpty
    private Boolean isLiked;

}
