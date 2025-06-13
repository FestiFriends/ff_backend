package site.festifriends.domain.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;

@Getter
@Builder
@AllArgsConstructor
public class ManagementApplicationResponse {

    private String groupId;
    private Performance performance;
    private String groupTitle;
    private GroupCategory category;
    private Integer memberCount;
    private Integer maxMembers;
    private String startDate;
    private String endDate;
    private List<ApplicationInfo> applications;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Performance {
        private String id;
        private String title;
        private String poster;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ApplicationInfo {
        private String applicationId;
        private String userId;
        private String userName;
        private Double rating;
        private Gender gender;
        private Integer age;
        private String profileImage;
        private String applicationText;
        private String createdAt;
        private ApplicationStatus status;
    }
}
