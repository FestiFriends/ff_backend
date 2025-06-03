package site.festifriends.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.group.repository.GroupRepository;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.domain.review.dto.CreateReviewRequest;
import site.festifriends.domain.review.dto.UserReviewResponse;
import site.festifriends.domain.review.dto.WritableReviewRequest;
import site.festifriends.domain.review.dto.WritableReviewResponse;
import site.festifriends.domain.review.dto.WrittenReviewRequest;
import site.festifriends.domain.review.dto.WrittenReviewResponse;
import site.festifriends.domain.review.repository.ReviewRepository;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberGroup;
import site.festifriends.entity.Performance;
import site.festifriends.entity.Review;
import site.festifriends.entity.enums.ReviewTag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

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
                .map(this::convertToUserReviewInfo)
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

    /**
     * 리뷰 작성
     */
    @Transactional
    public void createReview(Long reviewerId, CreateReviewRequest request) {
        // 1. 기본 유효성 검증
        Long groupId = Long.parseLong(request.getGroupId());
        Long targetUserId = Long.parseLong(request.getTargetUserId());

        // 자기 자신에게 리뷰 작성 방지
        if (reviewerId.equals(targetUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_REVIEW_SELF);
        }

        // 2. 모임 존재 여부 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        // 3. 리뷰 대상 사용자 존재 여부 확인
        Member targetUser = memberRepository.findById(targetUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TARGET_USER_NOT_FOUND));

        Member reviewer = memberRepository.findById(reviewerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        // 4. 현재 로그인한 사용자가 모임에 참여했는지 확인
        if (!reviewRepository.isUserParticipantInGroup(reviewerId, groupId)) {
            throw new BusinessException(ErrorCode.NOT_GROUP_PARTICIPANT);
        }

        // 5. 리뷰 대상자도 모임에 참여했는지 확인
        if (!reviewRepository.isUserParticipantInGroup(targetUserId, groupId)) {
            throw new BusinessException(ErrorCode.NOT_GROUP_PARTICIPANT);
        }

        // 6. 모임이 종료되었는지 확인
        if (group.getEndDate().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.GROUP_NOT_ENDED);
        }

        // 7. 이미 해당 대상자에게 리뷰를 작성했는지 확인
        if (reviewRepository.existsByReviewerIdAndRevieweeIdAndGroupId(reviewerId, targetUserId, groupId)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 8. 리뷰 저장
        Review review = Review.builder()
            .reviewer(reviewer)
            .reviewee(targetUser)
            .group(group)
            .content(request.getContent())
            .score(request.getRating())
            .tags(request.getDefaultTag())
            .build();

        reviewRepository.save(review);
    }

    /**
     * 내가 작성한 리뷰 목록 조회 (커서 기반 페이지네이션)
     */
    public CursorResponseWrapper<WrittenReviewResponse> getWrittenReviews(Long reviewerId,
        WrittenReviewRequest request) {
        int size = request.getSize() != null ? request.getSize() : 20;
        List<Review> reviews = reviewRepository.findWrittenReviewsByReviewerId(
            reviewerId, request.getCursorId(), size);

        boolean hasNext = reviews.size() > size;
        if (hasNext) {
            reviews = reviews.subList(0, size);
        }

        Map<Long, List<Review>> groupedByGroup = reviews.stream()
            .collect(Collectors.groupingBy(review -> review.getGroup().getId()));

        List<WrittenReviewResponse> responses = new ArrayList<>();
        Long lastCursorId = null;

        for (Map.Entry<Long, List<Review>> entry : groupedByGroup.entrySet()) {
            List<Review> groupReviews = entry.getValue();
            Review firstReview = groupReviews.get(0);
            Group group = firstReview.getGroup();

            List<WrittenReviewResponse.ReviewInfo> reviewInfos = groupReviews.stream()
                .map(this::convertToWrittenReviewInfo)
                .collect(Collectors.toList());

            WrittenReviewResponse.PerformanceInfo performanceInfo = null;
            Performance performance = group.getPerformance();
            if (performance != null) {
                performanceInfo = WrittenReviewResponse.PerformanceInfo.builder()
                    .id(performance.getId().toString())
                    .title(performance.getTitle())
                    .poster(performance.getPoster())
                    .build();
            }

            WrittenReviewResponse response = WrittenReviewResponse.builder()
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
            lastCursorId = group.getId();
        }

        return CursorResponseWrapper.success(
            "내가 작성한 리뷰 조회 성공",
            responses,
            lastCursorId,
            hasNext
        );
    }

    /**
     * 작성 가능한 리뷰 목록 조회 (커서 기반 페이지네이션)
     */
    public CursorResponseWrapper<WritableReviewResponse> getWritableReviews(Long userId,
        WritableReviewRequest request) {
        int size = request.getSize() != null ? request.getSize() : 20;
        List<Object[]> groupResults = reviewRepository.findWritableReviewGroups(userId, request.getCursorId(), size);

        boolean hasNext = groupResults.size() > size;
        if (hasNext) {
            groupResults = groupResults.subList(0, size);
        }

        List<WritableReviewResponse> responses = new ArrayList<>();
        Long lastCursorId = null;

        for (Object[] result : groupResults) {
            Group group = (Group) result[0];
            Performance performance = (Performance) result[1];

            // 해당 모임에서 아직 리뷰를 작성하지 않은 멤버들 조회
            List<Member> unreviewedMembers = reviewRepository.findUnreviewedMembersInGroup(userId, group.getId());

            if (!unreviewedMembers.isEmpty()) {
                List<WritableReviewResponse.ReviewTargetInfo> reviewTargets = unreviewedMembers.stream()
                    .map(member -> WritableReviewResponse.ReviewTargetInfo.builder()
                        .targetUserId(member.getId().toString())
                        .targetUserProfileImage(member.getProfileImageUrl())
                        .targetUserName(member.getNickname())
                        .build())
                    .collect(Collectors.toList());

                WritableReviewResponse.PerformanceInfo performanceInfo = null;
                if (performance != null) {
                    performanceInfo = WritableReviewResponse.PerformanceInfo.builder()
                        .id(performance.getId().toString())
                        .title(performance.getTitle())
                        .poster(performance.getPoster())
                        .build();
                }

                WritableReviewResponse response = WritableReviewResponse.builder()
                    .groupId(group.getId().toString())
                    .performance(performanceInfo)
                    .groupTitle(group.getTitle())
                    .category(group.getGatherType())
                    .memberCount(group.getCount())
                    .groupStartDate(group.getStartDate())
                    .groupEndDate(group.getEndDate())
                    .reviews(reviewTargets)
                    .build();

                responses.add(response);
                lastCursorId = group.getId();
            }
        }

        return CursorResponseWrapper.success(
            "작성 가능한 리뷰 목록 조회 성공",
            responses,
            lastCursorId,
            hasNext
        );
    }

    private UserReviewResponse.ReviewInfo convertToUserReviewInfo(Review review) {
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

    private WrittenReviewResponse.ReviewInfo convertToWrittenReviewInfo(Review review) {
        Integer rating = review.getScore() != null ? (int) Math.round(review.getScore()) : 0;

        List<String> tags = review.getTags() != null
            ? review.getTags().stream()
            .map(ReviewTag::name)
            .collect(Collectors.toList())
            : new ArrayList<>();

        Member reviewee = review.getReviewee();

        return WrittenReviewResponse.ReviewInfo.builder()
            .reviewId(review.getId().toString())
            .targetUserId(reviewee.getId().toString())
            .targetUserName(reviewee.getNickname())
            .targetUserProfileImage(reviewee.getProfileImageUrl())
            .rating(rating)
            .content(review.getContent())
            .defaultTag(tags)
            .createdAt(review.getCreatedAt())
            .build();
    }
}
