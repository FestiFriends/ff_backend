package site.festifriends.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCreateRequest {
    
    private String content;
    private Boolean isPinned;
    private List<PostImageCreateRequest> images;
    
    @Getter
    @Setter
    public static class PostImageCreateRequest {
        private String alt;
        private String src;
    }
}