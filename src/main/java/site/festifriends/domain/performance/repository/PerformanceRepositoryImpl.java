package site.festifriends.domain.performance.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import site.festifriends.domain.performance.dto.PerformanceSearchRequest;
import site.festifriends.entity.Performance;
import site.festifriends.entity.QBookmark;
import site.festifriends.entity.QGroup;
import site.festifriends.entity.QPerformance;
import site.festifriends.entity.QPerformanceImage;
import site.festifriends.entity.enums.BookmarkType;

@Repository
@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Performance> searchPerformancesWithPaging(PerformanceSearchRequest request, Pageable pageable) {
        QPerformance p = QPerformance.performance;
        QPerformanceImage pi = QPerformanceImage.performanceImage;

        JPAQuery<Performance> query = queryFactory
            .selectFrom(p)
            .leftJoin(p.imgs, pi).fetchJoin()
            .where(
                titleContains(request.getTitle()),
                locationContains(request.getLocation()),
                visitEquals(request.getVisit()),
                dateRangeFilter(request.getStartDate(), request.getEndDate()),
                notDeleted()
            )
            .distinct();

        // 정렬 적용
        query = applySorting(query, request.getSort());

        // 전체 개수 조회
        long total = query.fetchCount();

        // 페이징 적용하여 결과 조회
        List<Performance> performances = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(performances, pageable, total);
    }

    @Override
    public Map<Long, Long> findGroupCountsByPerformanceIds(List<Long> performanceIds) {
        QGroup g = QGroup.group;

        List<Object[]> results = queryFactory
            .select(g.performance.id, g.count())
            .from(g)
            .where(
                g.performance.id.in(performanceIds),
                g.deleted.isNull()
            )
            .groupBy(g.performance.id)
            .fetch()
            .stream()
            .map(tuple -> new Object[]{tuple.get(g.performance.id), tuple.get(g.count())})
            .collect(Collectors.toList());

        return results.stream()
            .collect(Collectors.toMap(
                result -> (Long) result[0],
                result -> (Long) result[1]
            ));
    }

    @Override
    public Map<Long, Integer> getGroupCountsByPerformanceIds(List<Long> performanceIds) {
        List<Tuple> result = queryFactory
            .select(QGroup.group.performance.id, QGroup.group.count())
            .from(QGroup.group)
            .where(
                QGroup.group.performance.id.in(performanceIds),
                QGroup.group.deleted.isNull()
            )
            .groupBy(QGroup.group.performance.id)
            .fetch();

        Map<Long, Integer> map = performanceIds.stream()
            .collect(Collectors.toMap(id -> id, count -> 0));

        for (Tuple tuple : result) {
            Long id = tuple.get(QGroup.group.performance.id);
            Integer count = tuple.get(QGroup.group.count()).intValue();
            map.put(id, count);
        }
        return map;
    }

    @Override
    public Map<Long, Long> findFavoriteCountsByPerformanceIds(List<Long> performanceIds) {
        QBookmark b = QBookmark.bookmark;

        List<Object[]> results = queryFactory
            .select(b.targetId, b.count())
            .from(b)
            .where(
                b.type.eq(BookmarkType.PERFORMANCE),
                b.targetId.in(performanceIds)
            )
            .groupBy(b.targetId)
            .fetch()
            .stream()
            .map(tuple -> new Object[]{tuple.get(b.targetId), tuple.get(b.count())})
            .collect(Collectors.toList());

        return results.stream()
            .collect(Collectors.toMap(
                result -> (Long) result[0],
                result -> (Long) result[1]
            ));
    }

    @Override
    public Map<Long, Boolean> findIsLikedByPerformanceIds(List<Long> performanceIds, Long memberId) {
        if (memberId == null) {
            // 로그인하지 않은 경우 모든 공연에 대해 false 반환
            return performanceIds.stream()
                .collect(Collectors.toMap(id -> id, id -> false));
        }

        QBookmark b = QBookmark.bookmark;

        List<Long> likedPerformanceIds = queryFactory
            .select(b.targetId)
            .from(b)
            .where(
                b.type.eq(BookmarkType.PERFORMANCE),
                b.targetId.in(performanceIds),
                b.member.id.eq(memberId)
            )
            .fetch();

        return performanceIds.stream()
            .collect(Collectors.toMap(
                id -> id,
                likedPerformanceIds::contains
            ));
    }

    private BooleanExpression titleContains(String title) {
        return title != null && !title.trim().isEmpty() ?
            QPerformance.performance.title.containsIgnoreCase(title.trim()) : null;
    }

    private BooleanExpression locationContains(String location) {
        return location != null && !location.trim().isEmpty() ?
            QPerformance.performance.location.containsIgnoreCase(location.trim()) : null;
    }

    private BooleanExpression visitEquals(String visit) {
        return visit != null && !visit.trim().isEmpty() ?
            QPerformance.performance.visit.eq(visit.trim()) : null;
    }

    private BooleanExpression dateRangeFilter(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        QPerformance p = QPerformance.performance;

        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            return p.startDate.goe(startDateTime).and(p.endDate.loe(endDateTime));
        } else if (startDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            return p.startDate.goe(startDateTime);
        } else if (endDate != null) {
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            return p.endDate.loe(endDateTime);
        }

        return null;
    }

    private BooleanExpression notDeleted() {
        return QPerformance.performance.deleted.isNull();
    }

    private JPAQuery<Performance> applySorting(JPAQuery<Performance> query, String sort) {
        QPerformance p = QPerformance.performance;

        if (sort == null || sort.trim().isEmpty()) {
            sort = "title_asc";
        }

        switch (sort.toLowerCase()) {
            case "title_desc":
                return query.orderBy(p.title.desc());
            case "date_asc":
                return query.orderBy(p.startDate.asc());
            case "date_desc":
                return query.orderBy(p.startDate.desc());
            case "group_count_desc":
            case "group_count_asc":
                return query.orderBy(p.title.asc());
            case "title_asc":
            default:
                return query.orderBy(p.title.asc());
        }
    }
} 