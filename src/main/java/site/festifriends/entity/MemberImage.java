package site.festifriends.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import site.festifriends.common.model.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_image")
public class MemberImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_image_id", nullable = false)
    private Long id;

    @Column(name = "src", nullable = false)
    @Comment("소개 이미지 URL")
    private String src;

    @Column(name = "alt")
    @Comment("소개 이미지 설명")
    private String alt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public MemberImage(Member member, String src, String alt) {
        this.member = member;
        this.src = src;
        this.alt = alt;
    }

    public void updateImage(String src, String alt) {
        this.src = src;
        this.alt = alt;
    }
}
