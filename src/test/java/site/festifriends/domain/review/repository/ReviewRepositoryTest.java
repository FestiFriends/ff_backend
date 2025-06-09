package site.festifriends.domain.review.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import site.festifriends.common.config.AuditConfig;
import site.festifriends.common.config.QueryDslConfig;
import site.festifriends.domain.group.repository.GroupRepository;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.Review;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;
import site.festifriends.entity.enums.ReviewTag;

@DataJpaTest
@Import({QueryDslConfig.class, AuditConfig.class})
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupRepository groupRepository;

    private Member reviewer;
    private Member reviewee;
    private Group group;
    private Review review1;
    private Review review2;

    @BeforeEach
    void setUp() {
        reviewer = memberRepository.save(Member.builder()
            .email("reviewer@example.com")
            .nickname("리뷰어")
            .age(28)
            .gender(Gender.MALE)
            .introduction("리뷰어입니다")
            .tags(List.of("음악", "여행"))
            .sns(List.of("github", "instagram"))
            .socialId("socialId1")
            .build());

        reviewee = memberRepository.save(Member.builder()
            .email("reviewee@example.com")
            .nickname("리뷰이")
            .age(28)
            .gender(Gender.MALE)
            .introduction("리뷰이입니다")
            .tags(List.of("피아노", "기타"))
            .sns(List.of("github", "instagram"))
            .socialId("socialId2")
            .build());

        group = groupRepository.save(Group.builder()
            .title("테스트 그룹")
            .genderType(Gender.ALL)
            .startAge(20)
            .endAge(90)
            .gatherType(GroupCategory.COMPANION)
            .startDate(java.time.LocalDateTime.now())
            .endDate(java.time.LocalDateTime.now().plusDays(1))
            .location("테스트 장소")
            .count(10)
            .introduction("테스트 소개")
            .build());

        review1 = reviewRepository.save(Review.builder()
            .reviewer(reviewer)
            .reviewee(reviewee)
            .group(group)
            .content("리뷰 내용 1")
            .score(3.5)
            .tags(List.of(ReviewTag.COMFORTABLE, ReviewTag.CLEAN))
            .build());

        review2 = reviewRepository.save(Review.builder()
            .reviewer(reviewer)
            .reviewee(reviewee)
            .group(group)
            .content("리뷰 내용 2")
            .score(4.0)
            .tags(List.of(ReviewTag.COMFORTABLE, ReviewTag.RESPONSIVE))
            .build());
    }

    @Test
    @DisplayName("[성공] 타겟 회원 리뷰 개수 조회")
    void countReviewsByTargetMember() {
        // given
        Long targetMemberId = reviewee.getId();

        // when
        Object[] count = reviewRepository.getMemberReviewCount(targetMemberId);

        // then
        assertThat(((BigDecimal) count[0]).doubleValue()).isEqualTo(3.75);
        assertThat(count[1]).isEqualTo(2L);
    }

    @Test
    @DisplayName("[성공] 타겟 회원 리뷰 태그 개수 조회")
    void countEachReviewTagByTargetMember() {
        // given
        Long targetMemberId = reviewee.getId();

        // when
        List<Object[]> tagCounts = reviewRepository.countEachReviewTag(targetMemberId);
        Map<ReviewTag, Integer> tagCountMap = new EnumMap<>(ReviewTag.class);
        for (Object[] row : tagCounts) {
            ReviewTag tag = ReviewTag.valueOf((String) row[0]);
            Integer count = ((Number) row[1]).intValue();
            tagCountMap.put(tag, count);
        }

        // then
        assertThat(tagCounts).hasSize(3);
        assertThat(tagCountMap.get(ReviewTag.COMFORTABLE)).isEqualTo(2);
        assertThat(tagCountMap.get(ReviewTag.CLEAN)).isEqualTo(1);
        assertThat(tagCountMap.get(ReviewTag.RESPONSIVE)).isEqualTo(1);
    }

    @Test
    @DisplayName("[성공] 타겟 회원 리뷰 내용 조회")
    void getReviewContentByTargetMember() {
        // given
        Long targetMemberId = reviewee.getId();

        // when
        List<String> reviewContents = reviewRepository.getMemberReviewContent(targetMemberId);

        // then
        assertThat(reviewContents).hasSize(2);
        assertThat(reviewContents).containsExactlyInAnyOrder("리뷰 내용 1", "리뷰 내용 2");
    }
}
