package site.festifriends.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.festifriends.scheduler.service.PerformanceStateUpdateService;

/**
 * 공연 상태 자동 업데이트 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceStateScheduler {

    private final PerformanceStateUpdateService performanceStateUpdateService;

    /**
     * 매일 자정 1분에 공연 상태 업데이트 실행
     * cron = "초 분 시 일 월 요일"
     * 0 1 0 * * * = 매일 00:01:00에 실행
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void updatePerformanceStates() {
        log.info("공연 상태 자동 업데이트 작업을 시작합니다.");
        try {
            int updatedCount = performanceStateUpdateService.updateAllPerformanceStates();
            log.info("공연 상태 자동 업데이트 작업이 완료되었습니다. 업데이트된 공연 수: {}", updatedCount);
        } catch (Exception e) {
            log.error("공연 상태 자동 업데이트 작업 중 오류가 발생했습니다.", e);
        }
    }
}
