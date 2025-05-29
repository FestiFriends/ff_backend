package site.festifriends.domain.performance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.domain.performance.dto.PerformanceResponse;
import site.festifriends.domain.performance.dto.PerformanceSearchRequest;
import site.festifriends.domain.performance.dto.PerformanceSearchResponse;
import site.festifriends.domain.performance.repository.PerformanceRepository;
import site.festifriends.entity.Performance;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public PerformanceSearchResponse searchPerformances(PerformanceSearchRequest request) {
        // 페이징 객체 생성
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        // 공연 검색
        Page<Performance> performancePage = performanceRepository.searchPerformancesWithPaging(request, pageable);

        List<Performance> performances = performancePage.getContent();

        // 공연 ID 목록 추출
        List<Long> performanceIds = performances.stream()
                .map(Performance::getId)
                .collect(Collectors.toList());

        // 각 공연별 모임 개수 조회
        Map<Long, Long> groupCountMap = performanceRepository.findGroupCountsByPerformanceIds(performanceIds);

        performances.forEach(performance -> {
            performance.getCast().size();
            performance.getCrew().size();
            performance.getProductionCompany().size();
            performance.getAgency().size();
            performance.getHost().size();
            performance.getOrganizer().size();
            performance.getPrice().size();
            performance.getTime().size();
            performance.getImgs().size();
        });

        List<PerformanceResponse> performanceResponses = performances.stream()
                .map(performance -> convertToResponse(performance, groupCountMap))
                .collect(Collectors.toList());

        if ("group_count_desc".equals(request.getSort())) {
            performanceResponses.sort((a, b) -> {
                int countCompare = Integer.compare(b.getGroupCount(), a.getGroupCount());
                if (countCompare == 0) {
                    return a.getTitle().compareTo(b.getTitle());
                }
                return countCompare;
            });
        } else if ("group_count_asc".equals(request.getSort())) {
            performanceResponses.sort((a, b) -> {
                int countCompare = Integer.compare(a.getGroupCount(), b.getGroupCount());
                if (countCompare == 0) {
                    return a.getTitle().compareTo(b.getTitle());
                }
                return countCompare;
            });
        }

        return PerformanceSearchResponse.builder()
                .code(200)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(performanceResponses)
                .page(request.getPage())
                .size(request.getSize())
                .totalElements(performancePage.getTotalElements())
                .totalPages(performancePage.getTotalPages())
                .first(performancePage.isFirst())
                .last(performancePage.isLast())
                .build();
    }

    /**
     * 공연 상세 정보 조회
     */
    public PerformanceResponse getPerformanceDetail(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "공연을 찾을 수 없습니다."));

        performance.getCast().size();
        performance.getCrew().size();
        performance.getProductionCompany().size();
        performance.getAgency().size();
        performance.getHost().size();
        performance.getOrganizer().size();
        performance.getPrice().size();
        performance.getTime().size();
        performance.getImgs().size();

        // 해당 공연의 모임 개수 조회
        Map<Long, Long> groupCountMap = performanceRepository.findGroupCountsByPerformanceIds(
                Collections.singletonList(performanceId));

        return convertToResponse(performance, groupCountMap);
    }

    private PerformanceResponse convertToResponse(Performance performance, Map<Long, Long> groupCountMap) {
        List<PerformanceResponse.PerformanceImage> images = performance.getImgs().stream()
                .map(img -> PerformanceResponse.PerformanceImage.builder()
                        .id(img.getId().toString())
                        .src(img.getSrc())
                        .alt(img.getAlt())
                        .build())
                .collect(Collectors.toList());

        List<String> timeStrings = performance.getTime().stream()
                .map(time -> time.format(ISO_FORMATTER))
                .collect(Collectors.toList());

        return PerformanceResponse.builder()
                .id(performance.getId().toString())
                .title(performance.getTitle())
                .startDate(performance.getStartDate().format(ISO_FORMATTER))
                .endDate(performance.getEndDate().format(ISO_FORMATTER))
                .location(performance.getLocation())
                .cast(performance.getCast())
                .crew(performance.getCrew())
                .runtime(performance.getRuntime())
                .age(performance.getAge())
                .productionCompany(performance.getProductionCompany())
                .agency(performance.getAgency())
                .host(performance.getHost())
                .organizer(performance.getOrganizer())
                .price(performance.getPrice())
                .poster(performance.getPoster())
                .state(performance.getState().getDescription())
                .visit(performance.getVisit())
                .imgs(images)
                .time(timeStrings)
                .groupCount(groupCountMap.getOrDefault(performance.getId(), 0L).intValue())
                .build();
    }
} 
