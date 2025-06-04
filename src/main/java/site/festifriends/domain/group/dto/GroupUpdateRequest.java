package site.festifriends.domain.group.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.festifriends.entity.enums.Gender;
import site.festifriends.entity.enums.GroupCategory;

@Getter
@NoArgsConstructor
@Schema(description = "모임 정보 수정 요청")
public class GroupUpdateRequest {

    @NotBlank(message = "모임 제목은 필수입니다")
    @Size(max = 100, message = "모임 제목은 100자를 초과할 수 없습니다")
    @Schema(description = "모임 제목", example = "펜타포트 페스티벌 같이 가실 분")
    private String title;

    @NotNull(message = "모임 카테고리는 필수입니다")
    @Schema(description = "모임 카테고리", example = "같이 동행")
    private String category;

    @NotNull(message = "성별 제한은 필수입니다")
    @Schema(description = "성별 제한", example = "all", allowableValues = {"male", "female", "all"})
    private String gender;

    @NotNull(message = "시작 연령은 필수입니다")
    @Min(value = 18, message = "시작 연령은 18세 이상이어야 합니다")
    @Max(value = 100, message = "시작 연령은 100세 이하여야 합니다")
    @Schema(description = "시작 연령", example = "20")
    private Integer startAge;

    @NotNull(message = "종료 연령은 필수입니다")
    @Min(value = 18, message = "종료 연령은 18세 이상이어야 합니다")
    @Max(value = 100, message = "종료 연령은 100세 이하여야 합니다")
    @Schema(description = "종료 연령", example = "35")
    private Integer endAge;

    @NotBlank(message = "모임 장소는 필수입니다")
    @Size(max = 100, message = "모임 장소는 100자를 초과할 수 없습니다")
    @Schema(description = "모임 장소", example = "인천")
    private String location;

    @NotNull(message = "시작 날짜는 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Schema(description = "시작 날짜", example = "2025-08-08T00:00:00Z")
    private LocalDateTime startDate;

    @NotNull(message = "종료 날짜는 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Schema(description = "종료 날짜", example = "2025-08-11T00:00:00Z")
    private LocalDateTime endDate;

    @NotNull(message = "최대 인원수는 필수입니다")
    @Min(value = 2, message = "최대 인원수는 2명 이상이어야 합니다")
    @Max(value = 50, message = "최대 인원수는 50명 이하여야 합니다")
    @Schema(description = "최대 인원수", example = "10")
    private Integer maxMembers;

    @NotBlank(message = "모임 설명은 필수입니다")
    @Size(max = 500, message = "모임 설명은 500자를 초과할 수 없습니다")
    @Schema(description = "모임 설명", example = "인천에서 열리는 펜타포트 페스티벌, 함께 즐길 멤버 구합니다!")
    private String description;

    @Schema(description = "해시태그 목록", example = "[\"펜타포트\", \"락페스티벌\", \"동행\"]")
    private List<String> hashtag;

    @JsonIgnore
    public Gender getGenderEnum() {
        return switch (gender.toLowerCase()) {
            case "male" -> Gender.MALE;
            case "female" -> Gender.FEMALE;
            case "all" -> Gender.ALL;
            default -> throw new IllegalArgumentException("유효하지 않은 성별 값입니다: " + gender);
        };
    }

    @JsonIgnore
    public GroupCategory getCategoryEnum() {
        return switch (category) {
            case "같이 동행" -> GroupCategory.COMPANION;
            case "같이 탑승" -> GroupCategory.RIDE_SHARE;
            case "같이 숙박" -> GroupCategory.ROOM_SHARE;
            default -> throw new IllegalArgumentException("유효하지 않은 카테고리 값입니다: " + category);
        };
    }
}
