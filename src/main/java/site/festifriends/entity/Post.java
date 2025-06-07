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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import site.festifriends.common.model.SoftDeleteEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post",
    indexes = @Index(name = "idx_post_pinned",
        columnList = "is_pinned"))
public class Post extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @Comment("모임")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @Comment("작성자")
    private Member author;

    @Column(name = "content", nullable = false)
    @Comment("게시글 내용")
    private String content;

    @Column(name = "is_pinned", nullable = false)
    @Comment("상단 고정 여부")
    private boolean isPinned = false;

    @Column(name = "is_reported", nullable = false)
    @Comment("신고 여부")
    private boolean isReported = false;

    @Column(name = "comment_count", nullable = false)
    @Comment("댓글 수")
    private int commentCount = 0;

    @Column(name = "reaction_count", nullable = false)
    @Comment("게시글 반응 수")
    private int reactionCount = 0;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostReaction> reactions = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comments> comments = new ArrayList<>();

    @Builder
    public Post(Group group, Member author, String content) {
        this.group = group;
        this.author = author;
        this.content = content;
    }

    /**
     * 첨부 이미지 수 계산
     *
     * @return 이미지 수
     */
    public int getImageCount() {
        return images.size();
    }

    /**
     * 자신의 게시글인지 확인
     */
    public boolean isMine(Long memberId) {
        return author.getId().equals(memberId);
    }

    /**
     * 게시글 내용 수정
     */
    public void updateContent(String content) {
        this.content = content;
    }

    /**
     * 게시글 신고 처리
     */
    public void report() {
        this.isReported = true;
    }

    /**
     * 댓글 수 증가
     */
    public void incrementCommentCount() {
        this.commentCount++;
    }

    /**
     * 댓글 수 감소
     */
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    /**
     * 반응 수 증가
     */
    public void incrementReactionCount() {
        this.reactionCount++;
    }

    /**
     * 반응 수 감소
     */
    public void decrementReactionCount() {
        if (this.reactionCount > 0) {
            this.reactionCount--;
        }
    }

    /**
     * 게시글 고정 설정
     */
    public void setPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }
}
