package site.festifriends.domain.application.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import site.festifriends.common.config.AuditConfig;
import site.festifriends.common.config.QueryDslConfig;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberGroup;
import site.festifriends.entity.Performance;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;
import site.festifriends.entity.enums.PerformanceState;
import site.festifriends.entity.enums.Role;

@DataJpaTest
@Import({QueryDslConfig.class, AuditConfig.class})
@ActiveProfiles("test")
class ApplicationRepositoryJoinedTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Member member1;
    private Member member2;
    private Member host1;
    private Member host2;
    private Performance performance1;
    private Performance performance2;
    private Group group1;
    private Group group2;
    private Group group3;

    @BeforeEach
    void setUp() {
        // 멤버들 생성
        member1 = createMember("member1", "참가자1", Gender.FEMALE, 25);
        member2 = createMember("member2", "참가자2", Gender.MALE, 30);
        host1 = createMember("host1", "호스트1", Gender.FEMALE, 28);
        host2 = createMember("host2", "호스트2", Gender.MALE, 32);

        // 페스티벌 생성
        performance1 = createPerformance("테스트 콘서트 1", "테스트 공연장 1");
        performance2 = createPerformance("테스트 콘서트 2", "테스트 공연장 2");

        // 모임 생성
        group1 = createGroup("첫번째 모임", performance1, host1);
        group2 = createGroup("두번째 모임", performance1, host1);
        group3 = createGroup("세번째 모임", performance2, host2);

        // 호스트 관계 생성
        createHostRelation(host1, group1);
        createHostRelation(host1, group2);
        createHostRelation(host2, group3);

        // 참가 확정된 모임들 생성 (CONFIRMED 상태)
        createConfirmedMembership(member1, group1, "첫번째 모임 참가 확정");
        createConfirmedMembership(member1, group2, "두번째 모임 참가 확정");
        createConfirmedMembership(member2, group1, "첫번째 모임 참가 확정");

        // 다른 상태들도 생성 (테스트 검증용)
        createApplication(member1, group3, "세번째 모임 신청", ApplicationStatus.PENDING);
        createApplication(member2, group2, "두번째 모임 신청", ApplicationStatus.ACCEPTED);
        createApplication(member2, group3, "세번째 모임 신청", ApplicationStatus.REJECTED);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("참가 중인 모임 목록을 조회한다 - CONFIRMED 상태만")
    void findJoinedGroupsWithSlice_Success() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberGroup> result = applicationRepository.findJoinedGroupsWithSlice(member1.getId(), null, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isFalse();

        List<MemberGroup> joinedGroups = result.getContent();
        assertThat(joinedGroups).allMatch(mp -> mp.getStatus() == ApplicationStatus.CONFIRMED);
        assertThat(joinedGroups).allMatch(mp -> mp.getMember().getId().equals(member1.getId()));

        // 모임 정보 확인
        List<String> partyTitles = joinedGroups.stream()
            .map(mp -> mp.getGroup().getTitle())
            .toList();
        assertThat(partyTitles).containsExactlyInAnyOrder("첫번째 모임", "두번째 모임");
    }

    @Test
    @DisplayName("참가 중인 모임이 없는 경우 빈 결과를 반환한다")
    void findJoinedGroupsWithSlice_EmptyResult() {
        // given
        Member newMember = createMember("newMember", "새로운멤버", Gender.MALE, 25);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberGroup> result = applicationRepository.findJoinedGroupsWithSlice(newMember.getId(), null, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("커서 기반 페이지네이션이 정상 동작한다")
    void findJoinedGroupsWithSlice_CursorPagination() {
        // given
        // 추가 참가 확정 모임들 생성
        Group group4 = createGroup("네번째 모임", performance2, host2);
        createHostRelation(host2, group4);
        createConfirmedMembership(member1, group4, "네번째 모임 참가 확정");

        Group group5 = createGroup("다섯번째 모임", performance1, host1);
        createHostRelation(host1, group5);
        createConfirmedMembership(member1, group5, "다섯번째 모임 참가 확정");

        entityManager.flush();
        entityManager.clear();

        PageRequest pageable = PageRequest.of(0, 2);

        // when - 첫 번째 페이지
        Slice<MemberGroup> firstPage = applicationRepository.findJoinedGroupsWithSlice(member1.getId(), null, pageable);

        // then - 첫 번째 페이지 검증
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.hasNext()).isTrue();

        // when - 두 번째 페이지 (커서 사용)
        Long cursorId = firstPage.getContent().get(firstPage.getContent().size() - 1).getId();
        Slice<MemberGroup> secondPage = applicationRepository.findJoinedGroupsWithSlice(member1.getId(), cursorId, pageable);

        // then - 두 번째 페이지 검증
        assertThat(secondPage.getContent()).hasSize(2);
        assertThat(secondPage.hasNext()).isFalse();

        // 전체 결과 검증
        List<MemberGroup> allResults = new ArrayList<>(firstPage.getContent());
        allResults.addAll(secondPage.getContent());
        assertThat(allResults).hasSize(4);
        assertThat(allResults).allMatch(mp -> mp.getStatus() == ApplicationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("삭제된 모임은 조회되지 않는다")
    void findJoinedGroupsWithSlice_ExcludeDeleted() {
        // given
        MemberGroup confirmedMembership = createConfirmedMembership(member2, group3, "삭제될 모임 참가");
        confirmedMembership.delete(); // soft delete
        entityManager.persistAndFlush(confirmedMembership);
        entityManager.clear();

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberGroup> result = applicationRepository.findJoinedGroupsWithSlice(member2.getId(), null, pageable);

        // then
        assertThat(result.getContent()).hasSize(1); // 삭제된 것 제외하고 1개만
        assertThat(result.getContent().get(0).getGroup().getTitle()).isEqualTo("첫번째 모임");
    }

    @Test
    @DisplayName("페스티벌 정보가 함께 조회된다")
    void findJoinedGroupsWithSlice_WithFestivalInfo() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberGroup> result = applicationRepository.findJoinedGroupsWithSlice(member1.getId(), null, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);

        for (MemberGroup memberGroup : result.getContent()) {
            assertThat(memberGroup.getGroup().getPerformance()).isNotNull();
            assertThat(memberGroup.getGroup().getPerformance().getTitle()).isNotBlank();
            assertThat(memberGroup.getGroup().getPerformance().getPoster()).isNotBlank();
        }
    }

    private Member createMember(String socialId, String nickname, Gender gender, int age) {
        Member member = Member.builder()
            .socialId(socialId)
            .nickname(nickname)
            .email(socialId + "@test.com")
            .profileImageUrl("http://test.com/profile.jpg")
            .age(age)
            .gender(gender)
            .introduction("테스트 소개")
            .build();
        return entityManager.persistAndFlush(member);
    }

    private Performance createPerformance(String title, String location) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);
        Performance performance = Performance.builder()
            .title(title)
            .poster("http://test.com/poster.jpg")
            .startDate(startDate)
            .endDate(endDate)
            .location(location)
            .price(List.of("50000"))
            .state(PerformanceState.UPCOMING)
            .visit("false")
            .build();
        return entityManager.persistAndFlush(performance);
    }

    private Group createGroup(String title, Performance performance, Member host) {
        LocalDateTime gatherDate = LocalDateTime.now().plusDays(1);
        Group party = Group.builder()
            .title(title)
            .genderType(Gender.ALL)
            .startAge(20)
            .endAge(29)
            .gatherType(GroupCategory.COMPANION)
            .startDate(gatherDate)
            .endDate(gatherDate.plusDays(1))
            .location("테스트 장소")
            .count(4)
            .introduction(title + " 소개")
            .performance(performance)
            .build();
        return entityManager.persistAndFlush(party);
    }

    private void createHostRelation(Member host, Group group) {
        MemberGroup hostRelation = MemberGroup.builder()
            .member(host)
            .group(group)
            .role(Role.HOST)
            .status(ApplicationStatus.ACCEPTED)
            .build();
        entityManager.persistAndFlush(hostRelation);
    }

    private MemberGroup createConfirmedMembership(Member member, Group group, String applicationText) {
        MemberGroup membership = MemberGroup.builder()
            .member(member)
            .group(group)
            .role(Role.MEMBER)
            .status(ApplicationStatus.CONFIRMED)
            .applicationText(applicationText)
            .build();
        return entityManager.persistAndFlush(membership);
    }

    private void createApplication(Member member, Group party, String applicationText, ApplicationStatus status) {
        MemberGroup application = MemberGroup.builder()
            .member(member)
            .group(party)
            .role(Role.MEMBER)
            .status(status)
            .applicationText(applicationText)
            .build();
        entityManager.persistAndFlush(application);
    }
} 
