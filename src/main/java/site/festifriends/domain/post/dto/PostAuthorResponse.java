package site.festifriends.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import site.festifriends.domain.image.dto.ImageDto;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberImage;

@Getter
@Builder
public class PostAuthorResponse {

    private Long id;
    private String name;
    private ImageDto profileImage;

    public static PostAuthorResponse from(Member member, MemberImage memberImage) {
        ImageDto profileImage = null;

        if (memberImage != null) {
            profileImage = ImageDto.builder()
                .id(memberImage.getId().toString())
                .src(memberImage.getSrc())
                .alt(memberImage.getAlt())
                .build();
        }

        return PostAuthorResponse.builder()
            .id(member.getId())
            .name(member.getNickname())
            .profileImage(profileImage)
            .build();
    }
}
