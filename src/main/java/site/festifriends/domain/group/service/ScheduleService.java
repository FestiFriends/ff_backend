package site.festifriends.domain.group.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.domain.group.dto.GetScheduleRequest;
import site.festifriends.domain.group.dto.GetScheduleResponse;
import site.festifriends.domain.group.dto.ScheduleDto;
import site.festifriends.domain.group.repository.GroupRepository;
import site.festifriends.domain.group.repository.ScheduleRepository;
import site.festifriends.domain.member.repository.MemberGroupRepository;
import site.festifriends.entity.Group;
import site.festifriends.entity.MemberGroup;
import site.festifriends.entity.Schedule;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final GroupRepository groupRepository;


    public GetScheduleResponse getGroupSchedules(Long memberId, Long groupId, LocalDateTime startDateTime,
        LocalDateTime endDateTime) {
        groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        validateMemberGroup(memberId, groupId, "일정 조회 권한이 없습니다.");

        List<ScheduleDto> schedules = scheduleRepository.getGroupSchedules(
            memberId, groupId, startDateTime, endDateTime
        );

        return GetScheduleResponse.builder()
            .scheduleCount(schedules.size())
            .schedules(schedules)
            .build();
    }

    public void createSchedule(Long memberId, Long groupId, GetScheduleRequest request) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        MemberGroup memberGroup = validateMemberGroup(memberId, groupId, "일정 등록 권한이 없습니다.");

        Schedule newSchedule = Schedule.builder()
            .description(request.getDescription())
            .location(request.getLocation())
            .startDate(request.getStartAt().toLocalDateTime())
            .endDate(request.getEndAt().toLocalDateTime())
            .eventColor(request.getEventColor())
            .group(group)
            .member(memberGroup.getMember())
            .build();

        scheduleRepository.save(newSchedule);
    }


    private MemberGroup validateMemberGroup(Long memberId, Long groupId, String message) {
        return memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN, message));
    }

    public void updateSchedule(Long memberId, Long groupId, Long scheduleId,
        GetScheduleRequest request) {

        groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        validateMemberGroup(memberId, groupId, "일정 수정 권한이 없습니다.");

        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다."));

        if (!schedule.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "일정 수정 권한이 없습니다.");
        }

        schedule.updateSchedule(
            request.getDescription(),
            request.getLocation(),
            request.getStartAt().toLocalDateTime(),
            request.getEndAt().toLocalDateTime(),
            request.getEventColor()
        );

        scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long memberId, Long groupId, Long scheduleId) {
        groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 모임을 찾을 수 없습니다."));

        validateMemberGroup(memberId, groupId, "일정 삭제 권한이 없습니다.");

        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 일정을 찾을 수 없습니다."));

        if (!schedule.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "일정 삭제 권한이 없습니다.");
        }

        scheduleRepository.delete(schedule);
    }
}
