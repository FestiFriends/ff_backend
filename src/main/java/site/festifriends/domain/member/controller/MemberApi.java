package site.festifriends.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.member.dto.ToggleUserLikeRequest;

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
            @ApiResponse(responseCode = "200", description = "요청이 성공적으로 처리되었습니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> getMyLikedMembersCount(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
        summary = "찜한 공연 목록 조회",
        description = "내가 찜한 공연 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "요청이 성공적으로 처리되었습니다."),
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

    @Operation(
        summary = "사용자 찜하기/취소",
        description = "사용자를 찜하거나 찜을 취소합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "사용자를 찜했습니다"),
            @ApiResponse(responseCode = "200", description = "사용자를 찜 취소했습니다"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 회원입니다."),
            @ApiResponse(responseCode = "400", description = "이미 찜한 사용자를 찜할 수 없습니다."),
            @ApiResponse(responseCode = "400", description = "찜하지 않은 사용자를 찜 취소할 수 없습니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> toggleLikeMember(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(description = "찜할 사용자 ID")
        @PathVariable Long memberId,
        @RequestBody ToggleUserLikeRequest request
    );
}
