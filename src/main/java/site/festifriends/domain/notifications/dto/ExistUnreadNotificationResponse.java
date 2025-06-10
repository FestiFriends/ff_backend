package site.festifriends.domain.notifications.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExistUnreadNotificationResponse {

    @JsonProperty("hasUnread")
    private Boolean hasUnread;
}
