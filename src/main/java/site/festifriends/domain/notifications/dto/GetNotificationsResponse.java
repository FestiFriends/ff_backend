package site.festifriends.domain.notifications.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetNotificationsResponse {

    private Long id;
    private String message;
    private String type;
    private TargetDto target;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
    @JsonProperty("isRead")
    private Boolean isRead;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TargetDto {

        private Long postId;
        private Long groupId;
    }
}
