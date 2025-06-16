package site.festifriends.domain.group.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.group.dto.GetScheduleRequest;
import site.festifriends.domain.group.dto.GetScheduleResponse;
import site.festifriends.domain.group.service.ScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups/{groupId}/schedules")
public class ScheduleController implements ScheduleApi {

    private final ScheduleService scheduleService;

    @Override
    @GetMapping("")
    public ResponseEntity<?> getSchedules(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long groupId,
        @RequestParam OffsetDateTime startDate,
        @RequestParam OffsetDateTime endDate
    ) {
        LocalDateTime startDateTime = startDate.toLocalDateTime();
        LocalDateTime endDateTime = endDate.toLocalDateTime();

        GetScheduleResponse response = scheduleService.getGroupSchedules(
            userDetails.getMemberId(),
            groupId,
            startDateTime,
            endDateTime
        );
        return ResponseEntity.ok(ResponseWrapper.success(
            "일정 목록 조회에 성공했습니다.",
            response
        ));
    }

    @Override
    @PostMapping("")
    public ResponseEntity<?> createSchedule(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long groupId,
        @RequestBody @Valid GetScheduleRequest getScheduleRequest
    ) {
        scheduleService.createSchedule(
            userDetails.getMemberId(),
            groupId,
            getScheduleRequest
        );

        return ResponseEntity.ok(ResponseWrapper.success("일정이 성공적으로 등록되었습니다."));
    }

    @Override
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long groupId,
        @PathVariable Long scheduleId,
        @RequestBody @Valid GetScheduleRequest getScheduleRequest
    ) {
        scheduleService.updateSchedule(
            userDetails.getMemberId(),
            groupId,
            scheduleId,
            getScheduleRequest
        );
        return ResponseEntity.ok(ResponseWrapper.success("일정이 성공적으로 수정되었습니다."));
    }

    @Override
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long groupId,
        @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(
            userDetails.getMemberId(),
            groupId,
            scheduleId
        );

        return ResponseEntity.ok(ResponseWrapper.success("일정이 성공적으로 삭제되었습니다."));
    }
}
