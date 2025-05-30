package site.festifriends.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListResponse {
    
    private Integer code;
    private String message;
    private String cursorId;
    private boolean hasNext;
    private PostListData data;
    
    @Getter
    @Builder
    public static class PostListData {
        private Long groupId;
        private List<PostResponse> posts;
    }
    
    public static PostListResponse of(Integer code, String message, String cursorId, boolean hasNext, Long groupId, List<PostResponse> posts) {
        return PostListResponse.builder()
                .code(code)
                .message(message)
                .cursorId(cursorId)
                .hasNext(hasNext)
                .data(PostListData.builder()
                        .groupId(groupId)
                        .posts(posts)
                        .build())
                .build();
    }
}