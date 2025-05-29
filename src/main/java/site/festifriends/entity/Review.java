package site.festifriends.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;
import site.festifriends.entity.enums.ReviewTag;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    @Comment("리뷰 작성자")
    private Member reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    @Comment("리뷰 대상자")
    private Member reviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @Comment("관련 모임")
    private Group group;

    @Column(name = "content", nullable = false)
    @Comment("리뷰 내용")
    private String content;

    @Column(name = "score", nullable = false)
    @Comment("리뷰 점수(0.5점 단위)")
    private Double score;

    @ElementCollection
    @CollectionTable(name = "review_tags", joinColumns = @JoinColumn(name = "review_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    @Comment("리뷰 태그")
    private List<ReviewTag> tags = new ArrayList<>();

    @Builder
    public Review(Member reviewer, Member reviewee, Group group, String content, Double score, List<ReviewTag> tags) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.group = group;
        this.content = content;
        this.score = score;
        if (tags != null) {
            this.tags = tags;
        }
    }
}
