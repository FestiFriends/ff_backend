package site.festifriends.domain.performance.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.festifriends.domain.performance.dto.PerformanceSearchRequest;
import site.festifriends.entity.Performance;

import java.util.List;
import java.util.Map;

public interface PerformanceRepositoryCustom {
    
    Page<Performance> searchPerformancesWithPaging(PerformanceSearchRequest request, Pageable pageable);
    
    Map<Long, Long> findGroupCountsByPerformanceIds(List<Long> performanceIds);
    
    Map<Long, Long> findFavoriteCountsByPerformanceIds(List<Long> performanceIds);
} 