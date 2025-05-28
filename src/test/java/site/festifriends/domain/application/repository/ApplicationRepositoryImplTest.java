package site.festifriends.domain.application.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import site.festifriends.common.config.AuditConfig;
import site.festifriends.common.config.QueryDslConfig;
import site.festifriends.entity.*;
import site.festifriends.entity.enums.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({QueryDslConfig.class, AuditConfig.class})
@ActiveProfiles("test")
class ApplicationRepositoryImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Member host1;
    private Member host2;
    private Festival festival1;
    private Festival festival2;
    private Party party1;
    private Party party2;
    private Party party3;

    @BeforeEach
    void setUp() {
        // 호스트들 생성
        host1 = createMember("host1", "테스트호스트1", Gender.MALE, 25);
        host2 = createMember("host2", "테스트호스트2", Gender.FEMALE, 30);

        // 페스티벌 생성
        festival1 = createFestival("테스트 콘서트", "테스트 공연장");
        festival2 = createFestival("다른 콘서트", "다른 공연장");

        // 모임 생성
        party1 = createParty("첫번째 모임", festival1);
        party2 = createParty("두번째 모임", festival1);
        party3 = createParty("세번째 모임", festival2);

        // 호스트 관계 생성
        createHostRelation(host1, party1);
        createHostRelation(host1, party2);
        createHostRelation(host2, party3);

        // 신청자들과 신청서 생성
        Member applicant1 = createMember("applicant1", "신청자1", Gender.FEMALE, 23);
        Member applicant2 = createMember("applicant2", "신청자2", Gender.MALE, 27);
        Member applicant3 = createMember("applicant3", "신청자3", Gender.FEMALE, 29);
        Member applicant4 = createMember("applicant4", "신청자4", Gender.MALE, 26);

        // PENDING 상태 신청서들
        createApplication(applicant1, party1, "첫번째 모임에 신청합니다!", ApplicationStatus.PENDING);
        createApplication(applicant2, party1, "두번째 신청자입니다!", ApplicationStatus.PENDING);
        createApplication(applicant3, party2, "두번째 모임에 참여하고 싶어요!", ApplicationStatus.PENDING);
        createApplication(applicant4, party3, "세번째 모임에 신청합니다!", ApplicationStatus.PENDING);

        // 다른 상태의 신청서들 (필터링 테스트용)
        createApplication(applicant1, party2, "승인된 신청서", ApplicationStatus.APPROVED);
        createApplication(applicant2, party2, "거절된 신청서", ApplicationStatus.REJECTED);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("호스트의 모임에 대한 PENDING 상태 신청서들을 조회한다")
    void findApplicationsWithSlice_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberParty> result = applicationRepository.findApplicationsWithSlice(host1.getId(), null, pageable);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.hasNext()).isFalse();

        List<MemberParty> applications = result.getContent();

        // ID 내림차순으로 정렬되어야 함
        assertThat(applications.get(0).getId()).isGreaterThan(applications.get(1).getId());
        assertThat(applications.get(1).getId()).isGreaterThan(applications.get(2).getId());

        // 모든 신청서는 PENDING 상태여야 함
        assertThat(applications).allMatch(app -> app.getStatus() == ApplicationStatus.PENDING);

        // 연관 엔티티들이 페치조인으로 로딩되어야 함
        assertThat(applications.get(0).getMember()).isNotNull();
        assertThat(applications.get(0).getParty()).isNotNull();
        assertThat(applications.get(0).getParty().getFestival()).isNotNull();
    }

    @Test
    @DisplayName("커서 기반 페이징이 정상적으로 동작한다")
    void findApplicationsWithSlice_CursorPaging() {
        // given
        Pageable pageable = PageRequest.of(0, 2);

        // when - 첫 번째 페이지 조회
        Slice<MemberParty> firstPage = applicationRepository.findApplicationsWithSlice(host1.getId(), null, pageable);

        // then - 첫 번째 페이지 검증
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.hasNext()).isTrue();

        // when - 두 번째 페이지 조회 (커서 사용)
        Long cursorId = firstPage.getContent().get(1).getId();
        Slice<MemberParty> secondPage = applicationRepository.findApplicationsWithSlice(host1.getId(), cursorId, pageable);

        // then - 두 번째 페이지 검증
        assertThat(secondPage.getContent()).hasSize(1);
        assertThat(secondPage.hasNext()).isFalse();
        assertThat(secondPage.getContent().get(0).getId()).isLessThan(cursorId);
    }

    @Test
    @DisplayName("PENDING이 아닌 상태의 신청서는 조회되지 않는다")
    void findApplicationsWithSlice_FilterByStatus() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberParty> result = applicationRepository.findApplicationsWithSlice(host1.getId(), null, pageable);

        // then - PENDING 상태의 신청서만 조회되어야 함
        assertThat(result.getContent()).allMatch(app -> app.getStatus() == ApplicationStatus.PENDING);
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("삭제된 신청서는 조회되지 않는다")
    void findApplicationsWithSlice_FilterDeleted() {
        // given
        Slice<MemberParty> beforeDelete = applicationRepository.findApplicationsWithSlice(host1.getId(), null, PageRequest.of(0, 10));
        MemberParty memberParty = beforeDelete.getContent().get(0);
        memberParty.delete();
        entityManager.merge(memberParty);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberParty> result = applicationRepository.findApplicationsWithSlice(host1.getId(), null, pageable);

        // then - 삭제되지 않은 신청서만 조회되어야 함
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).noneMatch(app -> app.getId().equals(memberParty.getId()));
    }

    @Test
    @DisplayName("다른 호스트의 모임 신청서는 조회되지 않는다")
    void findApplicationsWithSlice_FilterByHost() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberParty> host1Result = applicationRepository.findApplicationsWithSlice(host1.getId(), null, pageable);
        Slice<MemberParty> host2Result = applicationRepository.findApplicationsWithSlice(host2.getId(), null, pageable);

        // then
        assertThat(host1Result.getContent()).hasSize(3);
        assertThat(host2Result.getContent()).hasSize(1);

        // 각 호스트는 자신의 모임 신청서만 조회해야 함
        assertThat(host1Result.getContent()).allMatch(app ->
            app.getParty().getId().equals(party1.getId()) || app.getParty().getId().equals(party2.getId()));

        assertThat(host2Result.getContent()).allMatch(app ->
            app.getParty().getId().equals(party3.getId()));
    }

    @Test
    @DisplayName("빈 결과에 대해서도 정상적으로 Slice를 반환한다")
    void findApplicationsWithSlice_EmptyResult() {
        // given
        Member nonExistentHost = createMember("nonexistent", "존재하지않는호스트", Gender.MALE, 25);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberParty> result = applicationRepository.findApplicationsWithSlice(nonExistentHost.getId(), null, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("방장 권한을 정확히 확인한다")
    void existsByPartyIdAndMemberIdAndRole_Host() {
        // when & then
        assertThat(applicationRepository.existsByPartyIdAndMemberIdAndRole(party1.getId(), host1.getId(), Role.HOST))
                .isTrue();
        assertThat(applicationRepository.existsByPartyIdAndMemberIdAndRole(party2.getId(), host1.getId(), Role.HOST))
                .isTrue();
        assertThat(applicationRepository.existsByPartyIdAndMemberIdAndRole(party3.getId(), host1.getId(), Role.HOST))
                .isFalse();
        assertThat(applicationRepository.existsByPartyIdAndMemberIdAndRole(party3.getId(), host2.getId(), Role.HOST))
                .isTrue();
    }

    @Test
    @DisplayName("일반 멤버는 방장 권한이 없다")
    void existsByPartyIdAndMemberIdAndRole_NotHost() {
        // given
        Member applicant = createMember("test_applicant", "테스트신청자", Gender.MALE, 25);

        // when & then
        assertThat(applicationRepository.existsByPartyIdAndMemberIdAndRole(party1.getId(), applicant.getId(), Role.HOST))
                .isFalse();
    }

    @Test
    @DisplayName("삭제된 방장 관계는 권한 확인에서 제외된다")
    void existsByPartyIdAndMemberIdAndRole_DeletedHost() {
        // given
        List<MemberParty> hostRelations = entityManager.getEntityManager()
                .createQuery("SELECT mp FROM MemberParty mp WHERE mp.member.id = :hostId AND mp.party.id = :partyId AND mp.role = :role", MemberParty.class)
                .setParameter("hostId", host1.getId())
                .setParameter("partyId", party1.getId())
                .setParameter("role", Role.HOST)
                .getResultList();
        
        if (!hostRelations.isEmpty()) {
            MemberParty hostRelation = hostRelations.get(0);
            hostRelation.delete();
            entityManager.merge(hostRelation);
            entityManager.flush();
        }

        // when & then
        assertThat(applicationRepository.existsByPartyIdAndMemberIdAndRole(party1.getId(), host1.getId(), Role.HOST))
                .isFalse();
    }

    // ===== Private Helper Methods =====

    private Member createMember(String socialId, String nickname, Gender gender, Integer age) {
        Member member = Member.builder()
            .socialId(socialId)
            .nickname(nickname)
            .email(nickname + "@test.com")
            .profileImageUrl("http://test.com/" + nickname + ".jpg")
            .age(age)
            .gender(gender)
            .introduction(nickname + "입니다")
            .build();
        return entityManager.persistAndFlush(member);
    }

    private Festival createFestival(String title, String location) {
        Date startDate = new Date();
        Date endDate = new Date();
        Festival festival = Festival.builder()
            .title(title)
            .posterUrl("http://test.com/poster.jpg")
            .startDate(startDate)
            .endDate(endDate)
            .location(location)
            .price(50000)
            .state(FestivalState.UPCOMING)
            .visit(false)
            .build();
        return entityManager.persistAndFlush(festival);
    }

    private Party createParty(String title, Festival festival) {
        LocalDateTime gatherDate = LocalDateTime.now().plusDays(1);
        Party party = Party.builder()
            .title(title)
            .genderType(Gender.ALL)
            .ageRange(AgeRange.TWENTIES)
            .gatherType(GroupCategory.COMPANION)
            .gatherDate(gatherDate)
            .location("테스트 장소")
            .count(4)
            .introduction(title + " 소개")
            .festival(festival)
            .build();
        return entityManager.persistAndFlush(party);
    }

    private void createHostRelation(Member host, Party party) {
        MemberParty hostRelation = MemberParty.builder()
            .member(host)
            .party(party)
            .role(Role.HOST)
            .status(ApplicationStatus.APPROVED)
            .build();
        entityManager.persistAndFlush(hostRelation);
    }

    private void createApplication(Member member, Party party, String applicationText, ApplicationStatus status) {
        MemberParty application = MemberParty.builder()
            .member(member)
            .party(party)
            .role(Role.MEMBER)
            .status(status)
            .applicationText(applicationText)
            .build();
        entityManager.persistAndFlush(application);
    }

}
