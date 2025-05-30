package site.festifriends.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.member.dto.LikedMemberDto;
import site.festifriends.domain.member.dto.LikedMemberResponse;
import site.festifriends.domain.member.dto.LikedPerformanceDto;
import site.festifriends.domain.member.dto.LikedPerformanceImageDto;
import site.festifriends.domain.member.dto.LikedPerformanceResponse;
import site.festifriends.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("[성공] 내가 찜한 유저 목록 조회(nextCursor 존재)")
    void getMyLikedMembers_cursor_paging() {
        // given
        Long memberId = 1L;
        Long cursorId = null;
        int size = 2;

        LikedMemberDto member1 = new LikedMemberDto(
            "테스트1", "Male", 28, "1L", false, "https://example.com/1.jpg", List.of("#음악", "#여행"), 100L);
        LikedMemberDto member2 = new LikedMemberDto(
            "테스트2", "Female", 25, "2L", false, "https://example.com/2.jpg", List.of("#운동", "#독서"), 99L);
        LikedMemberDto member3 = new LikedMemberDto(
            "테스트3", "Male", 30, "3L", false, "https://example.com/3.jpg", List.of("#사진"), 98L);

        List<LikedMemberDto> memberList = Arrays.asList(member1, member2, member3);
        Slice<LikedMemberDto> slice = new SliceImpl<>(memberList, PageRequest.of(0, size + 1), true);

        when(memberRepository.getMyLikedMembers(eq(memberId), eq(cursorId), any(PageRequest.class)))
            .thenReturn(slice);

        // when
        CursorResponseWrapper<LikedMemberResponse> response = memberService.getMyLikedMembers(memberId, cursorId, size);

        // then
        assertThat(response.getData()).hasSize(size);
        assertThat(response.getData().get(0).getName()).isEqualTo("테스트1");
        assertThat(response.getHasNext()).isTrue();
        assertThat(response.getCursorId()).isEqualTo(98L);
    }

    @Test
    @DisplayName("[성공] 내가 찜한 유저 목록 조회(nextCursor 없을 때)")
    void getMyLikedMembers_noMoreData() {
        // given
        Long memberId = 1L;
        Long cursorId = null;
        int size = 2;

        LikedMemberDto member1 = new LikedMemberDto(
            "테스트1", "Male", 28, "1L", false, "https://example.com/1.jpg", List.of("#음악", "#여행"), 100L);
        LikedMemberDto member2 = new LikedMemberDto(
            "테스트2", "Female", 25, "2L", false, "https://example.com/2.jpg", List.of("#운동", "#독서"), 99L);

        List<LikedMemberDto> memberList = Arrays.asList(member1, member2);
        Slice<LikedMemberDto> slice = new SliceImpl<>(memberList, PageRequest.of(0, size + 1), false);

        when(memberRepository.getMyLikedMembers(eq(memberId), eq(cursorId), any(PageRequest.class)))
            .thenReturn(slice);

        // when
        CursorResponseWrapper<LikedMemberResponse> response = memberService.getMyLikedMembers(memberId, cursorId, size);

        // then
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getHasNext()).isFalse();
        assertThat(response.getCursorId()).isNull();
    }

    @Test
    @DisplayName("[성공] 내가 찜한 공연 목록 조회")
    void getMyLikedPerformances() {
        // given
        Long memberId = 1L;
        Long cursorId = null;
        int size = 1;

        LikedPerformanceDto performance1 = new LikedPerformanceDto(
            1L,
            "공연1",
            LocalDateTime.of(2023, 10, 1, 0, 0),
            LocalDateTime.of(2023, 10, 2, 0, 0),
            "장소1",
            List.of("배우1", "배우2"),
            List.of("스태프1"),
            "180분",
            "만 12세 이상",
            List.of("제작사1"),
            List.of("기획사1"),
            List.of("주최1"),
            List.of("주관1"),
            List.of("VIP 10만원"),
            "https://example.com/performance1.jpg",
            "UPCOMING",
            "국내",
            List.of(new LikedPerformanceImageDto("1", "https://example.com/img1.jpg", "이미지1")),
            List.of(LocalDateTime.of(2023, 10, 1, 18, 0)),
            100L // bookmarkId
        );

        LikedPerformanceDto performance2 = new LikedPerformanceDto(
            2L,
            "공연2",
            LocalDateTime.of(2023, 11, 1, 0, 0),
            LocalDateTime.of(2023, 11, 2, 0, 0),
            "장소2",
            List.of("배우3", "배우4"),
            List.of("스태프2"),
            "180분",
            "만 12세 이상",
            List.of("제작사2"),
            List.of("기획사2"),
            List.of("주최2"),
            List.of("주관2"),
            List.of("VIP 9만원"),
            "https://example.com/performance2.jpg",
            "UPCOMING",
            "국내",
            List.of(new LikedPerformanceImageDto("2", "https://example.com/img2.jpg", "이미지2")),
            List.of(LocalDateTime.of(2023, 11, 1, 18, 0)),
            101L // bookmarkId
        );

        List<LikedPerformanceDto> performanceList = Arrays.asList(performance1, performance2);
        Slice<LikedPerformanceDto> slice = new SliceImpl<>(performanceList, PageRequest.of(0, size + 1), true);

        when(memberRepository.getMyLikedPerformances(eq(memberId), eq(cursorId), any(PageRequest.class)))
            .thenReturn(slice);

        // when
        CursorResponseWrapper<LikedPerformanceResponse> response =
            memberService.getMyLikedPerformances(memberId, cursorId, size);

        // then
        assertThat(response.getData()).hasSize(size);
        assertThat(response.getData().get(0).getTitle()).isEqualTo("공연1");
        assertThat(response.getHasNext()).isTrue();
        assertThat(response.getCursorId()).isEqualTo(101L);
    }

}
