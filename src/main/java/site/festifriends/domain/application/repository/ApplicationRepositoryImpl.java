package site.festifriends.domain.application.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import site.festifriends.entity.MemberParty;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Role;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Slice<MemberParty> findApplicationsWithSlice(Long hostId, Long cursorId, Pageable pageable) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT mp FROM MemberParty mp ")
            .append("JOIN FETCH mp.member m ")
            .append("JOIN FETCH mp.party p ")
            .append("JOIN FETCH p.festival f ")
            .append("WHERE mp.party.id IN (")
            .append("    SELECT host.party.id FROM MemberParty host ")
            .append("    WHERE host.member.id = :hostId ")
            .append("    AND host.role = :role ")
            .append("    AND host.deleted IS NULL")
            .append(") ")
            .append("AND mp.status = :status ")
            .append("AND mp.deleted IS NULL ");

        if (cursorId != null) {
            jpql.append("AND mp.id < :cursorId ");
        }

        jpql.append("ORDER BY mp.id DESC");

        TypedQuery<MemberParty> query = entityManager.createQuery(jpql.toString(), MemberParty.class);
        query.setParameter("hostId", hostId);
        query.setParameter("role", Role.HOST);
        query.setParameter("status", ApplicationStatus.PENDING);

        if (cursorId != null) {
            query.setParameter("cursorId", cursorId);
        }

        // Slice는 size + 1 개를 조회해서 hasNext를 판단
        int size = pageable.getPageSize();
        query.setMaxResults(size + 1);

        List<MemberParty> results = query.getResultList();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size); // 마지막 하나 제거
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
} 