package site.festifriends.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.festifriends.entity.GroupBookmark;

import java.util.List;
import java.util.Optional;

public interface GroupBookmarkRepository extends JpaRepository<GroupBookmark, Long> {
    
    Optional<GroupBookmark> findByMemberIdAndGroupId(Long memberId, Long groupId);
    
    @Query("SELECT gb.group.id FROM GroupBookmark gb WHERE gb.member.id = :memberId AND gb.group.id IN :groupIds")
    List<Long> findBookmarkedGroupIdsByMemberIdAndGroupIds(@Param("memberId") Long memberId, @Param("groupIds") List<Long> groupIds);
    
    void deleteByMemberIdAndGroupId(Long memberId, Long groupId);
}