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
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_party")
public class MemberParty extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_party_id", nullable = false)
    private Long id;

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
    @Comment("신청 상태(대기/수락/거절/확정)")
    private ApplicationStatus status = ApplicationStatus.PENDING;

     @Column(name = "application_text", length = 150)
     @Comment("신청 내용")
     private String applicationText;

    @Column(name = "joined_at")
    @Comment("가입 일시")
    private LocalDateTime joinedAt;

    @Builder
    public MemberParty(Member member, Party party, Role role, ApplicationStatus status, String applicationText) {
        this.member = member;
        this.party = party;
        this.role = role;
        this.status = status;
        this.applicationText = applicationText;
    }

    public MemberParty(Member member, Party party, Role role, ApplicationStatus status) {
        this(member, party, role, status, null);
    }

    /**
     * 모임 가입 승인
     */
    public void approve() {
        this.status = ApplicationStatus.ACCEPTED;
        this.joinedAt = LocalDateTime.now();
    }

    /**
     * 모임 가입 거절
     */
    public void reject() {
        this.status = ApplicationStatus.REJECTED;
    }

    /**
     * 모임 가입 취소 (신청자가 가입 거절)
     */
    public void cancel() {
        this.status = ApplicationStatus.REJECTED;
    }

    /**
     * 모임 가입 확정 (신청자가 확정)
     */
    public void confirm() {
        this.status = ApplicationStatus.CONFIRMED;
    }
}
