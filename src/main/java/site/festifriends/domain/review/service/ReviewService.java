package site.festifriends.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.domain.review.dto.UserReviewResponse;
import site.festifriends.domain.review.repository.ReviewRepository;
import site.festifriends.entity.Group;
import site.festifriends.entity.Performance;
import site.festifriends.entity.Review;
import site.festifriends.entity.enums.ReviewTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * 사용자가 받은 리뷰 목록 조회
     */
    public List<UserReviewResponse> getUserReviews(Long userId) {
        List<Review> reviews = reviewRepository.findUserReviewsByRevieweeId(userId);
        
        Map<Long, List<Review>> groupedByGroup = reviews.stream()
                .collect(Collectors.groupingBy(review -> review.getGroup().getId()));

        List<UserReviewResponse> responses = new ArrayList<>();
        
        for (Map.Entry<Long, List<Review>> entry : groupedByGroup.entrySet()) {
            List<Review> groupReviews = entry.getValue();
            Review firstReview = groupReviews.get(0);
            Group group = firstReview.getGroup();
            
            List<UserReviewResponse.ReviewInfo> reviewInfos = groupReviews.stream()
                    .map(this::convertToReviewInfo)
                    .collect(Collectors.toList());

            UserReviewResponse.PerformanceInfo performanceInfo = null;
            Performance performance = group.getPerformance();
            if (performance != null) {
                performanceInfo = UserReviewResponse.PerformanceInfo.builder()
                        .id(performance.getId().toString())
                        .title(performance.getTitle())
                        .poster(performance.getPoster())
                        .build();
            }

            UserReviewResponse response = UserReviewResponse.builder()
                    .groupId(group.getId().toString())
                    .performance(performanceInfo)
                    .groupTitle(group.getTitle())
                    .category(group.getGatherType())
                    .memberCount(group.getCount())
                    .groupStartDate(group.getStartDate())
                    .groupEndDate(group.getEndDate())
                    .reviews(reviewInfos)
                    .build();

            responses.add(response);
        }

        return responses;
    }

    private UserReviewResponse.ReviewInfo convertToReviewInfo(Review review) {
        Integer rating = review.getScore() != null ? (int) Math.round(review.getScore()) : 0;
        
        List<String> tags = review.getTags() != null
                ? review.getTags().stream()
                    .map(ReviewTag::name)
                    .collect(Collectors.toList())
                : new ArrayList<>();

        return UserReviewResponse.ReviewInfo.builder()
                .reviewId(review.getId().toString())
                .rating(rating)
                .content(review.getContent())
                .defaultTag(tags)
                .createdAt(review.getCreatedAt())
                .build();
    }
}
