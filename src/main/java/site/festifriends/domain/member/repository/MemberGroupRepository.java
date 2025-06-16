package site.festifriends.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.MemberGroup;

public interface MemberGroupRepository extends JpaRepository<MemberGroup, Long> {

    Optional<MemberGroup> findByMemberIdAndGroupId(Long memberId, Long groupId);
}
