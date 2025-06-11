package site.festifriends.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.domain.auth.UserDetailsImpl;

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
        summary = "카카오 로그인 요청(로컬 개발용)",
        description = "카카오 로그인 페이지로 리다이렉트합니다.",
        responses = {
            @ApiResponse(responseCode = "302", description = "카카오 로그인 페이지로 리다이렉트"),
        }
    )
    ResponseEntity<?> localLogin();

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

    @Operation(
        summary = "카카오 로그인 콜백 처리(로컬 개발용)",
        description = "카카오 로그인 후 콜백 URL에서 인증 코드를 받아 처리합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> localHandleCallback(@RequestParam String code);

    @Operation(
        summary = "로그아웃",
        description = "사용자의 토큰을 무효화하고 로그아웃합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request);

    @Operation(
        summary = "액세스 토큰 재발급",
        description = "리프레시 토큰을 사용하여 액세스 토큰을 재발급합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "액세스 토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<?> reissueAccessToken(HttpServletRequest request);
}
