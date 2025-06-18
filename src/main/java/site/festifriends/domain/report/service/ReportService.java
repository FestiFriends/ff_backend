package site.festifriends.domain.report.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.PageResponseWrapper;
import site.festifriends.domain.member.service.MemberService;
import site.festifriends.domain.report.dto.CreateReportRequest;
import site.festifriends.domain.report.dto.GetReportDetailResponse;
import site.festifriends.domain.report.dto.GetReportResponse;
import site.festifriends.domain.report.dto.HandleReportRequest;
import site.festifriends.domain.report.repository.ReportImageRepository;
import site.festifriends.domain.report.repository.ReportRepository;
import site.festifriends.entity.Member;
import site.festifriends.entity.Report;
import site.festifriends.entity.ReportImage;
import site.festifriends.entity.enums.MemberRole;
import site.festifriends.entity.enums.ReportStatus;
import site.festifriends.entity.enums.ReportType;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MemberService memberService;
    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;

    @Transactional
    public void createReport(Long memberId, CreateReportRequest request) {

        Member member = memberService.getMemberById(memberId);

        Report newReport = Report.builder()
            .member(member)
            .targetId(request.getTargetId())
            .type(request.getCategory())
            .reason(request.getReason())
            .detail(request.getDetails())
            .build();

        Report saved = reportRepository.save(newReport);

        List<ReportImage> reportImages = request.getSnapshots().stream()
            .map(image -> ReportImage.builder()
                .report(saved)
                .alt(image.getAlt())
                .src(image.getSrc())
                .build())
            .toList();

        reportImageRepository.saveAll(reportImages);
    }

    public PageResponseWrapper<GetReportResponse> getReportList(Long memberId, int page, int size) {
        Member member = memberService.getMemberById(memberId);

        if (member.getMemberRole() != MemberRole.ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "관리자만 접근할 수 있습니다.");
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Report> reports = reportRepository.getReportPage(pageable);

        List<GetReportResponse> data = reports.getContent().stream()
            .map(report -> GetReportResponse.builder()
                .id(report.getId())
                .createdAt(report.getCreatedAt())
                .category(report.getType().name())
                .reason(report.getReason().name())
                .status(report.getStatus().name())
                .build())
            .toList();

        return PageResponseWrapper.success(
            "신고 목록이 정상적으로 조회되었습니다.",
            data,
            reports.getNumber() + 1,
            reports.getSize(),
            (int) reports.getTotalElements(),
            reports.getTotalPages(),
            reports.isFirst(),
            reports.isLast()
        );
    }

    public GetReportDetailResponse getReportDetail(Long memberId, Long reportId) {
        Member member = memberService.getMemberById(memberId);

        if (member.getMemberRole() != MemberRole.ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "관리자만 접근할 수 있습니다.");
        }

        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "신고를 찾을 수 없습니다."));

        List<ReportImage> reportImages = reportImageRepository.findByReportId(reportId);

        List<GetReportDetailResponse.ReportImages> snapshots = reportImages.stream()
            .map(image -> GetReportDetailResponse.ReportImages.builder()
                .id(image.getId())
                .alt(image.getAlt())
                .src(image.getSrc())
                .build())
            .toList();

        return GetReportDetailResponse.builder()
            .id(report.getId())
            .createdAt(report.getCreatedAt())
            .category(report.getType().name())
            .reason(report.getReason().name())
            .snapshots(snapshots)
            .details(report.getDetail())
            .status(report.getStatus().name())
            .build();
    }

    @Transactional
    public String handleReport(Long memberId, Long reportId, HandleReportRequest request) {
        Member member = memberService.getMemberById(memberId);

        if (member.getMemberRole() != MemberRole.ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "관리자만 접근할 수 있습니다.");
        }

        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "신고를 찾을 수 없습니다."));

        if (report.getStatus() == ReportStatus.PENDING) {
            if (request.getReportStatus() == ReportStatus.APPROVED) {
                report.process(ReportStatus.APPROVED);

                if (report.getType() == ReportType.USER) {
                    Member targetMember = memberService.getMemberById(report.getTargetId());
                    targetMember.ban();
                }
                return "해당 신고가 승인되었습니다";
            } else if (request.getReportStatus() == ReportStatus.REJECTED) {
                report.process(ReportStatus.REJECTED);
                return "해당 신고가 반려되었습니다";
            }
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 처리된 신고입니다.");
        }

        return "";
    }
}
