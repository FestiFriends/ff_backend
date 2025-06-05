package site.festifriends.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.Member;

@Getter
@Builder
public class CommentAuthorResponse {

    private Long id;
    private String name;
    private String profileImage;

    public static CommentAuthorResponse from(Member member) {
        return CommentAuthorResponse.builder()
            .id(member.getId())
            .name(member.getNickname())
            .profileImage(member.getProfileImageUrl())
            .build();
    }
}
