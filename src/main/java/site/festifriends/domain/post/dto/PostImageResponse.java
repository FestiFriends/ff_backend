package site.festifriends.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.PostImage;

@Getter
@Builder
public class PostImageResponse {
    
    private Long id;
    private String alt;
    private String src;
    
    public static PostImageResponse from(PostImage image) {
        return PostImageResponse.builder()
                .id(image.getId())
                .alt(image.getAlt())
                .src(image.getSrc())
                .build();
    }
}