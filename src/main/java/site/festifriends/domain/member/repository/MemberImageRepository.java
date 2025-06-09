package site.festifriends.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.MemberImage;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {

    Optional<MemberImage> findByMemberId(Long memberId);
}
