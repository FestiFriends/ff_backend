package site.festifriends.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ToggleUserLikeRequest {

    @NotNull
    private Boolean isLiked;

}
