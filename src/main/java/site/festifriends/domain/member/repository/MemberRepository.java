package site.festifriends.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import site.festifriends.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findBySocialId(String socialId);

    @Modifying
    @Query("UPDATE Member m SET m.deleted = CURRENT_TIMESTAMP WHERE m = :member")
    void deleteMember(Member member);

    boolean existsByNickname(String nickname);
    
}
