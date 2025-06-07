package site.festifriends.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.festifriends.common.model.SoftDeleteEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment",
    indexes = {
        @Index(name = "idx_comment_post_id", columnList = "post_id"),
        @Index(name = "idx_comment_author_id", columnList = "author_id"),
        @Index(name = "idx_comment_created_at", columnList = "created_at")
    })
public class Comments extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "is_reported", nullable = false)
    private boolean isReported = false;

    @Builder
    public Comments(Post post, Member author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
    }

    /**
     * 자신의 댓글인지 확인
     */
    public boolean isMine(Long memberId) {
        return author.getId().equals(memberId);
    }

    /**
     * 댓글 내용 수정
     */
    public void updateContent(String content) {
        this.content = content;
    }

    /**
     * 댓글 신고 처리
     */
    public void report() {
        this.isReported = true;
    }
}
