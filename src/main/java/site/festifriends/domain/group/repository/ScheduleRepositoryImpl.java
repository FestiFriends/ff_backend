package site.festifriends.domain.group.repository;

import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.festifriends.domain.group.dto.ScheduleDto;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<ScheduleDto> getGroupSchedules(Long memberId, Long groupId, LocalDateTime startDateTime,
        LocalDateTime endDateTime) {
        String sql = """
            SELECT
                s.schedule_id,
                s.description,
                s.start_date,
                s.end_date,
                s.location,
                s.created_at,
                s.event_color,
                m.member_id,
                m.nickname,
                mi.src
            FROM schedule s
            JOIN `groups` g ON s.group_id = g.group_id
            JOIN member m ON s.member_id = m.member_id
            LEFT JOIN member_image mi ON m.member_id = mi.member_id
            WHERE s.group_id = :groupId
            AND s.start_date >= :startDateTime
            AND s.end_date <= :endDateTime
            ORDER BY s.start_date
            """;

        List<Object[]> data = em.createNativeQuery(sql)
            .setParameter("groupId", groupId)
            .setParameter("startDateTime", startDateTime)
            .setParameter("endDateTime", endDateTime)
            .getResultList();

        List<ScheduleDto> result = new ArrayList<>();

        for (Object[] row : data) {
            ScheduleDto.Author author = ScheduleDto.Author.builder()
                .id((Long) row[7])
                .name((String) row[8])
                .profileImage((String) row[9])
                .build();

            result.add(ScheduleDto.builder()
                .id((Long) row[0])
                .description((String) row[1])
                .startAt(((Timestamp) row[2]).toLocalDateTime())
                .endAt(((Timestamp) row[3]).toLocalDateTime())
                .location((String) row[4])
                .createdAt(((Timestamp) row[5]).toLocalDateTime())
                .eventColor((String) row[6])
                .author(author)
                .isMine(memberId.equals(author.getId()))
                .build());
        }

        return result;
    }
}
