package site.festifriends.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.enums.Role;

@Getter
@Builder
@Schema(description = "모임원 정보")
public class GroupMemberResponse {

    @Schema(description = "회원 ID", example = "m001")
    private String memberId;

    @Schema(description = "회원 이름", example = "홍길동")
    private String name;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/image1.jpg")
    private String profileImage;

    @Schema(description = "모임 내 역할", example = "HOST")
    private Role role;
}
