package site.festifriends.domain.application.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;

@Getter
@Builder
public class AppliedListResponse {

    private String applicationId;
    private String performanceId;
    private String poster;
    private String groupId;
    private String groupName;
    private GroupCategory category;
    private String hostName;
    private Double hostRating;
    private String hostProfileImage;
    private Gender gender;
    private String applicationText;
    private LocalDateTime createdAt;
    private ApplicationStatus status;
} 
