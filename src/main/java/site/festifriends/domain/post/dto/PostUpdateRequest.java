package site.festifriends.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostUpdateRequest {
    
    private String content;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isPinned;
    
    private List<PostImageUpdateRequest> images;
    
    @Getter
    @Setter
    public static class PostImageUpdateRequest {
        private String alt;
        private String src;
    }
}