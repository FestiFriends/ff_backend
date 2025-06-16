package site.festifriends.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.application.dto.JoinedGroupResponse;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.member.dto.GetMyProfileResponse;
import site.festifriends.domain.member.dto.GetProfileResponse;
import site.festifriends.domain.member.dto.UpdateProfileRequest;

public interface ProfileApi {

    @Operation(
        summary = "프로필 홈",
        description = "유저 프로필 홈 조회 데이터를 반환합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "프로필 홈 조회 성공"),
            @ApiResponse(responseCode = "400", description = "해당하는 회원이 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        }
    )
    ResponseEntity<ResponseWrapper<GetProfileResponse>> getProfile(
        @PathVariable Long userId
    );

    @Operation(
        summary = "마이페이지 - 프로필 카드",
        description = "회원 본인 프로필 데이터를 반환합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "프로필 홈 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        }
    )
    ResponseEntity<ResponseWrapper<GetMyProfileResponse>> getMyProfile(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(
        summary = "내 프로필 수정",
        description = "회원 본인의 프로필 데이터를 수정합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "프로필이 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 회원입니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<ResponseWrapper<?>> updateMyProfile(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody UpdateProfileRequest request
    );

    @Operation(
        summary = "유저의 참여 모임 조회",
        description = "특정 유저의 참여 모임을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "참여 그룹 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 회원입니다."),
        }
    )
    ResponseEntity<CursorResponseWrapper<JoinedGroupResponse>> getUserJoinedGroups(
        @PathVariable Long memberId,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    );
}
