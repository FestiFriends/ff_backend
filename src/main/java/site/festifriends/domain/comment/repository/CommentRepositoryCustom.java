package site.festifriends.domain.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.entity.Comments;

public interface CommentRepositoryCustom {

    /**
     * 게시글의 댓글 목록을 커서 기반 페이지네이션으로 조회
     */
    Slice<Comments> findCommentsByPostIdWithSlice(Long postId, Long cursorId, Pageable pageable);
}
