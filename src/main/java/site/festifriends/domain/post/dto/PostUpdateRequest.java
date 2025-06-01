package site.festifriends.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostUpdateRequest {
    
    private String content;
    private Boolean isPinned;
    private List<PostImageUpdateRequest> images;
    
    @Getter
    @Setter
    public static class PostImageUpdateRequest {
        private String alt;
        private String src;
    }
}