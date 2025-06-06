package site.festifriends.domain.performance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.performance.dto.PerformanceFavoriteRequest;
import site.festifriends.domain.performance.dto.PerformanceFavoriteResponse;
import site.festifriends.domain.performance.dto.PerformanceResponse;
import site.festifriends.domain.performance.dto.PerformanceSearchRequest;
import site.festifriends.domain.performance.dto.PerformanceSearchResponse;

@Tag(name = "Performance", description = "공연 관련 API")
public interface PerformanceApi {

    @Operation(
        summary = "공연 검색",
        description = """
            공연을 검색합니다.
            
            **검색 필터:**
            - title: 공연명 검색
            - location: 지역 검색
            - visit: 국내/내한 여부 (국내, 내한)
            - startDate: 검색 시작 날짜 (yyyy-MM-dd)
            - endDate: 검색 종료 날짜 (yyyy-MM-dd)
            
            **정렬 옵션:**
            - title_asc: 이름 가나다순 (기본값)
            - title_desc: 이름 역순
            - date_asc: 일자 빠른순
            - date_desc: 일자 먼순
            - group_count_desc: 모임개수 많은 순
            - group_count_asc: 모임개수 적은 순
            """
    )
    @GetMapping
    ResponseEntity<PerformanceSearchResponse> searchPerformances(
        @Parameter(description = "검색 조건") PerformanceSearchRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user
    );

    @Operation(
        summary = "공연 상세 조회",
        description = "공연 ID로 공연 상세 정보를 조회합니다."
    )
    @GetMapping("/{performanceId}")
    ResponseEntity<ResponseWrapper<PerformanceResponse>> getPerformanceDetail(
        @Parameter(description = "공연 ID") @PathVariable Long performanceId,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user
    );

    @Operation(
        summary = "공연 찜하기/취소",
        description = """
            공연을 찜하거나 찜을 취소합니다.
            
            **요청:**
            - isLiked: true(찜하기), false(찜 취소)
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "공연을 찜했습니다. / 공연을 찜 취소했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "404", description = "공연을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    @PatchMapping("/{performanceId}/favorites")
    ResponseEntity<ResponseWrapper<PerformanceFavoriteResponse>> togglePerformanceFavorite(
        @Parameter(description = "공연 ID") @PathVariable Long performanceId,
        @Parameter(description = "찜하기 요청") @RequestBody PerformanceFavoriteRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user
    );
}
