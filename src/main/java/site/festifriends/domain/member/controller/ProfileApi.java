package site.festifriends.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.member.dto.GetProfileResponse;

public interface ProfileApi {

    @Operation(
        summary = "프로필 홈",
        description = "유저 프로필 홈 조회 데이터를 반환합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "프로필 홈 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<ResponseWrapper<GetProfileResponse>> getProfile(
        @PathVariable Long userId
    );
}
