package site.festifriends.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import site.festifriends.domain.auth.UserDetailsImpl;

public interface MemberApi {

    @Operation(
        summary = "회원 탈퇴",
        description = "회원 탈퇴 API. Access Token을 받아 해당 회원을 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> deleteMember(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request);
}
