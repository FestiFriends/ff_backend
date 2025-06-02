package site.festifriends.domain.review.repository;

import site.festifriends.entity.Review;

import java.util.List;

public interface ReviewRepositoryCustom {
    
    /**
     * 특정 사용자가 받은 리뷰들을 그룹별로 조회
     */
    List<Review> findUserReviewsByRevieweeId(Long revieweeId);
}
