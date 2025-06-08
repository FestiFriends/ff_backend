package site.festifriends.domain.member.repository;

import java.util.List;
import java.util.Map;

public interface BookmarkRepositoryCustom {

    Map<Long, Integer> getCountByPerformanceIds(List<Long> performanceIds);
}
