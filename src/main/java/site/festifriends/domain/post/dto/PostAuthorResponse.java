package site.festifriends.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.Member;

@Getter
@Builder
public class PostAuthorResponse {

    private Long id;
    private String name;
    private String profileImage;

    public static PostAuthorResponse from(Member member) {
        return PostAuthorResponse.builder()
            .id(member.getId())
            .name(member.getNickname())
            .profileImage(member.getProfileImageUrl())
            .build();
    }
}
