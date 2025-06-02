package site.festifriends.domain.member.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikedPerformanceResponse {

    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private List<String> cast;
    private List<String> crew;
    private String runtime;
    private String age;
    private List<String> productionCompany;
    private List<String> agency;
    private List<String> host;
    private List<String> organizer;
    private List<String> price;
    private String poster;
    private String state;
    private String visit;
    private List<LikedPerformanceImageDto> images;
    private List<String> time;
    private Integer groupCount;
}
