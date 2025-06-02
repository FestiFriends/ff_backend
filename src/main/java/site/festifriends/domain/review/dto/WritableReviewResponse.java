package site.festifriends.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.GroupCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "작성 가능한 리뷰 목록 응답")
public class WritableReviewResponse {

    @Schema(description = "모임 ID", example = "group-901")
    private String groupId;

    @Schema(description = "공연 정보")
    private PerformanceInfo performance;

    @Schema(description = "모임 이름", example = "클래식 좋아하는 분들 모여요")
    private String groupTitle;

    @Schema(description = "모임 카테고리", example = "COMPANION")
    private GroupCategory category;

    @Schema(description = "그룹 인원 수", example = "4")
    private Integer memberCount;

    @Schema(description = "모임 시작 날짜", example = "2025-06-18T19:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime groupStartDate;

    @Schema(description = "모임 종료 날짜", example = "2025-06-18T22:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime groupEndDate;

    @Schema(description = "리뷰 작성 대상 사용자 목록")
    private List<ReviewTargetInfo> reviews;

    @Getter
    @Builder
    @Schema(description = "공연 정보")
    public static class PerformanceInfo {

        @Schema(description = "공연 ID", example = "perf-005")
        private String id;

        @Schema(description = "공연 이름", example = "클래식 나잇 콘서트")
        private String title;

        @Schema(description = "포스터 이미지 URL", example = "https://example.com/posters/classicnight.jpg")
        private String poster;
    }

    @Getter
    @Builder
    @Schema(description = "리뷰 작성 대상 사용자 정보")
    public static class ReviewTargetInfo {

        @Schema(description = "리뷰 대상 사용자 ID", example = "user-201")
        private String targetUserId;

        @Schema(description = "리뷰 대상 사용자 프로필 이미지", example = "https://example.com/posters/profile.jpg")
        private String targetUserProfileImage;

        @Schema(description = "리뷰 대상 사용자 이름", example = "박지은")
        private String targetUserName;
    }
}
