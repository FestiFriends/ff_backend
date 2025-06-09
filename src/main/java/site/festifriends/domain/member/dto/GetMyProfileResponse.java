package site.festifriends.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.domain.group.dto.GroupSummaryDto;
import site.festifriends.domain.image.dto.ImageDto;
import site.festifriends.domain.review.dto.ReviewSummaryDto;

@Getter
@Builder
public class GetMyProfileResponse {

    private String id;
    private String name;
    private Integer age;
    private String gender;
    private ImageDto profileImage;
    private String description;
    private List<String> hashtags;
    private String sns;
    private Double rating;
    @JsonIgnore
    private Boolean isLiked;
    @JsonProperty("isReported")
    private Boolean isReported;
    @JsonProperty("isMine")
    private Boolean isMine;
    private GroupSummaryDto groupSummary;
    private ReviewSummaryDto reviewSummary;
    private Integer reviewCount;
    private List<String> reviewList;

}
