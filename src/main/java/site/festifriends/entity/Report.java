package site.festifriends.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;

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
    @Column(name = "type", nullable = false)
    @Comment("신고 타입(게시물, 채팅, 모임, 유저, 리뷰)")
    private String type;
    @Column(name = "reason", nullable = false)
    @Comment("신고 사유")
    private String reason;
    @Column(name = "status", nullable = false)
    @Comment("신고 상태(접수/처리 중/완료)")
    private String status;
}
