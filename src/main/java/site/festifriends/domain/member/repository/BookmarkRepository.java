package site.festifriends.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.Bookmark;
import site.festifriends.entity.enums.BookmarkType;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {

    Optional<Bookmark> findByMemberIdAndTypeAndTargetId(Long memberId, BookmarkType type, Long targetId);

    boolean existsByMemberIdAndTypeAndTargetId(Long memberId, BookmarkType type, Long targetId);

    void deleteByMemberIdAndTypeAndTargetId(Long memberId, BookmarkType type, Long targetId);
}
