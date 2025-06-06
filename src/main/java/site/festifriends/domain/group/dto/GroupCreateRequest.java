package site.festifriends.domain.group.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.festifriends.entity.enums.Gender;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class GroupCreateRequest {

    @NotBlank(message = "공연 ID는 필수입니다.")
    private String performanceId;

    @NotBlank(message = "모임 제목은 필수입니다.")
    @Size(max = 100, message = "모임 제목은 100자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;

    @NotNull(message = "성별 제한은 필수입니다.")
    private Gender gender;

    @NotNull(message = "시작 연령은 필수입니다.")
    @Min(value = 1, message = "시작 연령은 1 이상이어야 합니다.")
    @Max(value = 100, message = "시작 연령은 100 이하여야 합니다.")
    private Integer startAge;

    @NotNull(message = "종료 연령은 필수입니다.")
    @Min(value = 1, message = "종료 연령은 1 이상이어야 합니다.")
    @Max(value = 100, message = "종료 연령은 100 이하여야 합니다.")
    private Integer endAge;

    @NotBlank(message = "모임 장소는 필수입니다.")
    @Size(max = 200, message = "모임 장소는 200자 이하로 입력해주세요.")
    private String location;

    @NotNull(message = "시작 시간은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime startDate;

    @NotNull(message = "종료 시간은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime endDate;

    @NotNull(message = "최대 인원은 필수입니다.")
    @Min(value = 2, message = "최대 인원은 2명 이상이어야 합니다.")
    @Max(value = 50, message = "최대 인원은 50명 이하여야 합니다.")
    private Integer maxMembers;

    @NotBlank(message = "모임 설명은 필수입니다.")
    @Size(max = 500, message = "모임 설명은 500자 이하로 입력해주세요.")
    private String description;

    @Size(max = 10, message = "해시태그는 최대 10개까지 가능합니다.")
    private List<String> hashtag;

    public GroupCreateRequest(String performanceId, String title, String category, Gender gender,
        Integer startAge, Integer endAge, String location,
        LocalDateTime startDate, LocalDateTime endDate, Integer maxMembers,
        String description, List<String> hashtag) {
        this.performanceId = performanceId;
        this.title = title;
        this.category = category;
        this.gender = gender;
        this.startAge = startAge;
        this.endAge = endAge;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxMembers = maxMembers;
        this.description = description;
        this.hashtag = hashtag;
    }
}
