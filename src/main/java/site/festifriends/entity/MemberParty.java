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
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_party")
public class MemberParty extends SoftDeleteEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Comment("모임 내 역할(방장/일반)")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("신청 상태(대기중/승인됨/거절됨/취소됨)")
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "joined_at")
    @Comment("가입 일시")
    private LocalDateTime joinedAt;

    /**
     * 모임 가입 승인
     */
    public void approve() {
        this.status = ApplicationStatus.APPROVED;
        this.joinedAt = LocalDateTime.now();
    }

    /**
     * 모임 가입 거절
     */
    public void reject() {
        this.status = ApplicationStatus.REJECTED;
    }

    /**
     * 모임 가입 취소
     */
    public void cancel() {
        this.status = ApplicationStatus.CANCELLED;
    }
}
