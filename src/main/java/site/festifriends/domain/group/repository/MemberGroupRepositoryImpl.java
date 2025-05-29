package site.festifriends.domain.group.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import site.festifriends.entity.MemberGroup;
import site.festifriends.entity.QMember;
import site.festifriends.entity.QMemberGroup;
import site.festifriends.entity.QGroup;
import site.festifriends.entity.QFestival;
import site.festifriends.entity.enums.ApplicationStatus;
import site.festifriends.entity.enums.Role;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MemberGroupRepositoryImpl implements MemberGroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<MemberGroup> findApplicationsWithSlice(Long hostId, Long cursorId, Pageable pageable) {
        QMemberGroup mg = QMemberGroup.memberGroup;
        QMemberGroup host = new QMemberGroup("host");
        QMember m = QMember.member;
        QGroup g = QGroup.group;
        QFestival f = QFestival.festival;

        BooleanExpression hostGroupsCondition = mg.group.id.in(
            JPAExpressions.select(host.group.id)
                .from(host)
                .where(
                    host.member.id.eq(hostId),
                    host.role.eq(Role.HOST),
                    host.deleted.isNull()
                )
        );

        BooleanExpression statusCondition = mg.status.eq(ApplicationStatus.PENDING);
        BooleanExpression notDeletedCondition = mg.deleted.isNull();

        BooleanExpression cursorCondition = cursorIdLt(cursorId, mg);

        JPAQuery<MemberGroup> query = queryFactory
            .selectFrom(mg)
            .join(mg.member, m).fetchJoin()
            .join(mg.group, g).fetchJoin()
            .join(g.festival, f).fetchJoin()
            .where(
                hostGroupsCondition,
                statusCondition,
                notDeletedCondition,
                cursorCondition
            )
            .orderBy(mg.id.desc());

        // Slice는 size + 1 개를 조회해서 hasNext를 판단
        int size = pageable.getPageSize();
        List<MemberGroup> results = query
            .limit(size + 1)
            .fetch();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Slice<MemberGroup> findAppliedApplicationsWithSlice(Long memberId, Long cursorId, Pageable pageable) {
        QMemberGroup mg = QMemberGroup.memberGroup;
        QMember m = QMember.member;
        QGroup g = QGroup.group;
        QFestival f = QFestival.festival;

        BooleanExpression memberCondition = mg.member.id.eq(memberId);
        BooleanExpression notHostCondition = mg.role.ne(Role.HOST);
        BooleanExpression notDeletedCondition = mg.deleted.isNull();
        BooleanExpression notConfirmedCondition = mg.status.ne(ApplicationStatus.CONFIRMED);
        BooleanExpression cursorCondition = cursorIdLt(cursorId, mg);

        JPAQuery<MemberGroup> query = queryFactory
            .selectFrom(mg)
            .join(mg.member, m).fetchJoin()
            .join(mg.group, g).fetchJoin()
            .join(g.festival, f).fetchJoin()
            .where(
                memberCondition,
                notHostCondition,
                notDeletedCondition,
                notConfirmedCondition,
                cursorCondition
            )
            .orderBy(mg.id.desc());

        // Slice는 size + 1 개를 조회해서 hasNext를 판단
        int size = pageable.getPageSize();
        List<MemberGroup> results = query
            .limit(size + 1)
            .fetch();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Slice<MemberGroup> findJoinedGroupsWithSlice(Long memberId, Long cursorId, Pageable pageable) {
        QMemberGroup mg = QMemberGroup.memberGroup;
        QMember m = QMember.member;
        QGroup g = QGroup.group;
        QFestival f = QFestival.festival;

        BooleanExpression memberCondition = mg.member.id.eq(memberId);
        BooleanExpression confirmedCondition = mg.status.eq(ApplicationStatus.CONFIRMED);
        BooleanExpression notDeletedCondition = mg.deleted.isNull();
        BooleanExpression cursorCondition = cursorIdLt(cursorId, mg);

        JPAQuery<MemberGroup> query = queryFactory
            .selectFrom(mg)
            .join(mg.member, m).fetchJoin()
            .join(mg.group, g).fetchJoin()
            .join(g.festival, f).fetchJoin()
            .where(
                memberCondition,
                confirmedCondition,
                notDeletedCondition,
                cursorCondition
            )
            .orderBy(mg.id.desc());

        // Slice는 size + 1 개를 조회해서 hasNext를 판단
        int size = pageable.getPageSize();
        List<MemberGroup> results = query
            .limit(size + 1)
            .fetch();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Map<Long, MemberGroup> findHostsByGroupIds(List<Long> groupIds) {
        QMemberGroup mg = QMemberGroup.memberGroup;
        QMember m = QMember.member;

        List<MemberGroup> hosts = queryFactory
            .selectFrom(mg)
            .join(mg.member, m).fetchJoin()
            .where(
                mg.group.id.in(groupIds),
                mg.role.eq(Role.HOST),
                mg.deleted.isNull()
            )
            .fetch();

        return hosts.stream()
            .collect(Collectors.toMap(
                host -> host.getGroup().getId(),
                host -> host
            ));
    }

    @Override
    public Map<Long, Long> findConfirmedMemberCountsByGroupIds(List<Long> groupIds) {
        QMemberGroup mg = QMemberGroup.memberGroup;

        List<Object[]> results = queryFactory
            .select(mg.group.id, mg.count())
            .from(mg)
            .where(
                mg.group.id.in(groupIds),
                mg.status.eq(ApplicationStatus.CONFIRMED),
                mg.deleted.isNull()
            )
            .groupBy(mg.group.id)
            .fetch()
            .stream()
            .map(tuple -> new Object[]{tuple.get(mg.group.id), tuple.get(mg.count())})
            .collect(Collectors.toList());

        return results.stream()
            .collect(Collectors.toMap(
                result -> (Long) result[0],
                result -> (Long) result[1]
            ));
    }

    @Override
    public boolean existsByGroupIdAndMemberIdAndRole(Long groupId, Long memberId, Role role) {
        QMemberGroup mg = QMemberGroup.memberGroup;

        return queryFactory
            .selectOne()
            .from(mg)
            .where(
                mg.group.id.eq(groupId),
                mg.member.id.eq(memberId),
                mg.role.eq(role),
                mg.deleted.isNull()
            )
            .fetchFirst() != null;
    }

    private BooleanExpression cursorIdLt(Long cursorId, QMemberGroup mg) {
        if (cursorId == null) {
            return null;
        }
        return mg.id.lt(cursorId);
    }
}
