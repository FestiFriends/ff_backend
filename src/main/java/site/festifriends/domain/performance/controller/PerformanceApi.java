package site.festifriends.domain.performance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
import site.festifriends.domain.review.dto.RecentReviewResponse;

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
            - isExpired: 종료된 공연 포함 여부 (true: 모든 상태 포함, false: 종료된 공연 제외, 기본값: true)
            - startDate: 검색 시작 날짜 (yyyy-MM-dd)
            - endDate: 검색 종료 날짜 (yyyy-MM-dd)
            
            **정렬 옵션:**
            - title_asc: 이름 가나다순 (기본값)
            - title_desc: 이름 역순
            - date_asc: 일자 빠른순
            - date_desc: 일자 먼순
            - group_count_desc: 모임개수 많은 순
            - group_count_asc: 모임개수 적은 순
            - favorite_count_desc: 찜개수 많은 순
            - favorite_count_asc: 찜개수 적은 순
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

    @Operation(
        summary = "찜한 수가 많은 공연 TOP5 조회",
        description = """
            찜한 수가 많은 공연 TOP5를 조회합니다.
            
            **조건:**
            - 아직 시작하지 않은 공연만 대상
            - 찜 수가 많은 순으로 정렬
            - 찜 수가 같은 경우 제목 가나다순으로 정렬
            - 최대 5개 공연 반환
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "요청이 성공적으로 처리되었습니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    @GetMapping("/top-favorites")
    ResponseEntity<ResponseWrapper<List<PerformanceResponse>>> getTopFavoriteUpcomingPerformances(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user
    );

    @Operation(
        summary = "개설된 모임이 가장 많은 공연 TOP5 조회",
        description = """
            개설된 모임이 가장 많은 공연 TOP5를 조회합니다.
            
            **조건:**
            - 아직 시작하지 않은 공연만 대상
            - 모임 수가 많은 순으로 정렬
            - 모임 수가 같은 경우 빠른 날짜순으로 정렬
            - 날짜도 같은 경우 제목 가나다순으로 정렬
            - 최대 5개 공연 반환
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "요청이 성공적으로 처리되었습니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    @GetMapping("/top-groups")
    ResponseEntity<ResponseWrapper<List<PerformanceResponse>>> getTopGroupsUpcomingPerformances(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user
    );

    @Operation(
        summary = "최근 올라온 리뷰 TOP 5 조회",
        description = """
            최근 작성된 리뷰를 기준으로 상위 5개 리뷰를 조회합니다.
            
            **조건:**
            - 작성일시(createdAt) 기준 최신순 정렬
            - 같은 모임의 리뷰여도 상관없이 개별 리뷰 5개 반환
            
            **응답 정보:**
            - 각 리뷰의 모임 정보 (제목, 카테고리, 날짜)
            - 관련 공연 정보 (제목, 포스터)
            - 리뷰 상세 정보 (평점, 내용, 태그, 작성일시)
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "최근 올라온 리뷰 top5 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    @GetMapping("/recent-reviews")
    ResponseEntity<ResponseWrapper<List<RecentReviewResponse>>> getRecentReviews();
}
