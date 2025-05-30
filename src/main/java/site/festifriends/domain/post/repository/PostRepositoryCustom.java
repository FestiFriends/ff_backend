package site.festifriends.domain.post.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import site.festifriends.entity.Post;

public interface PostRepositoryCustom {
    
    /**
     * 모임 내 게시글 목록을 커서 기반 페이지네이션으로 조회
     * @param groupId 모임 ID
     * @param cursorId 커서 ID (null인 경우 첫 페이지)
     * @param pageable 페이지 정보
     * @return 게시글 목록
     */
    Slice<Post> findPostsByGroupIdWithSlice(Long groupId, Long cursorId, Pageable pageable);
}