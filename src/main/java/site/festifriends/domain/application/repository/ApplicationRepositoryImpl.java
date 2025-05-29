package site.festifriends.domain.application.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import site.festifriends.entity.MemberParty;
import site.festifriends.entity.QMember;
import site.festifriends.entity.QMemberParty;
import site.festifriends.entity.QParty;
import site.festifriends.entity.QFestival;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Role;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<MemberParty> findApplicationsWithSlice(Long hostId, Long cursorId, Pageable pageable) {
        QMemberParty mp = QMemberParty.memberParty;
        QMemberParty host = new QMemberParty("host");
        QMember m = QMember.member;
        QParty p = QParty.party;
        QFestival f = QFestival.festival;

        BooleanExpression hostPartiesCondition = mp.party.id.in(
            JPAExpressions.select(host.party.id)
                .from(host)
                .where(
                    host.member.id.eq(hostId),
                    host.role.eq(Role.HOST),
                    host.deleted.isNull()
                )
        );

        BooleanExpression statusCondition = mp.status.eq(ApplicationStatus.PENDING);
        BooleanExpression notDeletedCondition = mp.deleted.isNull();

        BooleanExpression cursorCondition = cursorIdLt(cursorId, mp);

        JPAQuery<MemberParty> query = queryFactory
            .selectFrom(mp)
            .join(mp.member, m).fetchJoin()
            .join(mp.party, p).fetchJoin()
            .join(p.festival, f).fetchJoin()
            .where(
                hostPartiesCondition,
                statusCondition,
                notDeletedCondition,
                cursorCondition
            )
            .orderBy(mp.id.desc());

        // Slice는 size + 1 개를 조회해서 hasNext를 판단
        int size = pageable.getPageSize();
        List<MemberParty> results = query
            .limit(size + 1)
            .fetch();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Slice<MemberParty> findAppliedApplicationsWithSlice(Long memberId, Long cursorId, Pageable pageable) {
        QMemberParty mp = QMemberParty.memberParty;
        QMember m = QMember.member;
        QParty p = QParty.party;
        QFestival f = QFestival.festival;

        BooleanExpression memberCondition = mp.member.id.eq(memberId);
        BooleanExpression notHostCondition = mp.role.ne(Role.HOST);
        BooleanExpression notDeletedCondition = mp.deleted.isNull();
        BooleanExpression notConfirmedCondition = mp.status.ne(ApplicationStatus.CONFIRMED);
        BooleanExpression cursorCondition = cursorIdLt(cursorId, mp);

        JPAQuery<MemberParty> query = queryFactory
            .selectFrom(mp)
            .join(mp.member, m).fetchJoin()
            .join(mp.party, p).fetchJoin()
            .join(p.festival, f).fetchJoin()
            .where(
                memberCondition,
                notHostCondition,
                notDeletedCondition,
                notConfirmedCondition,
                cursorCondition
            )
            .orderBy(mp.id.desc());

        // Slice는 size + 1 개를 조회해서 hasNext를 판단
        int size = pageable.getPageSize();
        List<MemberParty> results = query
            .limit(size + 1)
            .fetch();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Map<Long, MemberParty> findHostsByPartyIds(List<Long> partyIds) {
        QMemberParty mp = QMemberParty.memberParty;
        QMember m = QMember.member;

        List<MemberParty> hosts = queryFactory
            .selectFrom(mp)
            .join(mp.member, m).fetchJoin()
            .where(
                mp.party.id.in(partyIds),
                mp.role.eq(Role.HOST),
                mp.deleted.isNull()
            )
            .fetch();

        return hosts.stream()
            .collect(Collectors.toMap(
                host -> host.getParty().getId(),
                host -> host
            ));
    }

    @Override
    public boolean existsByPartyIdAndMemberIdAndRole(Long partyId, Long memberId, Role role) {
        QMemberParty mp = QMemberParty.memberParty;

        Integer count = queryFactory
            .selectOne()
            .from(mp)
            .where(
                mp.party.id.eq(partyId),
                mp.member.id.eq(memberId),
                mp.role.eq(role),
                mp.deleted.isNull()
            )
            .fetchFirst();

        return count != null;
    }

    private BooleanExpression cursorIdLt(Long cursorId, QMemberParty memberParty) {
        return cursorId != null ? memberParty.id.lt(cursorId) : null;
    }
}
