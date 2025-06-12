package site.festifriends.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.member.dto.GetMyProfileResponse;
import site.festifriends.domain.member.dto.GetProfileResponse;
import site.festifriends.domain.member.dto.UpdateProfileRequest;
import site.festifriends.domain.member.service.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
public class ProfileController implements ProfileApi {

    private final ProfileService profileService;

    @Override
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseWrapper<GetProfileResponse>> getProfile(
        @PathVariable Long userId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long memberId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            memberId = userDetails.getMemberId();
        }

        return ResponseEntity.ok(ResponseWrapper.success(
            "프로필 조회가 성공적으로 처리되었습니다.",
            profileService.getMemberProfile(userId, memberId)
        ));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<GetMyProfileResponse>> getMyProfile(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        return ResponseEntity.ok(ResponseWrapper.success(
            "프로필 조회가 성공적으로 처리되었습니다.",
            profileService.getMyProfile(memberId)
        ));
    }

    @Override
    @PatchMapping("/me")
    public ResponseEntity<ResponseWrapper<?>> updateMyProfile(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        profileService.updateMyProfile(userDetails.getMemberId(), request);

        return ResponseEntity.ok(ResponseWrapper.success(
            "프로필이 성공적으로 수정되었습니다."
        ));
    }
}
