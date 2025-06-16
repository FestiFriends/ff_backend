package site.festifriends.domain.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.group.dto.GetScheduleRequest;

@Tag(name = "Group_Schedule", description = "모임 일정 관련 API")
public interface ScheduleApi {

    @Operation(
        summary = "모임 내 일정 조회",
        description = "모임 내의 일정을 조회합니다. 커서 기반으로 페이징 처리됩니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "일정 목록 조회에 성공했습니다."),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "일정 조회 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 일정 조회에 실패했습니다.")
        }
    )
    ResponseEntity<?> getSchedules(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long groupId,
        @RequestParam OffsetDateTime startDate,
        @RequestParam OffsetDateTime endDate
    );

    @Operation(
        summary = "모임 내 일정 등록",
        description = "모임 내에 새로운 일정을 생성합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "일정이 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "일정 등록 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 일정 등록에 실패했습니다.")
        }
    )
    ResponseEntity<?> createSchedule(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long groupId,
        @RequestBody @Valid GetScheduleRequest getScheduleRequest

    );

    @Operation(
        summary = "모임 내 일정 수정",
        description = "모임 내의 일정을 수정합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "일정이 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "일정 수정 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 일정 등록에 실패했습니다.")
        }
    )
    ResponseEntity<?> updateSchedule(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long groupId,
        @PathVariable Long scheduleId,
        @RequestBody @Valid GetScheduleRequest getScheduleRequest
    );

    @Operation(
        summary = "모임 내 일정 삭제",
        description = "모임 내의 일정을 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "일정 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "일정 삭제 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 일정 등록에 실패했습니다.")
        }
    )
    ResponseEntity<?> deleteSchedule(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long groupId,
        @PathVariable Long scheduleId
    );
}
