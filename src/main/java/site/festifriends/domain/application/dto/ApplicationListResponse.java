package site.festifriends.domain.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;

@Getter
@Builder
public class ApplicationListResponse {

    private String groupId;
    private String groupName;
    private String poster;
    private GroupCategory category;
    private List<ApplicationInfo> applications;

    @Getter
    @Builder
    public static class ApplicationInfo {

        private String applicationId;
        private String userId;
        private String nickname;
        private Double rating;
        private Gender gender;
        private Integer age;
        private String profileImage;
        private String applicationText;
        private LocalDateTime createdAt;
        private ApplicationStatus status;
    }
} 
