package site.festifriends.domain.application.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
class ApplicationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Member host;
    private Member applicant;
    private Group group;
    private Performance performance;

    @BeforeEach
    void setUp() {
        host = Member.builder()
                .socialId("host1")
                .nickname("방장")
                .email("host@test.com")
                .age(25)
                .gender(Gender.MALE)
                .profileImageUrl("https://example.com/host.jpg")
                .build();
        entityManager.persistAndFlush(host);

        applicant = Member.builder()
                .socialId("applicant1")
                .nickname("신청자")
                .email("applicant@test.com")
                .age(23)
                .gender(Gender.FEMALE)
                .profileImageUrl("https://example.com/applicant.jpg")
                .build();
        entityManager.persistAndFlush(applicant);

        performance = Performance.builder()
                .title("테스트 페스티벌")
                .poster("https://example.com/poster.jpg")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(3))
                .location("서울시 강남구")
                .price(List.of("50000"))
                .state(PerformanceState.UPCOMING)
                .visit("true")
                .build();
        entityManager.persistAndFlush(performance);

        group = Group.builder()
                .title("테스트 모임")
                .genderType(Gender.ALL)
                .startAge(20)
                .endAge(29)
                .gatherType(GroupCategory.COMPANION)
                .startDate(LocalDateTime.now().plusDays(7))
                .endDate(LocalDateTime.now().plusDays(8))
                .location("서울시 강남구")
                .count(4)
                .introduction("함께 페스티벌을 즐겨요!")
                .performance(performance)
                .build();
        entityManager.persistAndFlush(group);

        // 방장 정보 저장
        MemberGroup hostMemberGroup = MemberGroup.builder()
                .member(host)
                .group(group)
                .role(Role.HOST)
                .status(ApplicationStatus.ACCEPTED)
                .build();
        entityManager.persistAndFlush(hostMemberGroup);

        // 신청자 정보 저장
        MemberGroup application = MemberGroup.builder()
                .member(applicant)
                .group(group)
                .role(Role.MEMBER)
                .status(ApplicationStatus.PENDING)
                .applicationText("참여하고 싶습니다!")
                .build();
        entityManager.persistAndFlush(application);

        entityManager.clear();
    }

    @Test
    @DisplayName("내가 신청한 모임 목록을 조회한다")
    void findAppliedApplicationsWithSlice_Success() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Slice<MemberGroup> result = applicationRepository
                .findAppliedApplicationsWithSlice(applicant.getId(), null, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.hasNext()).isFalse();

        MemberGroup memberGroup = result.getContent().get(0);
        assertThat(memberGroup.getMember().getId()).isEqualTo(applicant.getId());
        assertThat(memberGroup.getRole()).isEqualTo(Role.MEMBER);
        assertThat(memberGroup.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(memberGroup.getApplicationText()).isEqualTo("참여하고 싶습니다!");
    }

    @Test
    @DisplayName("파티별 방장 정보를 조회한다")
    void findHostsByPartyIds_Success() {
        // given
        List<Long> partyIds = List.of(group.getId());

        // when
        Map<Long, MemberGroup> hosts = applicationRepository.findHostsByGroupIds(partyIds);

        // then
        assertThat(hosts).hasSize(1);
        assertThat(hosts.get(group.getId())).isNotNull();

        MemberGroup hostInfo = hosts.get(group.getId());
        assertThat(hostInfo.getMember().getId()).isEqualTo(host.getId());
        assertThat(hostInfo.getRole()).isEqualTo(Role.HOST);
        assertThat(hostInfo.getMember().getNickname()).isEqualTo("방장");
    }

    @Test
    @DisplayName("방장 권한을 확인한다")
    void existsByPartyIdAndMemberIdAndRole_Host_Success() {
        // when
        boolean isHost = applicationRepository
                .existsByGroupIdAndMemberIdAndRole(group.getId(), host.getId(), Role.HOST);

        // then
        assertThat(isHost).isTrue();
    }

    @Test
    @DisplayName("일반 멤버는 방장 권한이 없다")
    void existsByPartyIdAndMemberIdAndRole_Member_False() {
        // when
        boolean isHost = applicationRepository
                .existsByGroupIdAndMemberIdAndRole(group.getId(), applicant.getId(), Role.HOST);

        // then
        assertThat(isHost).isFalse();
    }

    @Test
    @DisplayName("신청서 ID로 신청서를 조회한다")
    void findById_Success() {
        // given
        MemberGroup application = MemberGroup.builder()
                .member(applicant)
                .group(group)
                .role(Role.MEMBER)
                .status(ApplicationStatus.ACCEPTED)
                .applicationText("확정 테스트용 신청서")
                .build();
        MemberGroup savedApplication = entityManager.persistAndFlush(application);
        entityManager.clear();

        // when
        MemberGroup foundApplication = applicationRepository.findById(savedApplication.getId()).orElse(null);

        // then
        assertThat(foundApplication).isNotNull();
        assertThat(foundApplication.getId()).isEqualTo(savedApplication.getId());
        assertThat(foundApplication.getMember().getId()).isEqualTo(applicant.getId());
        assertThat(foundApplication.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        assertThat(foundApplication.getApplicationText()).isEqualTo("확정 테스트용 신청서");
    }

    @Test
    @DisplayName("신청서 상태를 CONFIRMED로 변경한다")
    void updateApplicationStatusToConfirmed_Success() {
        // given
        MemberGroup application = MemberGroup.builder()
                .member(applicant)
                .group(group)
                .role(Role.MEMBER)
                .status(ApplicationStatus.ACCEPTED)
                .applicationText("확정할 신청서")
                .build();
        MemberGroup savedApplication = entityManager.persistAndFlush(application);
        entityManager.clear();

        // when
        MemberGroup foundApplication = applicationRepository.findById(savedApplication.getId()).orElse(null);
        assertThat(foundApplication).isNotNull();

        foundApplication.confirm();
        entityManager.persistAndFlush(foundApplication);
        entityManager.clear();

        // then
        MemberGroup confirmedApplication = applicationRepository.findById(savedApplication.getId()).orElse(null);
        assertThat(confirmedApplication).isNotNull();
        assertThat(confirmedApplication.getStatus()).isEqualTo(ApplicationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("신청자 본인의 신청서인지 확인한다")
    void validateApplicantOwnership_Success() {
        // given
        MemberGroup application = MemberGroup.builder()
                .member(applicant)
                .group(group)
                .role(Role.MEMBER)
                .status(ApplicationStatus.ACCEPTED)
                .applicationText("본인 확인용 신청서")
                .build();
        MemberGroup savedApplication = entityManager.persistAndFlush(application);
        entityManager.clear();

        // when
        MemberGroup foundApplication = applicationRepository.findById(savedApplication.getId()).orElse(null);

        // then
        assertThat(foundApplication).isNotNull();
        assertThat(foundApplication.getMember().getId()).isEqualTo(applicant.getId());
    }

    @Test
    @DisplayName("다른 사용자의 신청서는 본인이 아니다")
    void validateApplicantOwnership_NotOwner() {
        // given
        Member anotherMember = Member.builder()
                .socialId("another1")
                .nickname("다른사용자")
                .email("another@test.com")
                .age(30)
                .gender(Gender.MALE)
                .profileImageUrl("https://example.com/another.jpg")
                .build();
        entityManager.persistAndFlush(anotherMember);

        MemberGroup application = MemberGroup.builder()
                .member(applicant)
                .group(group)
                .role(Role.MEMBER)
                .status(ApplicationStatus.ACCEPTED)
                .applicationText("다른 사용자 확인용 신청서")
                .build();
        MemberGroup savedApplication = entityManager.persistAndFlush(application);
        entityManager.clear();

        // when
        MemberGroup foundApplication = applicationRepository.findById(savedApplication.getId()).orElse(null);

        // then
        assertThat(foundApplication).isNotNull();
        assertThat(foundApplication.getMember().getId()).isNotEqualTo(anotherMember.getId());
        assertThat(foundApplication.getMember().getId()).isEqualTo(applicant.getId());
    }
} 
