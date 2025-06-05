package site.festifriends.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.festifriends.entity.enums.Role;

@Getter
@NoArgsConstructor
@Schema(description = "모임원 권한 수정 요청")
public class UpdateMemberRoleRequest {

    @NotNull(message = "권한은 필수입니다.")
    @Schema(description = "변경할 권한", example = "HOST", allowableValues = {"HOST", "MEMBER"})
    private Role role;
}
