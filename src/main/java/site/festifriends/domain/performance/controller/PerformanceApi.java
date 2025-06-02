package site.festifriends.domain.performance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import site.festifriends.common.response.ResponseWrapper;
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
            @Parameter(description = "검색 조건") PerformanceSearchRequest request
    );

    @Operation(
            summary = "공연 상세 조회",
            description = "공연 ID로 공연 상세 정보를 조회합니다."
    )
    @GetMapping("/{performanceId}")
    ResponseEntity<ResponseWrapper<PerformanceResponse>> getPerformanceDetail(
            @Parameter(description = "공연 ID") @PathVariable Long performanceId
    );
}
