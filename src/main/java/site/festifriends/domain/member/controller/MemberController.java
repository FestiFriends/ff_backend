package site.festifriends.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.member.dto.LikedMemberResponse;
import site.festifriends.domain.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MemberController implements MemberApi {

    private final MemberService memberService;

    @Override
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal UserDetailsImpl userDetails,
        HttpServletRequest request) {
        memberService.deleteMember(userDetails.getMemberId(), request);

        return ResponseEntity.ok().body(ResponseWrapper.success("회원 탈퇴가 완료되었습니다."));
    }

    @Override
    @GetMapping("/favorites")
    public ResponseEntity<CursorResponseWrapper<LikedMemberResponse>> getMyLikedMembers(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(memberService.getMyLikedMembers(userDetails.getMemberId(), cursorId, size));
    }

    @Override
    @GetMapping("/favorites/count")
    public ResponseEntity<?> getMyLikedMembersCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long count = memberService.getMyLikedMembersCount(userDetails.getMemberId());
        return ResponseEntity.ok(ResponseWrapper.success("요청이 성공적으로 처리되었습니다.", count));
    }

    @Override
    @GetMapping("/performances/favorites")
    public ResponseEntity<?> getMyLikedPerformances(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(memberService.getMyLikedPerformances(userDetails.getMemberId(), cursorId, size));
    }
}
