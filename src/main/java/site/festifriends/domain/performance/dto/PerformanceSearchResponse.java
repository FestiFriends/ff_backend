package site.festifriends.domain.performance.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PerformanceSearchResponse {
    
    private Integer code;
    private String message;
    private List<PerformanceResponse> data;
    
    // 페이징 정보
    private Integer page;          // 페이지 번호
    private Integer size;          // 한 페이지당 항목 수
    private Long totalElements;    // 전체 공연 개수
    private Integer totalPages;    // 전체 페이지 수
    private Boolean first;         // 현재 페이지가 첫 번째 페이지인지 여부
    private Boolean last;          // 현재 페이지가 마지막 페이지인지 여부
} 