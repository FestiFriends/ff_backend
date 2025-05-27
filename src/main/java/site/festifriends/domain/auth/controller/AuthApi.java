package site.festifriends.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증 관련 API", description = "카카오 소셜 로그인 및 인증 관련 API")
public interface AuthApi {

    @Operation(
        summary = "카카오 로그인 요청",
        description = "카카오 로그인 페이지로 리다이렉트합니다.",
        responses = {
            @ApiResponse(responseCode = "302", description = "카카오 로그인 페이지로 리다이렉트"),
        }
    )
    ResponseEntity<?> login();

    @Operation(
        summary = "카카오 로그인 콜백 처리",
        description = "카카오 로그인 후 콜백 URL에서 인증 코드를 받아 처리합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> handleCallback(@RequestParam String code);
}
