package site.festifriends.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
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
import site.festifriends.entity.Performance;
import site.festifriends.entity.PerformanceImage;
import site.festifriends.entity.enums.BookmarkType;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.PerformanceState;

@DataJpaTest
@Import({QueryDslConfig.class, AuditConfig.class})
@ActiveProfiles("test")
public class MemberRepositoryTest {

    @Autowired
    private MemberRepositoryImpl memberRepositoryImpl; // 혹은 MemberRepositoryCustom

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PerformanceImageRepository performanceImageRepository;

    @Test
    @DisplayName("[성공] 내가 찜한 사용자 목록 조회")
    void getMyLikedMembers_test() {
        // given
        Member member = memberRepository.save(Member.builder()
            .email("test1@example.com")
            .nickname("테스트")
            .profileImageUrl("https://example.com/profiles/test1.jpg")
            .age(28)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId1")
            .build());

        Member target1 = memberRepository.save(Member.builder()
            .email("test2@example.com")
            .nickname("테스트2")
            .profileImageUrl("https://example.com/profiles/test2.jpg")
            .age(20)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId2")
            .build());

        Member target2 = memberRepository.save(Member.builder()
            .email("test3@example.com")
            .nickname("테스트3")
            .profileImageUrl("https://example.com/profiles/test3.jpg")
            .age(21)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId3")
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
        assertThat(dto1.getGender()).isEqualTo("MALE");
        assertThat(dto1.getAge()).isEqualTo(21);
        assertThat(dto1.getProfileImage()).isEqualTo("https://example.com/profiles/test3.jpg");

        LikedMemberDto dto2 = content.get(1);
        assertThat(dto2.getName()).isEqualTo("테스트2");
        assertThat(dto2.getGender()).isEqualTo("MALE");
        assertThat(dto2.getAge()).isEqualTo(20);
        assertThat(dto2.getProfileImage()).isEqualTo("https://example.com/profiles/test2.jpg");
    }

    @Test
    @DisplayName("[성공] 내가 찜한 사용자 수 조회")
    void getMyLikedMembersCount_test() {
        // given
        Member member = memberRepository.save(Member.builder()
            .email("test1@example.com")
            .nickname("테스트")
            .profileImageUrl("https://example.com/profiles/test1.jpg")
            .age(28)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId1")
            .build());

        Member target1 = memberRepository.save(Member.builder()
            .email("test2@example.com")
            .nickname("테스트2")
            .profileImageUrl("https://example.com/profiles/test2.jpg")
            .age(20)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId2")
            .build());

        Member target2 = memberRepository.save(Member.builder()
            .email("test3@example.com")
            .nickname("테스트3")
            .profileImageUrl("https://example.com/profiles/test3.jpg")
            .age(21)
            .gender(Gender.MALE)
            .introduction("안녕하세요.")
            .tags(List.of("음악", "여행"))
            .socialId("socialId3")
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

        // when
        Long count = memberRepositoryImpl.countMyLikedMembers(member.getId());

        // then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("[성공] 내가 찜한 공연 목록 조회")
    void getMyLikedPerformances_test() {
        // given
        Member member = memberRepository.save(Member.builder()
            .email("test1@example.com")
            .nickname("테스터")
            .profileImageUrl("https://example.com/profile.jpg")
            .age(28)
            .gender(Gender.MALE)
            .introduction("소개")
            .socialId("social1")
            .build());

        Performance performance = performanceRepository.save(Performance.builder()
            .title("테스트 공연")
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
            .poster("https://example.com/poster.jpg")
            .state(PerformanceState.UPCOMING)
            .visit("국내")
            .time(List.of(
                LocalDateTime.of(2025, 5, 30, 18, 0),
                LocalDateTime.of(2025, 5, 31, 19, 0),
                LocalDateTime.of(2025, 6, 1, 20, 0)
            ))
            .build());

        PerformanceImage pr1 = new PerformanceImage(performance, "https://example.com/img1.jpg", "이미지1");
        PerformanceImage pr2 = new PerformanceImage(performance, "https://example.com/img2.jpg", "이미지2");
        performanceImageRepository.save(pr1);
        performanceImageRepository.save(pr2);

        bookmarkRepository.save(
            Bookmark.builder()
                .member(member)
                .type(BookmarkType.PERFORMANCE)
                .targetId(performance.getId())
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<LikedPerformanceDto> result = memberRepositoryImpl.getMyLikedPerformances(member.getId(), null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.hasNext()).isFalse();
        List<LikedPerformanceDto> content = result.getContent();
        assertThat(content).isNotEmpty();
        LikedPerformanceDto dto = content.get(0);
        assertThat(dto.getId()).isEqualTo(performance.getId());
        assertThat(dto.getTitle()).isEqualTo("테스트 공연");
        assertThat(dto.getStartDate()).isEqualTo(LocalDateTime.of(2025, 5, 30, 18, 0));
        assertThat(dto.getEndDate()).isEqualTo(LocalDateTime.of(2025, 6, 1, 22, 0));
        assertThat(dto.getLocation()).isEqualTo("올림픽공원");
        assertThat(dto.getCast()).containsExactly("배우1", "배우2");
        assertThat(dto.getCrew()).containsExactly("감독1", "작가1");
        assertThat(dto.getRuntime()).isEqualTo("180분");
        assertThat(dto.getAge()).isEqualTo("만 12세 이상");
        assertThat(dto.getProductionCompany()).containsExactly("제작사1");
        assertThat(dto.getAgency()).containsExactly("기획사1");
        assertThat(dto.getHost()).containsExactly("주최1");
        assertThat(dto.getOrganizer()).containsExactly("주관사1");
        assertThat(dto.getPrice()).containsExactly("VIP 10만원", "R석 8만원", "S석 5만원");
        assertThat(dto.getPoster()).isEqualTo("https://example.com/poster.jpg");
        assertThat(dto.getState()).isEqualTo("UPCOMING");
        assertThat(dto.getVisit()).isEqualTo("국내");
        assertThat(dto.getImages()).hasSize(2);
        assertThat(dto.getImages().get(0).getSrc()).isEqualTo("https://example.com/img1.jpg");
        assertThat(dto.getImages().get(1).getSrc()).isEqualTo("https://example.com/img2.jpg");
        assertThat(dto.getTime()).containsExactly(
            LocalDateTime.of(2025, 5, 30, 18, 0),
            LocalDateTime.of(2025, 5, 31, 19, 0),
            LocalDateTime.of(2025, 6, 1, 20, 0)
        );
        assertThat(dto.getBookmarkId()).isNotNull();

    }
}
