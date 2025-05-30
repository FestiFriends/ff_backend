package site.festifriends.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateDeleteResponse {
    
    private boolean result;
    
    public static PostUpdateDeleteResponse success() {
        PostUpdateDeleteResponse response = new PostUpdateDeleteResponse();
        response.setResult(true);
        return response;
    }
    
    public static PostUpdateDeleteResponse fail() {
        PostUpdateDeleteResponse response = new PostUpdateDeleteResponse();
        response.setResult(false);
        return response;
    }
}