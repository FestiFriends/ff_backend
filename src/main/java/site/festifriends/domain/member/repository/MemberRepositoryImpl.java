package site.festifriends.domain.member.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import site.festifriends.domain.image.dto.ImageDto;
import site.festifriends.domain.member.dto.LikedMemberDto;
import site.festifriends.domain.member.dto.LikedPerformanceDto;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public Slice<LikedMemberDto> getMyLikedMembers(Long memberId, Long cursorId, Pageable pageable) {
        int pageSize = pageable.getPageSize() + 1;

        String sql = """
            SELECT m.nickname, m.gender, m.age, m.member_id,
            GROUP_CONCAT(DISTINCT CONCAT(mi.member_image_id, '|', mi.src, '|', IFNULL(mi.alt, ''))) AS images,
            GROUP_CONCAT(mt.tag) as tags, b.bookmark_id
            FROM bookmark b
            JOIN member m ON b.target_id = m.member_id
            LEFT JOIN member_tags mt ON m.member_id = mt.member_id
            LEFT JOIN member_image mi ON m.member_id = mi.member_id
            WHERE b.member_id = :memberId
            AND b.type = 'MEMBER'
            AND (:cursorId IS NULL OR b.bookmark_id <= :cursorId)
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
                parseImage((String) row[4]),
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
            GROUP_CONCAT(DISTINCT pt.time ORDER BY pt.order_index SEPARATOR '|') AS time,
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
            AND (:cursorId IS NULL OR b.bookmark_id <= :cursorId)
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

                List<String> cast = splitToList((String) row[5], ",");
                List<String> crew = splitToList((String) row[6], ",");
                String runtime = (String) row[7];
                String age = (String) row[8];
                List<String> productionCompany = splitToList((String) row[9], ",");
                List<String> agency = splitToList((String) row[10], ",");
                List<String> host = splitToList((String) row[11], ",");
                List<String> organizer = splitToList((String) row[12], ",");
                List<String> price = splitToList((String) row[13], ",");
                String poster = (String) row[14];
                String state = String.valueOf(row[15]);
                String visit = (String) row[16];

                List<ImageDto> images = parseImages((String) row[17]);
                List<String> time = splitToList((String) row[18], "\\|");

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

    @Override
    public Object[] getMemberProfile(Long targetId) {
        String sql = """
            SELECT m.member_id, m.nickname, m.age, m.gender,
            GROUP_CONCAT(DISTINCT CONCAT(mi.member_image_id, '|', mi.src, '|', IFNULL(mi.alt, ''))) AS images,
            m.introduce,
            GROUP_CONCAT(DISTINCT mt.tag) AS tags,
            GROUP_CONCAT(DISTINCT ms.sns_link) AS sns
            FROM member m
            LEFT JOIN member_image mi ON m.member_id = mi.member_id
            LEFT JOIN member_tags mt ON m.member_id = mt.member_id
            LEFT JOIN member_sns ms ON m.member_id = ms.member_id
            WHERE m.member_id = :memberId
            AND m.deleted IS NULL
            GROUP BY m.member_id, m.nickname, m.age, m.gender, m.introduce
            """;

        Query memberQuery = em.createNativeQuery(sql);

        memberQuery.setParameter("memberId", targetId);

        return (Object[]) memberQuery.getSingleResult();
    }

    @Override
    public Object[] getMemberExtraData(Long targetId, Long memberId) {
        if (memberId == null) {
            return new Object[]{0, 0, 0};
        }

        String sql = """
            SELECT
            CASE WHEN EXISTS (
                SELECT 1 FROM bookmark b
                WHERE b.member_id = :memberId
                AND b.target_id = :targetId
                AND b.type = 'MEMBER'
            ) THEN 1 ELSE 0 END as isLiked,
            CASE WHEN EXISTS (
                SELECT 1 FROM report r
                WHERE r.member_id = :memberId
                AND r.target_id = :targetId
                AND r.type = 'MEMBER'
            ) THEN 1 ELSE 0 END as isReported,
            CASE WHEN :targetId = :memberId THEN 1 ELSE 0 END AS isMine
            """;

        Query memberQuery = em.createNativeQuery(sql);
        memberQuery.setParameter("memberId", memberId);
        memberQuery.setParameter("targetId", targetId);

        return (Object[]) memberQuery.getSingleResult();
    }

    private List<String> splitToList(String value, String delimiter) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(value.split(delimiter));
    }

    private ImageDto parseImage(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String[] parts = value.split("\\|");
        String id = parts.length > 0 ? parts[0] : null;
        String src = parts.length > 1 ? parts[1] : null;
        String alt = parts.length > 2 ? parts[2] : null;
        return ImageDto.builder()
            .id(id)
            .src(src)
            .alt(alt)
            .build();
    }

    private List<ImageDto> parseImages(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(value.split(","))
            .map(img -> {
                String[] parts = img.split("\\|");
                String id = parts.length > 0 ? parts[0] : null;
                String src = parts.length > 1 ? parts[1] : null;
                String alt = parts.length > 2 ? parts[2] : null;
                return ImageDto.builder()
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
