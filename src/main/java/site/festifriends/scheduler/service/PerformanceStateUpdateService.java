package site.festifriends.scheduler.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.domain.performance.repository.PerformanceRepository;
import site.festifriends.entity.Performance;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceStateUpdateService {

    private final PerformanceRepository performanceRepository;

    /**
     * 모든 공연의 상태를 현재 시간 기준으로 업데이트
     */
    public int updateAllPerformanceStates() {
        log.debug("공연 상태 업데이트 서비스 시작");
        List<Performance> allPerformances = performanceRepository.findAll();
        int updatedCount = 0;
        for (Performance performance : allPerformances) {
            var previousState = performance.getState();
            performance.updateState();

            if (!previousState.equals(performance.getState())) {
                log.info("공연 ID: {}, 제목: '{}' 상태 변경: {} -> {}",
                    performance.getId(),
                    performance.getTitle(),
                    previousState.getDescription(),
                    performance.getState().getDescription());
                updatedCount++;
            }
        }
        log.debug("공연 상태 업데이트 서비스 완료. 총 {}개 공연 중 {}개 상태 변경됨",
            allPerformances.size(), updatedCount);
        return updatedCount;
    }
}
