package site.festifriends.domain.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "모임 참가 신청 요청")
public class ApplicationRequest {

    @NotBlank(message = "신청 내용은 필수입니다.")
    @Size(max = 150, message = "신청 내용은 150자 이하로 입력해주세요.")
    @Schema(description = "신청 내용", example = "안녕하세요! 20대 여성이고, 공연 너무 좋아해서 신청드려요 :)")
    private String description;
}
