package site.festifriends.domain.application.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Date;
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
import site.festifriends.entity.Festival;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberParty;
import site.festifriends.entity.Party;
import site.festifriends.entity.enums.AgeRange;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.FestivalState;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;
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
    private Party party;
    private Festival festival;

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

        festival = Festival.builder()
                .title("테스트 페스티벌")
                .posterUrl("https://example.com/poster.jpg")
                .startDate(new Date())
                .endDate(new Date())
                .location("서울시 강남구")
                .price(50000)
                .state(FestivalState.UPCOMING)
                .visit(true)
                .build();
        entityManager.persistAndFlush(festival);

        party = Party.builder()
                .title("테스트 모임")
                .genderType(Gender.ALL)
                .ageRange(AgeRange.TWENTIES)
                .gatherType(GroupCategory.COMPANION)
                .gatherDate(LocalDateTime.now().plusDays(7))
                .location("서울시 강남구")
                .count(4)
                .introduction("함께 페스티벌을 즐겨요!")
                .festival(festival)
                .build();
        entityManager.persistAndFlush(party);

        // 방장 정보 저장
        MemberParty hostMemberParty = MemberParty.builder()
                .member(host)
                .party(party)
                .role(Role.HOST)
                .status(ApplicationStatus.ACCEPTED)
                .build();
        entityManager.persistAndFlush(hostMemberParty);

        // 신청자 정보 저장
        MemberParty application = MemberParty.builder()
                .member(applicant)
                .party(party)
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
        Slice<MemberParty> result = applicationRepository
                .findAppliedApplicationsWithSlice(applicant.getId(), null, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.hasNext()).isFalse();

        MemberParty memberParty = result.getContent().get(0);
        assertThat(memberParty.getMember().getId()).isEqualTo(applicant.getId());
        assertThat(memberParty.getRole()).isEqualTo(Role.MEMBER);
        assertThat(memberParty.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(memberParty.getApplicationText()).isEqualTo("참여하고 싶습니다!");
    }

    @Test
    @DisplayName("파티별 방장 정보를 조회한다")
    void findHostsByPartyIds_Success() {
        // given
        List<Long> partyIds = List.of(party.getId());

        // when
        Map<Long, MemberParty> hosts = applicationRepository.findHostsByPartyIds(partyIds);

        // then
        assertThat(hosts).hasSize(1);
        assertThat(hosts.get(party.getId())).isNotNull();

        MemberParty hostInfo = hosts.get(party.getId());
        assertThat(hostInfo.getMember().getId()).isEqualTo(host.getId());
        assertThat(hostInfo.getRole()).isEqualTo(Role.HOST);
        assertThat(hostInfo.getMember().getNickname()).isEqualTo("방장");
    }

    @Test
    @DisplayName("방장 권한을 확인한다")
    void existsByPartyIdAndMemberIdAndRole_Host_Success() {
        // when
        boolean isHost = applicationRepository
                .existsByPartyIdAndMemberIdAndRole(party.getId(), host.getId(), Role.HOST);

        // then
        assertThat(isHost).isTrue();
    }

    @Test
    @DisplayName("일반 멤버는 방장 권한이 없다")
    void existsByPartyIdAndMemberIdAndRole_Member_False() {
        // when
        boolean isHost = applicationRepository
                .existsByPartyIdAndMemberIdAndRole(party.getId(), applicant.getId(), Role.HOST);

        // then
        assertThat(isHost).isFalse();
    }

    @Test
    @DisplayName("신청서 ID로 신청서를 조회한다")
    void findById_Success() {
        // given
        MemberParty application = MemberParty.builder()
                .member(applicant)
                .party(party)
                .role(Role.MEMBER)
                .status(ApplicationStatus.ACCEPTED)
                .applicationText("확정 테스트용 신청서")
                .build();
        MemberParty savedApplication = entityManager.persistAndFlush(application);
        entityManager.clear();

        // when
        MemberParty foundApplication = applicationRepository.findById(savedApplication.getId()).orElse(null);

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
        MemberParty application = MemberParty.builder()
                .member(applicant)
                .party(party)
                .role(Role.MEMBER)
                .status(ApplicationStatus.ACCEPTED)
                .applicationText("확정할 신청서")
                .build();
        MemberParty savedApplication = entityManager.persistAndFlush(application);
        entityManager.clear();

        // when
        MemberParty foundApplication = applicationRepository.findById(savedApplication.getId()).orElse(null);
        assertThat(foundApplication).isNotNull();
        
        foundApplication.confirm();
        entityManager.persistAndFlush(foundApplication);
        entityManager.clear();

        // then
        MemberParty confirmedApplication = applicationRepository.findById(savedApplication.getId()).orElse(null);
        assertThat(confirmedApplication).isNotNull();
        assertThat(confirmedApplication.getStatus()).isEqualTo(ApplicationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("신청자 본인의 신청서인지 확인한다")
    void validateApplicantOwnership_Success() {
        // given
        MemberParty application = MemberParty.builder()
                .member(applicant)
                .party(party)
                .role(Role.MEMBER)
                .status(ApplicationStatus.ACCEPTED)
                .applicationText("본인 확인용 신청서")
                .build();
        MemberParty savedApplication = entityManager.persistAndFlush(application);
        entityManager.clear();

        // when
        MemberParty foundApplication = applicationRepository.findById(savedApplication.getId()).orElse(null);

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

        MemberParty application = MemberParty.builder()
                .member(applicant)
                .party(party)
                .role(Role.MEMBER)
                .status(ApplicationStatus.ACCEPTED)
                .applicationText("다른 사용자 확인용 신청서")
                .build();
        MemberParty savedApplication = entityManager.persistAndFlush(application);
        entityManager.clear();

        // when
        MemberParty foundApplication = applicationRepository.findById(savedApplication.getId()).orElse(null);

        // then
        assertThat(foundApplication).isNotNull();
        assertThat(foundApplication.getMember().getId()).isNotEqualTo(anotherMember.getId());
        assertThat(foundApplication.getMember().getId()).isEqualTo(applicant.getId());
    }
} 
