package site.festifriends.domain.group.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import site.festifriends.common.config.AuditConfig;
import site.festifriends.common.config.QueryDslConfig;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.domain.performance.repository.PerformanceRepository;
import site.festifriends.entity.*;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;
import site.festifriends.entity.enums.PerformanceState;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, AuditConfig.class})
@ActiveProfiles("test")
class GroupBookmarkRepositoryTest {

    @Autowired
    private GroupBookmarkRepository groupBookmarkRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    private Member member1;
    private Member member2;
    private Group group1;
    private Group group2;
    private Group group3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        Performance performance = performanceRepository.save(createPerformance("테스트 공연"));
        
        // 멤버 생성
        member1 = memberRepository.save(createMember("user1@example.com", "User1"));
        member2 = memberRepository.save(createMember("user2@example.com", "User2"));
        
        // 그룹 생성
        group1 = groupRepository.save(createGroup("그룹 1", performance));
        group2 = groupRepository.save(createGroup("그룹 2", performance));
        group3 = groupRepository.save(createGroup("그룹 3", performance));
        
        // 북마크 생성
        groupBookmarkRepository.save(new GroupBookmark(member1, group1));
        groupBookmarkRepository.save(new GroupBookmark(member1, group3));
        groupBookmarkRepository.save(new GroupBookmark(member2, group2));
    }

    @Test
    @DisplayName("멤버 ID와 그룹 ID로 북마크 조회")
    void findByMemberIdAndGroupId() {
        // when
        Optional<GroupBookmark> bookmark1 = groupBookmarkRepository.findByMemberIdAndGroupId(member1.getId(), group1.getId());
        Optional<GroupBookmark> bookmark2 = groupBookmarkRepository.findByMemberIdAndGroupId(member1.getId(), group2.getId());
        
        // then
        assertThat(bookmark1).isPresent();
        assertThat(bookmark2).isEmpty();
    }

    @Test
    @DisplayName("멤버 ID와 그룹 ID 목록으로 북마크된 그룹 ID 목록 조회")
    void findBookmarkedGroupIdsByMemberIdAndGroupIds() {
        // given
        List<Long> groupIds = Arrays.asList(group1.getId(), group2.getId(), group3.getId());
        
        // when
        List<Long> bookmarkedGroupIds1 = groupBookmarkRepository.findBookmarkedGroupIdsByMemberIdAndGroupIds(member1.getId(), groupIds);
        List<Long> bookmarkedGroupIds2 = groupBookmarkRepository.findBookmarkedGroupIdsByMemberIdAndGroupIds(member2.getId(), groupIds);
        
        // then
        assertThat(bookmarkedGroupIds1).hasSize(2);
        assertThat(bookmarkedGroupIds1).containsExactlyInAnyOrder(group1.getId(), group3.getId());
        
        assertThat(bookmarkedGroupIds2).hasSize(1);
        assertThat(bookmarkedGroupIds2).containsExactly(group2.getId());
    }

    @Test
    @DisplayName("멤버 ID와 그룹 ID로 북마크 삭제")
    void deleteByMemberIdAndGroupId() {
        // when
        groupBookmarkRepository.deleteByMemberIdAndGroupId(member1.getId(), group1.getId());
        
        // then
        Optional<GroupBookmark> bookmark = groupBookmarkRepository.findByMemberIdAndGroupId(member1.getId(), group1.getId());
        assertThat(bookmark).isEmpty();
    }

    private Member createMember(String email, String nickname) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .profileImageUrl("profile.jpg")
                .age(25)
                .gender(Gender.MALE)
                .socialId("social123")
                .build();
    }

    private Performance createPerformance(String title) {
        return Performance.builder()
                .title(title)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .location("서울")
                .poster("poster.jpg")
                .state(PerformanceState.UPCOMING)
                .visit("국내")
                .build();
    }

    private Group createGroup(String title, Performance performance) {
        return Group.builder()
                .title(title)
                .genderType(Gender.ALL)
                .startAge(20)
                .endAge(30)
                .gatherType(GroupCategory.COMPANION)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .location("서울")
                .count(10)
                .introduction("모임 소개")
                .performance(performance)
                .build();
    }
}