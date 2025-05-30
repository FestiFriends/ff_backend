package site.festifriends.domain.member.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import site.festifriends.domain.member.dto.LikedMemberDto;
import site.festifriends.domain.member.dto.LikedPerformanceDto;
import site.festifriends.domain.member.dto.LikedPerformanceImageDto;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Slice<LikedMemberDto> getMyLikedMembers(Long memberId, Long cursorId, Pageable pageable) {
        int pageSize = pageable.getPageSize() + 1;

        String sql = """
            SELECT m.nickname, m.gender, m.age, m.member_id, m.profile_image_url, GROUP_CONCAT(mt.tag) as tags, b.bookmark_id
            FROM bookmark b
            JOIN member m ON b.target_id = m.member_id
            LEFT JOIN member_tags mt ON m.member_id = mt.member_id
            WHERE b.member_id = :memberId
            AND b.type = 'MEMBER'
            AND (:cursorId IS NULL OR b.bookmark_id < :cursorId)
            GROUP BY m.nickname, m.gender, m.age, m.member_id, m.profile_image_url, b.bookmark_id
            ORDER BY b.bookmark_id DESC
            LIMIT :pageSize
            """;

        Query query = em.createNativeQuery(sql);

        query.setParameter("memberId", memberId);
        query.setParameter("cursorId", cursorId);
        query.setParameter("pageSize", pageSize);

        List<Object[]> resultList = query.getResultList();

        List<LikedMemberDto> dtos = resultList.stream()
            .map(row -> new LikedMemberDto(
                (String) row[0],
                (String) row[1],
                (Integer) row[2],
                row[3].toString(),
                false,
                (String) row[4],
                row[5] == null ? new ArrayList<>() :
                    Arrays.stream(((String) row[5]).split(","))
                        .map(tag -> "#" + tag)
                        .collect(Collectors.toList()),
                (Long) row[6]
            ))
            .collect(Collectors.toList());

        boolean hasNext = dtos.size() == pageSize;

        return new SliceImpl<>(dtos, pageable, hasNext);
    }

    @Override
    public Long countMyLikedMembers(Long memberId) {
        String sql = """
            SELECT COUNT(*)
            FROM bookmark b
            WHERE b.member_id = :memberId
            AND b.type = 'MEMBER'
            """;

        Query query = em.createNativeQuery(sql);

        query.setParameter("memberId", memberId);

        List<Long> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return 0L;
        }

        return resultList.get(0);
    }

    @Override
    public Slice<LikedPerformanceDto> getMyLikedPerformances(Long memberId, Long cursorId, Pageable pageable) {
        int pageSize = pageable.getPageSize() + 1;

        String sql = """
            SELECT p.performance_id, p.title, p.start_date, p.end_date, p.location,
            GROUP_CONCAT(DISTINCT c.cast_member ORDER BY c.order_index) AS `cast`,
            GROUP_CONCAT(DISTINCT cr.crew_member ORDER BY cr.order_index) AS crew,
            p.runtime, p.age, GROUP_CONCAT(DISTINCT pc.company ORDER BY pc.order_index) AS production_company,
            GROUP_CONCAT(DISTINCT a.agency ORDER BY a.order_index) AS agency,
            GROUP_CONCAT(DISTINCT h.host ORDER BY h.order_index) AS host,
            GROUP_CONCAT(DISTINCT o.organizer ORDER BY o.order_index) AS organizer,
            GROUP_CONCAT(DISTINCT pr.price ORDER BY pr.order_index) AS price,
            p.poster_url, p.state, p.visit,
            GROUP_CONCAT(DISTINCT CONCAT(pi.performance_image_id, '|', pi.src, '|', IFNULL(pi.alt, ''))) AS images,
            GROUP_CONCAT(DISTINCT pt.time ORDER BY pt.order_index) AS time,
            b.bookmark_id
            FROM bookmark b
            JOIN performance p ON b.target_id = p.performance_id
            LEFT JOIN performance_cast c ON p.performance_id = c.performance_id
            LEFT JOIN performance_crew cr ON p.performance_id = cr.performance_id
            LEFT JOIN performance_production_company pc ON p.performance_id = pc.performance_id
            LEFT JOIN performance_agency a ON p.performance_id = a.performance_id
            LEFT JOIN performance_host h ON p.performance_id = h.performance_id
            LEFT JOIN performance_organizer o ON p.performance_id = o.performance_id
            LEFT JOIN performance_price pr ON p.performance_id = pr.performance_id
            LEFT JOIN performance_image pi ON p.performance_id = pi.performance_id
            LEFT JOIN performance_time pt ON p.performance_id = pt.performance_id
            WHERE b.member_id = :memberId
            AND b.type = 'PERFORMANCE'
            AND (:cursorId IS NULL OR b.bookmark_id < :cursorId)
            GROUP BY p.performance_id, p.title, p.start_date, p.end_date, p.location,
            p.runtime, p.age, p.poster_url, p.state, p.visit, b.bookmark_id
            ORDER BY b.bookmark_id DESC
            LIMIT :pageSize
            """;

        Query query = em.createNativeQuery(sql);

        query.setParameter("memberId", memberId);
        query.setParameter("cursorId", cursorId);
        query.setParameter("pageSize", pageSize);
        List<Object[]> resultList = query.getResultList();

        List<LikedPerformanceDto> dtos = resultList.stream()
            .map(row -> {
                Long id = ((Number) row[0]).longValue();
                String title = (String) row[1];
                LocalDateTime startDate = toLocalDateTime(row[2]);
                LocalDateTime endDate = toLocalDateTime(row[3]);
                String location = (String) row[4];

                List<String> cast = splitToList((String) row[5]);
                List<String> crew = splitToList((String) row[6]);
                String runtime = (String) row[7];
                String age = (String) row[8];
                List<String> productionCompany = splitToList((String) row[9]);
                List<String> agency = splitToList((String) row[10]);
                List<String> host = splitToList((String) row[11]);
                List<String> organizer = splitToList((String) row[12]);
                List<String> price = splitToList((String) row[13]);
                String poster = (String) row[14];
                String state = String.valueOf(row[15]);
                String visit = (String) row[16];

                List<LikedPerformanceImageDto> images = parseImages((String) row[17]);
                List<LocalDateTime> time = splitToLocalDateTimeList((String) row[18]);

                Long bookmarkId = row[19] == null ? null : ((Number) row[19]).longValue();

                return new LikedPerformanceDto(
                    id, title, startDate, endDate, location, cast, crew, runtime, age,
                    productionCompany, agency, host, organizer, price, poster, state, visit,
                    images, time, bookmarkId
                );
            })
            .collect(Collectors.toList());

        boolean hasNext = dtos.size() == pageSize;

        return new SliceImpl<>(dtos, pageable, hasNext);
    }

    private List<String> splitToList(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(value.split(","));
    }

    private List<LocalDateTime> splitToLocalDateTimeList(String value) {
        if (value == null || value.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
            .map(String::trim)
            .map(v -> LocalDateTime.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .collect(Collectors.toList());
    }

    private List<LikedPerformanceImageDto> parseImages(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(value.split(","))
            .map(img -> {
                String[] parts = img.split("\\|");
                String id = parts.length > 0 ? parts[0] : null;
                String src = parts.length > 1 ? parts[1] : null;
                String alt = parts.length > 2 ? parts[2] : null;
                return LikedPerformanceImageDto.builder()
                    .id(id)
                    .src(src)
                    .alt(alt)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private LocalDateTime toLocalDateTime(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof java.sql.Timestamp ts) {
            return ts.toLocalDateTime();
        } else if (obj instanceof LocalDateTime ldt) {
            return ldt;
        }
        return null;
    }
}
