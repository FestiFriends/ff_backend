package site.festifriends.domain.group.repository;

import java.time.LocalDateTime;
import java.util.List;
import site.festifriends.domain.group.dto.ScheduleDto;

public interface ScheduleRepositoryCustom {

    List<ScheduleDto> getGroupSchedules(Long memberId, Long groupId, LocalDateTime startDateTime,
        LocalDateTime endDateTime);

}
