package site.festifriends.domain.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.festifriends.entity.enums.ApplicationStatus;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "모임 신청서 상태 변경 요청")
public class ApplicationStatusRequest {
    
    @NotNull(message = "상태는 필수입니다.")
    @Schema(
        description = "신청서 상태 (모임 방장이 수락하면 ACCEPTED, 거절하면 REJECTED)", 
        example = "ACCEPTED",
        allowableValues = {"ACCEPTED", "REJECTED"}
    )
    private ApplicationStatus status;
}
