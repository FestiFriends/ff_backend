package site.festifriends.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.festifriends.entity.PostImage;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    /**
     * 게시글 ID로 이미지 목록 조회
     */
    List<PostImage> findByPostId(Long postId);

    /**
     * 게시글 ID로 이미지 하드 삭제
     */
    @Modifying
    @Query("DELETE FROM PostImage pi WHERE pi.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
