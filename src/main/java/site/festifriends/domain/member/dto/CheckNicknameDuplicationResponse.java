package site.festifriends.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckNicknameDuplicationResponse {

    @JsonProperty("isAvailable")
    private Boolean isAvailable;
}
