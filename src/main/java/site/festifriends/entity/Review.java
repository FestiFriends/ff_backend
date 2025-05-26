package site.festifriends.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review extends SoftDeleteEntity {

    @Column(name = "content", nullable = false)
    @Comment("리뷰 내용")
    private String content;
    @Column(name = "score", nullable = false)
    @Comment("리뷰 점수(0.5점 단위)")
    private Double score;
}
