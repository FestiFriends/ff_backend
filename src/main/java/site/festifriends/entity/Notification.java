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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;
import site.festifriends.entity.enums.NotificationType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
public class Notification extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("알림을 받는 회원")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Comment("알림 타입")
    private NotificationType type;

    @Column(name = "message", nullable = false)
    @Comment("알림 메시지")
    private String message;

    @Column(name = "is_read", nullable = false)
    @Comment("읽음 여부")
    private boolean isRead = false;

    @Column(name = "target_id")
    @Comment("알림 대상 ID (공지, 댓글 등)")
    private Long targetId;

    @Column(name = "sub_target_id")
    @Comment("알림 서브 대상 ID (예: 새 글의 경우 그룹 ID )")
    private Long subTargetId;

    @Builder
    public Notification(Member member, NotificationType type, String message, Long targetId) {
        this.member = member;
        this.type = type;
        this.message = message;
        this.targetId = targetId;
        this.isRead = false;
    }

    /**
     * 알림 읽음 처리
     */
    public void markAsRead() {
        this.isRead = true;
    }
}
