package site.festifriends.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.application.dto.ApplicationStatusRequest;
import site.festifriends.domain.application.dto.ApplicationStatusResponse;
import site.festifriends.domain.application.repository.ApplicationRepository;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberGroup;
import site.festifriends.entity.Performance;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.Role;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private Member host;
    private Member applicant;
    private Group group;
    private MemberGroup application;

    @BeforeEach
    void setUp() {
        host = Member.builder()
                .socialId("host1")
                .nickname("방장")
                .email("host@test.com")
                .age(25)
                .gender(Gender.MALE)
                .build();

        applicant = Member.builder()
                .socialId("applicant1")
                .nickname("신청자")
                .email("applicant@test.com")
                .age(23)
                .gender(Gender.FEMALE)
                .build();

        Performance performance = Performance.builder()
                .title("테스트 페스티벌")
                .build();

        group = mock(Group.class);
        lenient().when(group.getId()).thenReturn(1L);

        application = MemberGroup.builder()
                .member(applicant)
                .group(group)
                .role(Role.MEMBER)
                .status(ApplicationStatus.PENDING)
                .applicationText("참여하고 싶습니다!")
                .build();
    }

    @Test
    @DisplayName("방장이 신청서를 수락한다")
    void updateApplicationStatus_Accept_Success() {
        // given
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setStatus("accept");

        given(applicationRepository.findById(1L)).willReturn(Optional.of(application));
        given(applicationRepository.existsByGroupIdAndMemberIdAndRole(1L, 1L, Role.HOST))
                .willReturn(true);

        // when
        ResponseWrapper<ApplicationStatusResponse> response = 
                applicationService.updateApplicationStatus(1L, 1L, request);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("모임 가입 신청을 수락하였습니다");
        assertThat(response.getData().getResult()).isTrue();
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
    }

    @Test
    @DisplayName("방장이 신청서를 거절한다")
    void updateApplicationStatus_Reject_Success() {
        // given
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setStatus("reject");

        given(applicationRepository.findById(1L)).willReturn(Optional.of(application));
        given(applicationRepository.existsByGroupIdAndMemberIdAndRole(1L, 1L, Role.HOST))
                .willReturn(true);

        // when
        ResponseWrapper<ApplicationStatusResponse> response = 
                applicationService.updateApplicationStatus(1L, 1L, request);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("모임 가입 신청을 거절하였습니다");
        assertThat(response.getData().getResult()).isTrue();
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    @DisplayName("존재하지 않는 신청서에 대해 예외가 발생한다")
    void updateApplicationStatus_ApplicationNotFound() {
        // given
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setStatus("accept");

        given(applicationRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> applicationService.updateApplicationStatus(1L, 999L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("방장이 아닌 사용자가 신청서를 처리하려고 하면 예외가 발생한다")
    void updateApplicationStatus_NotHost() {
        // given
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setStatus("accept");

        given(applicationRepository.findById(1L)).willReturn(Optional.of(application));
        given(applicationRepository.existsByGroupIdAndMemberIdAndRole(1L, 2L, Role.HOST))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> applicationService.updateApplicationStatus(2L, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("PENDING 상태가 아닌 신청서를 처리하려고 하면 예외가 발생한다")
    void updateApplicationStatus_NotPendingStatus() {
        // given
        application = MemberGroup.builder()
                .member(applicant)
                .group(group)
                .role(Role.MEMBER)
                .status(ApplicationStatus.ACCEPTED) // 이미 승인된 상태
                .applicationText("참여하고 싶습니다!")
                .build();

        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setStatus("accept");

        given(applicationRepository.findById(1L)).willReturn(Optional.of(application));
        given(applicationRepository.existsByGroupIdAndMemberIdAndRole(1L, 1L, Role.HOST))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> applicationService.updateApplicationStatus(1L, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.BAD_REQUEST);
    }

    @Test
    @DisplayName("잘못된 상태 값으로 요청하면 예외가 발생한다")
    void updateApplicationStatus_InvalidStatus() {
        // given
        ApplicationStatusRequest request = new ApplicationStatusRequest();
        request.setStatus("invalid");

        given(applicationRepository.findById(1L)).willReturn(Optional.of(application));
        given(applicationRepository.existsByGroupIdAndMemberIdAndRole(1L, 1L, Role.HOST))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> applicationService.updateApplicationStatus(1L, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.BAD_REQUEST);
    }
} 
