package site.festifriends.domain.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationStatusResponse {
    private Boolean result;
} 