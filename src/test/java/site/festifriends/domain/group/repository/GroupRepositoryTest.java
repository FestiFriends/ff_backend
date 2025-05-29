package site.festifriends.domain.group.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import site.festifriends.common.config.AuditConfig;
import site.festifriends.common.config.QueryDslConfig;
import site.festifriends.entity.Group;
import site.festifriends.entity.Performance;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;
import site.festifriends.entity.enums.PerformanceState;
import site.festifriends.domain.performance.repository.PerformanceRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, AuditConfig.class})
@ActiveProfiles("test")
class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    private Performance performance1;
    private Performance performance2;
    private List<Group> performance1Groups;
    private List<Group> performance2Groups;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        performance1 = createPerformance("공연 1");
        performance2 = createPerformance("공연 2");
        
        performanceRepository.save(performance1);
        performanceRepository.save(performance2);
        
        // 공연 1에 속한 그룹 생성
        performance1Groups = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Group group = createGroup("공연 1의 모임 " + (i + 1), performance1);
            performance1Groups.add(groupRepository.save(group));
        }
        
        // 공연 2에 속한 그룹 생성
        performance2Groups = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Group group = createGroup("공연 2의 모임 " + (i + 1), performance2);
            performance2Groups.add(groupRepository.save(group));
        }
    }

    @Test
    @DisplayName("공연 ID로 모임 목록 조회")
    void findByPerformanceId() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        
        // when
        Page<Group> result = groupRepository.findByPerformanceId(performance1.getId(), pageable);
        
        // then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getContent()).extracting("title")
                .containsExactlyInAnyOrder(
                        "공연 1의 모임 1", 
                        "공연 1의 모임 2", 
                        "공연 1의 모임 3", 
                        "공연 1의 모임 4", 
                        "공연 1의 모임 5"
                );
    }

    @Test
    @DisplayName("공연 ID로 모임 개수 조회")
    void countByPerformanceId() {
        // when
        Long count1 = groupRepository.countByPerformanceId(performance1.getId());
        Long count2 = groupRepository.countByPerformanceId(performance2.getId());
        
        // then
        assertThat(count1).isEqualTo(5);
        assertThat(count2).isEqualTo(3);
    }

    @Test
    @DisplayName("페이징 처리 테스트")
    void findByPerformanceIdWithPaging() {
        // given
        Pageable firstPageable = PageRequest.of(0, 2);
        Pageable secondPageable = PageRequest.of(1, 2);
        
        // when
        Page<Group> firstPage = groupRepository.findByPerformanceId(performance1.getId(), firstPageable);
        Page<Group> secondPage = groupRepository.findByPerformanceId(performance1.getId(), secondPageable);
        
        // then
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(secondPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalElements()).isEqualTo(5);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.isLast()).isFalse();
        assertThat(secondPage.isFirst()).isFalse();
        assertThat(secondPage.isLast()).isFalse();
    }

    @Test
    @DisplayName("삭제된 모임은 조회되지 않아야 함")
    void findByPerformanceIdExcludeDeleted() {
        // given
        Group groupToDelete = performance1Groups.get(0);
        groupToDelete.delete(); // 소프트 삭제
        groupRepository.save(groupToDelete);
        
        Pageable pageable = PageRequest.of(0, 10);
        
        // when
        Page<Group> result = groupRepository.findByPerformanceId(performance1.getId(), pageable);
        
        // then
        assertThat(result.getContent()).hasSize(4); // 5개 중 1개 삭제됨
        assertThat(result.getContent()).extracting("title")
                .doesNotContain(groupToDelete.getTitle());
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