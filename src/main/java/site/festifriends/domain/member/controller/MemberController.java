package site.festifriends.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.jwt.AccessTokenProvider;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.member.dto.CheckNicknameDuplicationResponse;
import site.festifriends.domain.member.dto.GetMyIdResponse;
import site.festifriends.domain.member.dto.LikedMemberCountResponse;
import site.festifriends.domain.member.dto.LikedMemberResponse;
import site.festifriends.domain.member.dto.ToggleUserLikeRequest;
import site.festifriends.domain.member.dto.ToggleUserLikeResponse;
import site.festifriends.domain.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MemberController implements MemberApi {

    private final MemberService memberService;
    private final AccessTokenProvider accessTokenProvider;

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
        return ResponseEntity.ok(ResponseWrapper.success(
            "요청이 성공적으로 처리되었습니다.",
            new LikedMemberCountResponse(count)
        ));
    }

    @Override
    @GetMapping("/performances/favorites")
    public ResponseEntity<?> getMyLikedPerformances(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
            memberService.getMyLikedPerformances(userDetails.getMemberId(), cursorId, size)
        );
    }

    @Override
    @PatchMapping("/favorites/{userId}")
    public ResponseEntity<?> toggleLikeMember(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long userId,
        @Valid @RequestBody ToggleUserLikeRequest request
    ) {
        boolean like = request.getIsLiked();
        ToggleUserLikeResponse response = memberService.toggleLikeMember(userDetails.getMemberId(), userId, like);

        if (like) {
            return ResponseEntity.ok(ResponseWrapper.success("사용자를 찜했습니다", response));
        } else {
            return ResponseEntity.ok(ResponseWrapper.success("사용자를 찜 취소했습니다", response));
        }
    }

    @Override
    @GetMapping("/id")
    public ResponseEntity<ResponseWrapper<GetMyIdResponse>> getMyId(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(ResponseWrapper.success(
            "회원 ID 조회 성공",
            GetMyIdResponse.builder()
                .userId(userDetails.getMemberId())
                .build()
        ));
    }

    @Override
    @GetMapping("/check-nickname")
    public ResponseEntity<ResponseWrapper<CheckNicknameDuplicationResponse>> checkNicknameDuplication(
        @RequestParam String nickname
    ) {
        boolean isAvailable = memberService.checkNicknameDuplication(nickname);

        if (isAvailable) {
            return ResponseEntity.ok(ResponseWrapper.success(
                "사용 가능한 닉네임입니다.",
                CheckNicknameDuplicationResponse.builder()
                    .isAvailable(true)
                    .build()
            ));
        } else {
            return ResponseEntity.ok(ResponseWrapper.success(
                "이미 사용 중인 닉네임입니다.",
                CheckNicknameDuplicationResponse.builder()
                    .isAvailable(false)
                    .build()
            ));
        }
    }
}
