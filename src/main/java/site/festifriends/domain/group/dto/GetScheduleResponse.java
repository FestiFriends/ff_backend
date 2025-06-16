package site.festifriends.domain.group.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetScheduleResponse {

    private Integer scheduleCount;
    private List<ScheduleDto> schedules;
}
