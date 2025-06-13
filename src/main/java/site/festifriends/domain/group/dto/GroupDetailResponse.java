package site.festifriends.domain.group.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupDetailResponse {

    private String id;
    private Performance performance;
    private String title;
    private String category;     // GroupCategory description
    private String gender;       // Gender description
    private Integer startAge;
    private Integer endAge;
    private String location;
    private String startDate;    // ISO 8601 format
    private String endDate;      // ISO 8601 format
    private Integer memberCount; // 현재 가입된 멤버 수
    private Integer maxMembers;  // 최대 인원
    private String description;  // Group의 introduction
    private List<String> hashtag;
    private Host host;
    private Boolean isMember;
    private Long chatRoomId;

    @Getter
    @Builder
    public static class Performance {

        private String id;
        private String title;
        private String poster;
    }

    @Getter
    @Builder
    public static class Host {

        private String id;
        private String name;
        private Double rating;
    }
}
