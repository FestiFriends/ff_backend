package site.festifriends.domain.performance.service;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.member.repository.BookmarkRepository;
import site.festifriends.domain.member.repository.MemberRepository;
import site.festifriends.domain.performance.dto.PerformanceFavoriteRequest;
import site.festifriends.domain.performance.dto.PerformanceFavoriteResponse;
import site.festifriends.domain.performance.dto.PerformanceResponse;
import site.festifriends.domain.performance.dto.PerformanceSearchRequest;
import site.festifriends.domain.performance.dto.PerformanceSearchResponse;
import site.festifriends.domain.performance.repository.PerformanceRepository;
import site.festifriends.entity.Bookmark;
import site.festifriends.entity.Member;
import site.festifriends.entity.Performance;
import site.festifriends.entity.enums.BookmarkType;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public PerformanceSearchResponse searchPerformances(PerformanceSearchRequest request) {
        return searchPerformances(request, null);
    }

    public PerformanceSearchResponse searchPerformances(PerformanceSearchRequest request, Long memberId) {
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

        // 각 공연별 찜 개수 조회
        Map<Long, Long> favoriteCountMap = performanceRepository.findFavoriteCountsByPerformanceIds(performanceIds);

        // 각 공연별 사용자 좋아요 여부 조회
        Map<Long, Boolean> isLikedMap = performanceRepository.findIsLikedByPerformanceIds(performanceIds, memberId);

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
            .map(performance -> convertToResponse(performance, groupCountMap, favoriteCountMap, isLikedMap))
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
        } else if ("favorite_count_desc".equals(request.getSort())) {
            performanceResponses.sort((a, b) -> {
                int countCompare = Integer.compare(b.getFavoriteCount(), a.getFavoriteCount());
                if (countCompare == 0) {
                    return a.getTitle().compareTo(b.getTitle());
                }
                return countCompare;
            });
        } else if ("favorite_count_asc".equals(request.getSort())) {
            performanceResponses.sort((a, b) -> {
                int countCompare = Integer.compare(a.getFavoriteCount(), b.getFavoriteCount());
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
     * 공연 상세 정보 조회 (하위 호환성)
     */
    public ResponseWrapper<PerformanceResponse> getPerformanceDetail(Long performanceId) {
        return getPerformanceDetail(performanceId, null);
    }

    /**
     * 공연 상세 정보 조회
     */
    public ResponseWrapper<PerformanceResponse> getPerformanceDetail(Long performanceId, Long memberId) {
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

        // 해당 공연의 찜 개수 조회
        Map<Long, Long> favoriteCountMap = performanceRepository.findFavoriteCountsByPerformanceIds(
            Collections.singletonList(performanceId));

        // 해당 공연의 사용자 좋아요 여부 조회
        Map<Long, Boolean> isLikedMap = performanceRepository.findIsLikedByPerformanceIds(
            Collections.singletonList(performanceId), memberId);

        PerformanceResponse response = convertToResponse(performance, groupCountMap, favoriteCountMap, isLikedMap);

        return ResponseWrapper.success("요청이 성공적으로 처리되었습니다.", response);
    }

    private PerformanceResponse convertToResponse(Performance performance, Map<Long, Long> groupCountMap,
        Map<Long, Long> favoriteCountMap, Map<Long, Boolean> isLikedMap) {
        List<PerformanceResponse.PerformanceImage> images = performance.getImgs().stream()
            .map(img -> PerformanceResponse.PerformanceImage.builder()
                .id(img.getId().toString())
                .src(img.getSrc())
                .alt(img.getAlt())
                .build())
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
            .images(images)
            .time(performance.getTime())
            .groupCount(groupCountMap.getOrDefault(performance.getId(), 0L).intValue())
            .favoriteCount(favoriteCountMap.getOrDefault(performance.getId(), 0L).intValue())
            .isLiked(isLikedMap.getOrDefault(performance.getId(), false))
            .build();
    }

    /**
     * 공연 찜하기/취소
     */
    @Transactional
    public PerformanceFavoriteResponse togglePerformanceFavorite(Long performanceId, Long memberId,
        PerformanceFavoriteRequest request) {
        Performance performance = performanceRepository.findById(performanceId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "공연을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        boolean currentlyLiked = bookmarkRepository.existsByMemberIdAndTypeAndTargetId(
            memberId, BookmarkType.PERFORMANCE, performanceId);

        if (Boolean.TRUE.equals(request.getIsLiked()) && !currentlyLiked) {
            Bookmark bookmark = Bookmark.builder()
                .member(member)
                .type(BookmarkType.PERFORMANCE)
                .targetId(performanceId)
                .build();
            bookmarkRepository.save(bookmark);
        } else if (Boolean.FALSE.equals(request.getIsLiked()) && currentlyLiked) {
            bookmarkRepository.deleteByMemberIdAndTypeAndTargetId(
                memberId, BookmarkType.PERFORMANCE, performanceId);
        }

        boolean finalLikedState = Boolean.TRUE.equals(request.getIsLiked());
        return PerformanceFavoriteResponse.of(performanceId, finalLikedState);
    }

    /**
     * 찜한 수가 많은 공연 TOP5 조회 (아직 시작하지 않은 공연만)
     */
    public ResponseWrapper<List<PerformanceResponse>> getTopFavoriteUpcomingPerformances(Long memberId) {
        List<Performance> topPerformances = performanceRepository.findTopFavoriteUpcomingPerformances(5);

        if (topPerformances.isEmpty()) {
            return ResponseWrapper.success("요청이 성공적으로 처리되었습니다.", List.of());
        }

        topPerformances.forEach(performance -> {
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

        List<Long> performanceIds = topPerformances.stream()
            .map(Performance::getId)
            .collect(Collectors.toList());

        Map<Long, Long> groupCountMap = performanceRepository.findGroupCountsByPerformanceIds(performanceIds);

        Map<Long, Long> favoriteCountMap = performanceRepository.findFavoriteCountsByPerformanceIds(performanceIds);

        Map<Long, Boolean> isLikedMap = performanceRepository.findIsLikedByPerformanceIds(performanceIds, memberId);

        List<PerformanceResponse> performanceResponses = topPerformances.stream()
            .map(performance -> convertToResponse(performance, groupCountMap, favoriteCountMap, isLikedMap))
            .toList();

        return ResponseWrapper.success("요청이 성공적으로 처리되었습니다.", performanceResponses);
    }

    /**
     * 개설된 모임이 가장 많은 공연 TOP5 조회 (아직 시작하지 않은 공연만)
     */
    public ResponseWrapper<List<PerformanceResponse>> getTopGroupsUpcomingPerformances(Long memberId) {
        List<Performance> topPerformances = performanceRepository.findTopGroupsUpcomingPerformances(5);

        if (topPerformances.isEmpty()) {
            return ResponseWrapper.success("요청이 성공적으로 처리되었습니다.", List.of());
        }

        topPerformances.forEach(performance -> {
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

        List<Long> performanceIds = topPerformances.stream()
            .map(Performance::getId)
            .collect(Collectors.toList());

        Map<Long, Long> groupCountMap = performanceRepository.findGroupCountsByPerformanceIds(performanceIds);

        Map<Long, Long> favoriteCountMap = performanceRepository.findFavoriteCountsByPerformanceIds(performanceIds);

        Map<Long, Boolean> isLikedMap = performanceRepository.findIsLikedByPerformanceIds(performanceIds, memberId);

        List<PerformanceResponse> performanceResponses = topPerformances.stream()
            .map(performance -> convertToResponse(performance, groupCountMap, favoriteCountMap, isLikedMap))
            .toList();

        return ResponseWrapper.success("요청이 성공적으로 처리되었습니다.", performanceResponses);
    }
} 
