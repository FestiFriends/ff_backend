package site.festifriends.domain.performance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PerformanceResponse {
    
    private String id;               // 공연 ID
    private String title;            // 공연명
    private String startDate;        // 공연 시작 날짜 (ISO 8601)
    private String endDate;          // 공연 종료 날짜 (ISO 8601)
    private String location;         // 공연 장소
    private List<String> cast;       // 공연 출연진
    private List<String> crew;       // 공연 제작진
    private String runtime;          // 공연 런타임
    private String age;              // 관람 연령
    private List<String> productionCompany; // 제작사
    private List<String> agency;     // 기획사
    private List<String> host;       // 주최
    private List<String> organizer;  // 주관
    private List<String> price;      // 티켓 가격
    private String poster;           // 포스터 이미지 URL
    private String state;            // 공연 상태
    private String visit;            // 내한 여부
    private List<PerformanceImage> images; // 소개 이미지 목록
    private List<String> time;       // 공연 시간 (ISO 8601) 배열
    private Integer groupCount;      // 모임 개수 (모임개수 정렬용)
    private Integer favoriteCount;   // 공연 찜 수

    @JsonProperty("isLiked")
    private Boolean isLiked;         // 현재 로그인한 사용자의 해당 공연 좋아요 여부
    
    @Getter
    @Builder
    public static class PerformanceImage {
        private String id;           // 이미지 ID
        private String src;          // 소개 이미지 URL
        private String alt;          // 소개 이미지 설명
    }
} 