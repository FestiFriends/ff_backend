package site.festifriends.domain.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.common.response.PageResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.report.dto.CreateReportRequest;
import site.festifriends.domain.report.dto.GetReportResponse;
import site.festifriends.domain.report.dto.HandleReportRequest;

@Tag(name = "Report", description = "신고 관련 API")
public interface ReportApi {

    @Operation(
        summary = "신고 등록",
        description = "신고를 등록합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    ResponseEntity<?> createReport(
        @AuthenticationPrincipal UserDetailsImpl user,
        @Valid @RequestBody CreateReportRequest request
    );

    @Operation(
        summary = "신고 목록 조회",
        description = "신고 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "신고 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    ResponseEntity<PageResponseWrapper<GetReportResponse>> getReportList(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    );

    @Operation(
        summary = "신고 상세 조회",
        description = "신고의 상세 정보를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "신고 상세 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    ResponseEntity<?> getReportDetail(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long reportId
    );

    @Operation(
        summary = "신고 처리(승인/반려)",
        description = "신고를 처리합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "신고 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    ResponseEntity<?> handleReport(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long reportId,
        @RequestBody HandleReportRequest request
    );
}
