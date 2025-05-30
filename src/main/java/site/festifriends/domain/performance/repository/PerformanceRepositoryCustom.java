package site.festifriends.domain.performance.repository;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.festifriends.domain.performance.dto.PerformanceSearchRequest;
import site.festifriends.entity.Performance;

public interface PerformanceRepositoryCustom {

    Page<Performance> searchPerformancesWithPaging(PerformanceSearchRequest request, Pageable pageable);

    Map<Long, Long> findGroupCountsByPerformanceIds(List<Long> performanceIds);

    Map<Long, Integer> getGroupCountsByPerformanceIds(List<Long> performanceIds);
}