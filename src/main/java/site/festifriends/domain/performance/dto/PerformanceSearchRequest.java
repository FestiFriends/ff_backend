package site.festifriends.domain.performance.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class PerformanceSearchRequest {
    
    // 검색 필터
    private String title;          // 공연명 검색
    private String location;       // 지역 검색  
    private String visit;          // 국내/내한 (국내, 내한)
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;   // 검색 시작 날짜
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;     // 검색 종료 날짜
    
    // 정렬
    private String sort = "title_asc";  // 기본값: 이름 가나다순
    // 가능한 값: title_asc, title_desc, date_asc, date_desc, group_count_desc, group_count_asc
    
    // 페이징
    private Integer page = 1;      // 페이지 번호 (기본값: 1)
    private Integer size = 10;     // 한 페이지당 항목 수 (기본값: 10)
} 