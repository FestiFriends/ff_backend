package site.festifriends.domain.application.dto;

import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Gender;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppliedListResponse {
    private String applicationId;
    private String performanceId;
    private String poster;
    private String groupId;
    private String groupName;
    private String leaderNickname;
    private Double leaderRating;
    private Gender gender;
    private String applicationText;
    private LocalDateTime createdAt;
    private ApplicationStatus status;
} 