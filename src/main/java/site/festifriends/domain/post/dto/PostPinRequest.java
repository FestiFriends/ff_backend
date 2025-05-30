package site.festifriends.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostPinRequest {

    @JsonProperty("isPinned")
    private Boolean isPinned;

    public Boolean getIsPinned() {
        return isPinned;
    }
}
