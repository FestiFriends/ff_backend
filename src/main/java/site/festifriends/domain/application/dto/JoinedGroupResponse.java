package site.festifriends.domain.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;

@Getter
@Builder
public class JoinedGroupResponse {

    private String id;
    private Performance performance;
    private String title;
    private GroupCategory category;
    private Gender gender;
    private Integer startAge;
    private Integer endAge;
    private String location;
    private String startDate;
    private String endDate;
    private Integer memberCount;
    private Integer maxMembers;
    private String description;
    private List<String> hashtag;
    private Host host;

    @JsonProperty("isHost")
    private Boolean isHost;

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
        private String profileImage;
    }
} 
