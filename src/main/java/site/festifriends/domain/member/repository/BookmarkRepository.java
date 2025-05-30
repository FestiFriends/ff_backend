package site.festifriends.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

}
