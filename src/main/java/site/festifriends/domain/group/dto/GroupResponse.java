package site.festifriends.domain.group.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;

import java.util.List;

@Getter
@Builder
public class GroupResponse {
    private String id;
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
    private List<String> hashtag;

    @JsonProperty("isFavorite")
    private boolean isFavorite;

    private Host host;

    @Getter
    @Builder
    public static class Host {
        private String hostId;
        private String name;
        private Double rating;
    }
}
