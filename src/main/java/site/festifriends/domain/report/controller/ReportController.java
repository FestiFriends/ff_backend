package site.festifriends.domain.report.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.PageResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.report.dto.CreateReportRequest;
import site.festifriends.domain.report.dto.GetReportDetailResponse;
import site.festifriends.domain.report.dto.GetReportResponse;
import site.festifriends.domain.report.dto.HandleReportRequest;
import site.festifriends.domain.report.service.ReportService;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController implements ReportApi {

    private final ReportService reportService;

    @Override
    @PostMapping("")
    public ResponseEntity<?> createReport(
        @AuthenticationPrincipal UserDetailsImpl user,
        @Valid @RequestBody CreateReportRequest request
    ) {
        reportService.createReport(user.getMemberId(), request);
        return ResponseEntity.ok(ResponseWrapper.success("신고가 정상적으로 등록되었습니다"));
    }

    @Override
    @GetMapping("")
    public ResponseEntity<PageResponseWrapper<GetReportResponse>> getReportList(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        PageResponseWrapper<GetReportResponse> response = reportService.getReportList(user.getMemberId(), page, size);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{reportId}")
    public ResponseEntity<?> getReportDetail(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long reportId
    ) {
        GetReportDetailResponse data = reportService.getReportDetail(user.getMemberId(), reportId);
        return ResponseEntity.ok(ResponseWrapper.success("신고 내역이 정상적으로 조회되었습니다.", data));
    }

    @Override
    @PatchMapping("/{reportId}")
    public ResponseEntity<?> handleReport(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long reportId,
        @RequestBody HandleReportRequest request
    ) {
        String message = reportService.handleReport(user.getMemberId(), reportId, request);

        return ResponseEntity.ok(ResponseWrapper.success(message));
    }
}
