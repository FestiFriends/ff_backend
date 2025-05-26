package site.festifriends.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;
import site.festifriends.entity.enums.ReportStatus;
import site.festifriends.entity.enums.ReportType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "report")
public class Report extends SoftDeleteEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("신고한 회원")
    private Member member;

    @Column(name = "target_id", nullable = false)
    @Comment("신고 대상 ID")
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Comment("신고 타입(게시물, 채팅, 모임, 유저, 리뷰)")
    private ReportType type;

    @Column(name = "reason", nullable = false)
    @Comment("신고 사유")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("신고 상태(접수/처리 중/완료)")
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "processed_at")
    @Comment("처리 일시")
    private LocalDateTime processedAt;

    /**
     * 신고 처리
     * @param status 처리 상태
     */
    public void process(ReportStatus status) {
        this.status = status;
        this.processedAt = LocalDateTime.now();
    }
}
