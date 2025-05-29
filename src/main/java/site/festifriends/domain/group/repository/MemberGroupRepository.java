package site.festifriends.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.MemberGroup;

public interface MemberGroupRepository extends JpaRepository<MemberGroup, Long>, MemberGroupRepositoryCustom {
}
