package site.festifriends.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.GroupCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class WrittenReviewResponse {

    private String groupId;
    private PerformanceInfo performance;
    private String groupTitle;
    private GroupCategory category;
    private Integer memberCount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime groupStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime groupEndDate;

    private List<ReviewInfo> reviews;

    @Getter
    @Builder
    public static class PerformanceInfo {

        private String id;
        private String title;
        private String poster;
    }

    @Getter
    @Builder
    public static class ReviewInfo {

        private String reviewId;
        private String targetUserId;
        private String targetUserName;
        private String targetUserProfileImage;
        private Integer rating;
        private String content;
        private List<String> defaultTag;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime createdAt;
    }
}
