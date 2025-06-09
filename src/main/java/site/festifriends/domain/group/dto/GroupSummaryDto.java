package site.festifriends.domain.group.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupSummaryDto {

    private Integer joinedCount;
    private Integer totalJoinedCount;
    private Integer createdCount;
}