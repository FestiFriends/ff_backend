package site.festifriends.domain.group.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.group.dto.GroupDetailResponse;
import site.festifriends.domain.group.dto.PerformanceGroupsData;
import site.festifriends.domain.group.service.GroupService;

@RestController
@RequiredArgsConstructor
public class GroupController implements GroupApi {

    private final GroupService groupService;

    @Override
    public ResponseEntity<ResponseWrapper<PerformanceGroupsData>> getGroupsByPerformanceId
        (
            @AuthenticationPrincipal UserDetailsImpl user,
            Long performanceId,
            Integer page,
            Integer size
        ) {

        Long memberId = user != null ? user.getMemberId() : null;
        PerformanceGroupsData data = groupService.getGroupsByPerformanceId(performanceId, page, size, memberId);

        return ResponseEntity.ok(ResponseWrapper.success("요청이 성공적으로 처리되었습니다.", data));
    }

    @Override
    @GetMapping("/api/v1/groups/{groupId}")
    public ResponseEntity<ResponseWrapper<GroupDetailResponse>> getGroupDetail(@PathVariable Long groupId) {
        GroupDetailResponse response = groupService.getGroupDetail(groupId);
        return ResponseEntity.ok(ResponseWrapper.success("모임 기본 정보 조회 성공.", response));
    }
}

