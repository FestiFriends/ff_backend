package site.festifriends.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.Comments;

public interface CommentRepository extends JpaRepository<Comments, Long>, CommentRepositoryCustom {

    /**
     * 특정 게시글의 댓글 수 조회
     */
    long countByPostIdAndDeletedIsNull(Long postId);
}
