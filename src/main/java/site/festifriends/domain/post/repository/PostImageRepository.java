package site.festifriends.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}