package site.festifriends.domain.notification.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.festifriends.domain.notifications.dto.NotificationEvent;
import site.festifriends.domain.notifications.repository.NotificationRepository;
import site.festifriends.domain.notifications.service.NotificationService;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.Performance;
import site.festifriends.entity.Post;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;
import site.festifriends.entity.enums.NotificationType;
import site.festifriends.entity.enums.PerformanceState;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Member member;
    private Performance performance;
    private Group group;
    private Post post;

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .id(1L)
            .email("test1@example.com")
            .nickname("테스트1")
            .age(28)
            .gender(Gender.MALE)
            .introduction("테스트 1번 멤버")
            .tags(List.of("음악", "여행"))
            .sns(List.of("github", "instagram"))
            .socialId("socialId1")
            .build();

        performance = Performance.builder()
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
            .build();

        group = Group.builder()
            .title("테스트")
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

        post = Post.builder()
            .group(group)
            .author(member)
            .content("테스트 게시글 내용")
            .build();
    }

    @Test
    @DisplayName("[성공] 알림 생성 - 알림 타입이 APPLICATION인 경우")
    void createNotification_applicationType() {
        // Given
        NotificationType type = NotificationType.APPLICATION;

        // When
        NotificationEvent event = notificationService.createNotification(member, type, group.getTitle(), null, null);

        // Then
        assertThat(event).isNotNull();
        assertThat(event.getMessage()).isEqualTo("테스트" + type.getDescription());
    }

    @Test
    @DisplayName("[성공] 알림 생성 - 알림 타입이 APPLIED 경우")
    void createNotification_appliedType() {
        // Given
        NotificationType type = NotificationType.APPLIED;

        // When
        NotificationEvent event = notificationService.createNotification(member, type, group.getTitle(), null, null);

        // Then
        assertThat(event).isNotNull();
        assertThat(event.getMessage()).isEqualTo("테스트" + type.getDescription());
    }

    @Test
    @DisplayName("[성공] 알림 생성 - 알림 타입이 REJECTED인 경우")
    void createNotification_rejectedType() {
        // Given
        NotificationType type = NotificationType.REJECTED;

        // When
        NotificationEvent event = notificationService.createNotification(member, type, group.getTitle(), null, null);

        // Then
        assertThat(event).isNotNull();
        assertThat(event.getMessage()).isEqualTo("테스트" + type.getDescription());
    }

    @Test
    @DisplayName("[성공] 알림 생성 - 알림 타입이 BANNED인 경우")
    void createNotification_bannedType() {
        // Given
        NotificationType type = NotificationType.BANNED;

        // When
        NotificationEvent event = notificationService.createNotification(member, type, group.getTitle(), null, null);

        // Then
        assertThat(event).isNotNull();
        assertThat(event.getMessage()).isEqualTo("테스트" + type.getDescription());
    }

    @Test
    @DisplayName("[성공] 알림 생성 - 알림 타입이 GROUP인 경우")
    void createNotification_groupType() {
        // Given
        NotificationType type = NotificationType.GROUP;

        // When
        NotificationEvent event = notificationService.createNotification(member, type, group.getTitle(), group.getId(),
            null);

        // Then
        assertThat(event).isNotNull();
        assertThat(event.getMessage()).isEqualTo("테스트" + type.getDescription());
    }

    @Test
    @DisplayName("[성공] 알림 생성 - 알림 타입이 MY_PROFILE인 경우")
    void createNotification_myProfileType() {
        // Given
        NotificationType type = NotificationType.MY_PROFILE;

        // When
        NotificationEvent event = notificationService.createNotification(member, type, member.getNickname(), null,
            null);

        // Then
        assertThat(event).isNotNull();
        assertThat(event.getMessage()).isEqualTo(member.getNickname() + type.getDescription());
    }

    @Test
    @DisplayName("[성공] 알림 생성 - 알림 타입이 POST인 경우")
    void createNotification_postType() {
        // Given
        NotificationType type = NotificationType.POST;

        // When
        NotificationEvent event = notificationService.createNotification(member, type, "게시글 제목", post.getId(),
            group.getId());

        // Then
        assertThat(event).isNotNull();
        assertThat(event.getMessage()).isEqualTo("게시글 제목" + type.getDescription());
    }
}
