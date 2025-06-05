package site.festifriends.domain.post.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.festifriends.entity.PostReaction;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    /**
     * 특정 게시글에 대한 특정 회원의 반응 존재 여부 확인
     */
    @Query("SELECT pr FROM PostReaction pr WHERE pr.post.id = :postId AND pr.member.id = :memberId")
    Optional<PostReaction> findByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    /**
     * 특정 게시글에 대한 특정 회원의 반응 존재 여부 확인 (boolean 반환)
     */
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    /**
     * 특정 게시글에 대한 전체 반응 수 조회
     */
    long countByPostId(Long postId);

    /**
     * 특정 게시글에 대한 특정 회원의 반응 삭제
     */
    void deleteByPostIdAndMemberId(Long postId, Long memberId);
}
