package site.festifriends.domain.review.repository;

import java.util.List;
import site.festifriends.entity.Member;
import site.festifriends.entity.Review;

public interface ReviewRepositoryCustom {

    /**
     * 최근 작성된 리뷰 기준으로 모임을 조회 (TOP N)
     */
    List<Review> findRecentReviews(int limit);

    /**
     * 특정 사용자가 받은 리뷰들을 그룹별로 조회
     */
    List<Review> findUserReviewsByRevieweeId(Long revieweeId);

    /**
     * 특정 사용자가 작성한 리뷰들을 커서 기반 페이지네이션으로 조회
     */
    List<Review> findWrittenReviewsByReviewerId(Long reviewerId, Long cursorId, int size);

    /**
     * 특정 사용자가 특정 모임에서 특정 대상자에게 이미 리뷰를 작성했는지 확인
     */
    boolean existsByReviewerIdAndRevieweeIdAndGroupId(Long reviewerId, Long revieweeId, Long groupId);

    /**
     * 특정 사용자가 특정 모임에 참여했는지 확인
     */
    boolean isUserParticipantInGroup(Long userId, Long groupId);

    /**
     * 사용자가 참여한 모임 중 리뷰를 작성하지 않은 대상자들이 있는 모임 조회
     */
    List<Object[]> findWritableReviewGroups(Long userId, Long cursorId, int size);

    /**
     * 특정 모임에서 사용자가 리뷰를 작성하지 않은 대상자들 조회
     */
    List<Member> findUnreviewedMembersInGroup(Long userId, Long groupId);

    /**
     * 특정 사용자의 리뷰 평점 및 리뷰 달린 개수 조회
     */
    Object[] getMemberReviewCount(Long targetId);

    /**
     * 특정 사용자의 리뷰 태그 종류별 개수 조회
     */
    List<Object[]> countEachReviewTag(Long targetId);

    /**
     * 특정 사용자의 리뷰 내용 조회
     */
    List<String> getMemberReviewContent(Long targetId);
}
