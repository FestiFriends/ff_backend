package site.festifriends.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;
import site.festifriends.entity.enums.ReportReasonType;
import site.festifriends.entity.enums.ReportStatus;
import site.festifriends.entity.enums.ReportType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "report")
public class Report extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("신고한 회원")
    private Member member;

    @Column(name = "target_id", nullable = false)
    @Comment("신고 대상 ID")
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Comment("신고 타입(모임, 리뷰, 사용자, 채팅, 게시글, 댓글)")
    private ReportType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    @Comment("신고 사유")
    private ReportReasonType reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("신고 상태(접수/처리 중/완료)")
    private ReportStatus status;

    @Column(name = "processed_at")
    @Comment("처리 일시")
    private LocalDateTime processedAt;

    @Column(name = "detail")
    @Comment("신고 상세 사유")
    private String detail;

    @Builder
    public Report(Member member, Long targetId, ReportType type, ReportReasonType reason, String detail) {
        this.member = member;
        this.targetId = targetId;
        this.type = type;
        this.reason = reason;
        this.status = ReportStatus.PENDING;
        this.detail = detail;
    }

    /**
     * 신고 처리
     *
     * @param status 처리 상태
     */
    public void process(ReportStatus status) {
        this.status = status;
        this.processedAt = LocalDateTime.now();
    }
}
