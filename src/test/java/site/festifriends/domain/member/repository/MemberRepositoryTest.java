package site.festifriends.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import site.festifriends.common.config.AuditConfig;
import site.festifriends.common.config.QueryDslConfig;
import site.festifriends.domain.member.dto.LikedMemberDto;
import site.festifriends.domain.member.dto.LikedPerformanceDto;
import site.festifriends.domain.performance.repository.PerformanceImageRepository;
import site.festifriends.domain.performance.repository.PerformanceRepository;
import site.festifriends.entity.Bookmark;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberImage;
import site.festifriends.entity.Performance;
import site.festifriends.entity.enums.BookmarkType;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.PerformanceState;

@DataJpaTest
@Import({QueryDslConfig.class, AuditConfig.class})
@ActiveProfiles("test")
public class MemberRepositoryTest {

    @Autowired
    private MemberRepositoryImpl memberRepositoryImpl;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PerformanceImageRepository performanceImageRepository;

    private Member member;
    private Member target1;
    private Member target2;
    private MemberImage memberImage;
    private MemberImage targetImage1;
    private MemberImage targetImage2;
    private Performance targetPerformance1;
    private Performance targetPerformance2;
    @Autowired
    private MemberImageRepository memberImageRepository;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
            .email("test1@example.com")
            .nickname("테스트1")
            .age(28)
            .gender(Gender.MALE)
            .introduction("테스트 1번 멤버")
            .tags(List.of("음악", "여행"))
            .sns(List.of("github", "instagram"))
            .socialId("socialId1")
            .build());

        target1 = memberRepository.save(Member.builder()
            .email("test2@example.com")
            .nickname("테스트2")
            .age(30)
            .gender(Gender.MALE)
            .introduction("테스트 2번 멤버")
            .tags(List.of("피아노", "기타"))
            .sns(List.of("github", "instagram"))
            .socialId("socialId2")
            .build());

        target2 = memberRepository.save(Member.builder()
            .email("test3@example.com")
            .nickname("테스트3")
            .age(31)
            .gender(Gender.FEMALE)
            .introduction("테스트 3번 멤버")
            .tags(List.of("게임", "공부"))
            .sns(List.of("github", "instagram"))
            .socialId("socialId3")
            .build());

        memberImage = memberImageRepository.save(MemberImage.builder()
            .member(member)
            .src("https://example.com/profiles/test1.jpg")
            .alt("테스트1 프로필 이미지")
            .build());

        targetImage1 = memberImageRepository.save(MemberImage.builder()
            .member(target1)
            .src("https://example.com/profiles/test2.jpg")
            .alt("테스트2 프로필 이미지")
            .build());

        targetImage2 = memberImageRepository.save(MemberImage.builder()
            .member(target2)
            .src("https://example.com/profiles/test3.jpg")
            .alt("테스트3 프로필 이미지")
            .build());

        targetPerformance1 = performanceRepository.save(Performance.builder()
            .title("테스트 공연 1")
            .startDate(LocalDateTime.of(2025, 5, 30, 18, 0))
            .endDate(LocalDateTime.of(2025, 6, 1, 22, 0))
            .location("올림픽공원")
            .cast(List.of("배우1", "배우2"))
            .crew(List.of("감독1", "작가1"))
            .runtime("180분")
            .age("만 12세 이상")
            .productionCompany(List.of("제작사1"))
            .agency(List.of("기획사1"))
            .host(List.of("주최1"))
            .organizer(List.of("주관사1"))
            .price(List.of("VIP 10만원", "R석 8만원", "S석 5만원"))
            .poster("https://example.com/poster1.jpg")
            .state(PerformanceState.UPCOMING)
            .visit("국내")
            .time(List.of(
                "화요일 ~ 금요일(20:00)",
                "토요일(16:00,19:00)",
                "일요일(15:00,18:00)"
            ))
            .build());

        targetPerformance2 = performanceRepository.save(Performance.builder()
            .title("테스트 공연 2")
            .startDate(LocalDateTime.of(2025, 6, 5, 18, 0))
            .endDate(LocalDateTime.of(2025, 6, 7, 22, 0))
            .location("세종문화회관")
            .cast(List.of("배우3", "배우4"))
            .crew(List.of("감독2", "작가2"))
            .runtime("150분")
            .age("만 15세 이상")
            .productionCompany(List.of("제작사2"))
            .agency(List.of("기획사2"))
            .host(List.of("주최2"))
            .organizer(List.of("주관사2"))
            .price(List.of("VIP 12만원", "R석 10만원", "S석 7만원"))
            .poster("https://example.com/poster2.jpg")
            .state(PerformanceState.UPCOMING)
            .visit("국내")
            .time(List.of(
                "화요일 ~ 금요일(19:00)",
                "토요일(15:00,18:00)",
                "일요일(14:00,17:00)"
            ))
            .build());

        bookmarkRepository.save(
            Bookmark.builder()
                .member(member)
                .type(BookmarkType.MEMBER)
                .targetId(target1.getId())
                .build()
        );

        bookmarkRepository.save(
            Bookmark.builder()
                .member(member)
                .type(BookmarkType.MEMBER)
                .targetId(target2.getId())
                .build()
        );

        bookmarkRepository.save(
            Bookmark.builder()
                .member(member)
                .type(BookmarkType.PERFORMANCE)
                .targetId(targetPerformance1.getId())
                .build()
        );

        bookmarkRepository.save(
            Bookmark.builder()
                .member(member)
                .type(BookmarkType.PERFORMANCE)
                .targetId(targetPerformance2.getId())
                .build()
        );
    }

    @Test
    @DisplayName("[성공] 내가 찜한 사용자 목록 조회")
    void getMyLikedMembers_test() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<LikedMemberDto> result = memberRepositoryImpl.getMyLikedMembers(member.getId(), null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.hasNext()).isFalse();
        List<LikedMemberDto> content = result.getContent();
        assertThat(content).isNotEmpty();

        LikedMemberDto dto1 = content.get(0);
        assertThat(dto1.getName()).isEqualTo("테스트3");
        assertThat(dto1.getGender()).isEqualTo("FEMALE");
        assertThat(dto1.getAge()).isEqualTo(31);
        assertThat(dto1.getProfileImage().getSrc()).isEqualTo("https://example.com/profiles/test3.jpg");

        LikedMemberDto dto2 = content.get(1);
        assertThat(dto2.getName()).isEqualTo("테스트2");
        assertThat(dto2.getGender()).isEqualTo("MALE");
        assertThat(dto2.getAge()).isEqualTo(30);
        assertThat(dto2.getProfileImage().getSrc()).isEqualTo("https://example.com/profiles/test2.jpg");
    }

    @Test
    @DisplayName("[성공] 내가 찜한 사용자 수 조회")
    void getMyLikedMembersCount_test() {
        // when
        Long count = memberRepositoryImpl.countMyLikedMembers(member.getId());

        // then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("[성공] 내가 찜한 공연 목록 조회")
    void getMyLikedPerformances_test() {
        // given

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<LikedPerformanceDto> result = memberRepositoryImpl.getMyLikedPerformances(member.getId(), null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.hasNext()).isFalse();
        List<LikedPerformanceDto> content = result.getContent();
        assertThat(content).isNotEmpty();
        LikedPerformanceDto dto1 = content.get(0);
        assertThat(dto1.getTitle()).isEqualTo("테스트 공연 2");
        assertThat(dto1.getStartDate()).isEqualTo(LocalDateTime.of(2025, 6, 5, 18, 0));
        assertThat(dto1.getEndDate()).isEqualTo(LocalDateTime.of(2025, 6, 7, 22, 0));
        assertThat(dto1.getPoster()).isEqualTo("https://example.com/poster2.jpg");
        LikedPerformanceDto dto2 = content.get(1);
        assertThat(dto2.getTitle()).isEqualTo("테스트 공연 1");
        assertThat(dto2.getStartDate()).isEqualTo(LocalDateTime.of(2025, 5, 30, 18, 0));
        assertThat(dto2.getEndDate()).isEqualTo(LocalDateTime.of(2025, 6, 1, 22, 0));
        assertThat(dto2.getPoster()).isEqualTo("https://example.com/poster1.jpg");
    }
}
