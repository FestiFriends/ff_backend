package site.festifriends.domain.group.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "모임원 목록 응답")
public class GroupMembersResponse {

    @Schema(description = "모임 ID", example = "g001")
    private String groupId;

    @Schema(description = "공연 ID", example = "p001")
    private String performanceId;

    @Schema(description = "모임원 수", example = "42")
    private Integer memberCount;

    @Schema(description = "내가 모임장인지 여부", example = "true")
    @JsonProperty("isHost")
    private Boolean isHost;

    @Schema(description = "모임원 목록")
    private List<GroupMemberResponse> members;

    @Schema(description = "다음 페이지를 위한 커서 ID", example = "1234")
    private Long cursorId;

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private Boolean hasNext;
}
