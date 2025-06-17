package site.festifriends.domain.group.dto;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetScheduleRequest {

    @NotNull(message = "일정 내용은 필수입니다.")
    private String description;
    @NotNull(message = "시작 날짜는 필수입니다.")
    private OffsetDateTime startAt;
    @NotNull(message = "종료 날짜는 필수입니다.")
    private OffsetDateTime endAt;
    @NotNull(message = "장소는 필수입니다.")
    private String location;
    @NotNull(message = "일정 색상은 필수입니다.")
    private String eventColor;
}
