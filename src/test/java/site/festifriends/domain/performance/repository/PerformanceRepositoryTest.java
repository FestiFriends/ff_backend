package site.festifriends.domain.performance.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import site.festifriends.common.config.AuditConfig;
import site.festifriends.common.config.QueryDslConfig;
import site.festifriends.domain.performance.dto.PerformanceSearchRequest;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.Performance;
import site.festifriends.entity.PerformanceImage;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;
import site.festifriends.entity.enums.PerformanceState;

@DataJpaTest
@Import({QueryDslConfig.class, AuditConfig.class})
@ActiveProfiles("test")
class PerformanceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PerformanceRepository performanceRepository;

    private Performance performance1;
    private Performance performance2;
    private Performance performance3;
    private Group group1;
    private Group group2;
    private Group group3;

    @BeforeEach
    void setUp() {
        // 공연 생성
        performance1 = createPerformance("서울 재즈 페스티벌", "서울시 송파구", "국내", 
                LocalDateTime.of(2025, 5, 30, 18, 0),
                LocalDateTime.of(2025, 6, 1, 22, 0));
        
        performance2 = createPerformance("부산 락 페스티벌", "부산시 해운대구", "국내",
                LocalDateTime.of(2025, 7, 15, 19, 0),
                LocalDateTime.of(2025, 7, 17, 23, 0));
        
        performance3 = createPerformance("콜드플레이 내한공연", "서울시 강남구", "내한",
                LocalDateTime.of(2025, 8, 20, 20, 0),
                LocalDateTime.of(2025, 8, 22, 22, 30));

        // 삭제된 공연 (검색에서 제외되어야 함)
        Performance deletedPerformance = createPerformance("삭제된 공연", "서울시", "국내",
                LocalDateTime.of(2025, 9, 1, 18, 0),
                LocalDateTime.of(2025, 9, 3, 22, 0));
        deletedPerformance.delete();
        entityManager.merge(deletedPerformance);

        // 공연 이미지 추가
        createPerformanceImage(performance1, "https://example.com/jazz1.jpg", "재즈 페스티벌 메인 이미지");
        createPerformanceImage(performance1, "https://example.com/jazz2.jpg", "재즈 페스티벌 서브 이미지");
        
        // 모임 생성 (모임 개수 테스트용)
        Member host = createMember("host1", "테스트호스트", Gender.MALE, 25);
        group1 = createGroup("재즈 모임1", performance1, host);
        group2 = createGroup("재즈 모임2", performance1, host);
        group3 = createGroup("락 모임1", performance2, host);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("공연명으로 검색할 수 있다")
    void searchByTitle() {
        // given
        PerformanceSearchRequest request = new PerformanceSearchRequest();
        request.setTitle("재즈");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Performance> result = performanceRepository.searchPerformancesWithPaging(request, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("서울 재즈 페스티벌");
    }

    @Test
    @DisplayName("지역으로 검색할 수 있다")
    void searchByLocation() {
        // given
        PerformanceSearchRequest request = new PerformanceSearchRequest();
        request.setLocation("서울");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Performance> result = performanceRepository.searchPerformancesWithPaging(request, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Performance::getTitle)
                .containsExactlyInAnyOrder("서울 재즈 페스티벌", "콜드플레이 내한공연");
    }

    @Test
    @DisplayName("내한 여부로 검색할 수 있다")
    void searchByVisit() {
        // given
        PerformanceSearchRequest request = new PerformanceSearchRequest();
        request.setVisit("내한");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Performance> result = performanceRepository.searchPerformancesWithPaging(request, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("콜드플레이 내한공연");
    }

    @Test
    @DisplayName("날짜 범위로 검색할 수 있다")
    void searchByDateRange() {
        // given
        PerformanceSearchRequest request = new PerformanceSearchRequest();
        request.setStartDate(LocalDate.of(2025, 7, 1));
        request.setEndDate(LocalDate.of(2025, 8, 31));
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Performance> result = performanceRepository.searchPerformancesWithPaging(request, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Performance::getTitle)
                .containsExactlyInAnyOrder("부산 락 페스티벌", "콜드플레이 내한공연");
    }

    @Test
    @DisplayName("제목 가나다순으로 정렬할 수 있다")
    void sortByTitleAsc() {
        // given
        PerformanceSearchRequest request = new PerformanceSearchRequest();
        request.setSort("title_asc");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Performance> result = performanceRepository.searchPerformancesWithPaging(request, pageable);

        // then
        List<String> titles = result.getContent().stream()
                .map(Performance::getTitle)
                .toList();
        
        assertThat(titles).containsExactly("부산 락 페스티벌", "서울 재즈 페스티벌", "콜드플레이 내한공연");
    }

    @Test
    @DisplayName("제목 역순으로 정렬할 수 있다")
    void sortByTitleDesc() {
        // given
        PerformanceSearchRequest request = new PerformanceSearchRequest();
        request.setSort("title_desc");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Performance> result = performanceRepository.searchPerformancesWithPaging(request, pageable);

        // then
        List<String> titles = result.getContent().stream()
                .map(Performance::getTitle)
                .toList();
        
        assertThat(titles).containsExactly("콜드플레이 내한공연", "서울 재즈 페스티벌", "부산 락 페스티벌");
    }

    @Test
    @DisplayName("날짜 빠른순으로 정렬할 수 있다")
    void sortByDateAsc() {
        // given
        PerformanceSearchRequest request = new PerformanceSearchRequest();
        request.setSort("date_asc");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Performance> result = performanceRepository.searchPerformancesWithPaging(request, pageable);

        // then
        List<String> titles = result.getContent().stream()
                .map(Performance::getTitle)
                .toList();
        
        assertThat(titles).containsExactly("서울 재즈 페스티벌", "부산 락 페스티벌", "콜드플레이 내한공연");
    }

    @Test
    @DisplayName("페이징이 올바르게 작동한다")
    void pagingWorks() {
        // given
        PerformanceSearchRequest request = new PerformanceSearchRequest();
        Pageable pageable = PageRequest.of(0, 2); // 페이지 크기 2

        // when
        Page<Performance> result = performanceRepository.searchPerformancesWithPaging(request, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
    }

    @Test
    @DisplayName("삭제된 공연은 검색되지 않는다")
    void deletedPerformancesAreExcluded() {
        // given
        PerformanceSearchRequest request = new PerformanceSearchRequest();
        request.setTitle("삭제된");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Performance> result = performanceRepository.searchPerformancesWithPaging(request, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("공연 ID로 모임 개수를 조회할 수 있다")
    void findGroupCountsByPerformanceIds() {
        // given
        List<Long> performanceIds = List.of(performance1.getId(), performance2.getId(), performance3.getId());

        // when
        Map<Long, Long> result = performanceRepository.findGroupCountsByPerformanceIds(performanceIds);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(performance1.getId())).isEqualTo(2L);
        assertThat(result.get(performance2.getId())).isEqualTo(1L);
        assertThat(result.get(performance3.getId())).isNull(); // 모임이 없는 경우
    }

    @Test
    @DisplayName("복합 조건으로 검색할 수 있다")
    void searchWithMultipleConditions() {
        // given
        PerformanceSearchRequest request = new PerformanceSearchRequest();
        request.setLocation("서울");
        request.setVisit("국내");
        request.setStartDate(LocalDate.of(2025, 5, 1));
        request.setEndDate(LocalDate.of(2025, 6, 30));
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Performance> result = performanceRepository.searchPerformancesWithPaging(request, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("서울 재즈 페스티벌");
    }

    // ===== Private Helper Methods =====

    private Performance createPerformance(String title, String location, String visit, 
                                        LocalDateTime startDate, LocalDateTime endDate) {
        Performance performance = Performance.builder()
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .location(location)
                .cast(new ArrayList<>(List.of("아티스트1", "아티스트2")))
                .crew(new ArrayList<>(List.of("스태프1", "스태프2")))
                .runtime("180분")
                .age("만 12세 이상")
                .productionCompany(new ArrayList<>(List.of("제작사")))
                .agency(new ArrayList<>(List.of("기획사")))
                .host(new ArrayList<>(List.of("주최사")))
                .organizer(new ArrayList<>(List.of("주관사")))
                .price(new ArrayList<>(List.of("일반석 50,000원", "VIP석 100,000원")))
                .poster("https://example.com/poster.jpg")
                .state(PerformanceState.UPCOMING)
                .visit(visit)
                .time(new ArrayList<>(List.of(startDate.toString(), endDate.toString())))
                .build();
        return entityManager.persistAndFlush(performance);
    }

    private PerformanceImage createPerformanceImage(Performance performance, String src, String alt) {
        PerformanceImage image = PerformanceImage.builder()
                .performance(performance)
                .src(src)
                .alt(alt)
                .build();
        return entityManager.persistAndFlush(image);
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

    private Group createGroup(String title, Performance performance, Member host) {
        LocalDateTime gatherDate = LocalDateTime.now().plusDays(1);
        Group group = Group.builder()
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
        return entityManager.persistAndFlush(group);
    }
} 