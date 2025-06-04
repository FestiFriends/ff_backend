package site.festifriends.domain.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.group.dto.GroupDetailResponse;
import site.festifriends.domain.group.dto.PerformanceGroupsData;

@Tag(name = "Group", description = "모임 관련 API")
public interface GroupApi {

    @Operation(
        summary = "공연 모임 목록 조회",
        description = """
            해당 공연의 모임 목록을 조회합니다.
            
            로그인한 사용자인 경우 모임 찜 여부도 조회됩니다.
            로그인하지 않은 사용자인 경우 찜 여부는 모두 false로 반환됩니다.
            """
    )
    @GetMapping("/api/v1/performances/{performanceId}/groups")
    ResponseEntity<ResponseWrapper<PerformanceGroupsData>> getGroupsByPerformanceId(
        @AuthenticationPrincipal UserDetailsImpl user,
        @Parameter(description = "공연 ID") @PathVariable Long performanceId,
        @Parameter(description = "페이지 번호 (기본값: 1)") @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "한 페이지당 항목 수 (기본값: 20)") @RequestParam(defaultValue = "20") Integer size
    );

    @Operation(
        summary = "모임 기본 정보 조회",
        description = """
            모임 ID로 모임의 기본 정보를 조회합니다.
            
            - 모임의 상세 정보 (제목, 카테고리, 인원, 날짜 등)
            - 관련 공연 정보 (ID, 제목, 포스터)
            - 방장 정보 (ID, 이름, 평점)
            """
    )
    @GetMapping("/api/v1/groups/{groupId}")
    ResponseEntity<ResponseWrapper<GroupDetailResponse>> getGroupDetail(
        @Parameter(description = "모임 ID") @PathVariable Long groupId
    );
}

