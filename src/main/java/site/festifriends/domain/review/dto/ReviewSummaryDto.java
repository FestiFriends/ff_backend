package site.festifriends.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewSummaryDto {

    private Integer PUNCTUAL;
    private Integer POLITE;
    private Integer COMFORTABLE;
    private Integer COMMUNICATIVE;
    private Integer CLEAN;
    private Integer RESPONSIVE;
    private Integer RECOMMEND;
}