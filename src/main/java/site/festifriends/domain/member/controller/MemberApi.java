package site.festifriends.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Operation(
        summary = "내가 찜한 사용자 목록 조회",
        description = "내가 찜한 사용자 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "찜한 사용자 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> getMyLikedMembers(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(description = "커서 Id, default는 첫번째 요소")
        @RequestParam(required = false) Long cursorId,
        @Parameter(description = "한 페이지당 항목 수, default는 20")
        @RequestParam(defaultValue = "20") int size
    );

    @Operation(
        summary = "내가 찜한 사용자 수",
        description = "내가 찜한 사용자 수를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "찜한 사용자 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> getMyLikedMembersCount(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
        summary = "찜한 공연 목록 조회",
        description = "내가 찜한 공연 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "찜한 사용자 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> getMyLikedPerformances(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(description = "커서 Id, default는 첫번째 요소")
        @RequestParam(required = false) Long cursorId,
        @Parameter(description = "한 페이지당 항목 수, default는 20")
        @RequestParam(defaultValue = "20") int size
    );
}
