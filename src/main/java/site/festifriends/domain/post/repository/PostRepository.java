package site.festifriends.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}