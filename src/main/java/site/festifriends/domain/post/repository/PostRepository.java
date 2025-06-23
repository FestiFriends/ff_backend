package site.festifriends.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.festifriends.entity.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    /**
     * 모임 내 고정 게시글 조회
     * @param groupId 모임 ID
     * @return 고정 게시글 목록
     */
    List<Post> findByGroupIdAndIsPinnedTrueAndDeletedIsNull(Long groupId);

    /**
     * 모임 내 모든 게시글의 고정 상태를 해제
     * @param groupId 모임 ID
     * @return 업데이트된 행 수
     */
    @Modifying
    @Query("UPDATE Post p SET p.isPinned = false WHERE p.group.id = :groupId AND p.isPinned = true AND p.deleted IS NULL")
    int unpinAllPostsInGroup(@Param("groupId") Long groupId);

    @Modifying
    @Query("UPDATE Post p SET p.isPinned = false WHERE p.group.id = :groupId AND p.id != :postId AND p.isPinned = true AND p.deleted IS NULL")
    int unpinAllPostsInGroupExcept(@Param("groupId") Long groupId, @Param("postId") Long postId);
}
