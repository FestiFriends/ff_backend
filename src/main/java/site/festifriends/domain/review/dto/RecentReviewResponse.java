package site.festifriends.domain.review.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.GroupCategory;
import site.festifriends.entity.enums.ReviewTag;

@Getter
@Builder
public class RecentReviewResponse {

    private String groupId;
    private Performance performance;
    private String groupTitle;
    private GroupCategory category;
    private String groupStartDate;
    private String groupEndDate;
    private List<ReviewInfo> reviews;

    @Getter
    @Builder
    public static class Performance {

        private String id;
        private String title;
        private String poster;
    }

    @Getter
    @Builder
    public static class ReviewInfo {

        private String reviewId;
        private Double rating;
        private String content;
        private List<ReviewTag> defaultTag;
        private LocalDateTime createdAt;
    }
}
