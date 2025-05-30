package site.festifriends.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
import site.festifriends.domain.member.dto.MemberDto;
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
        CursorResponseWrapper<MemberDto> response = memberService.getMyLikedMembers(memberId, cursorId, size);

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
        CursorResponseWrapper<MemberDto> response = memberService.getMyLikedMembers(memberId, cursorId, size);

        // then
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getHasNext()).isFalse();
        assertThat(response.getCursorId()).isNull();
    }
}
