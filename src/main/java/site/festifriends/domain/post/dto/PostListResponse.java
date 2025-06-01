package site.festifriends.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListResponse {

    private Long groupId;
    private List<PostResponse> posts;

    public static PostListResponse of(Long groupId, List<PostResponse> posts) {
        return PostListResponse.builder()
            .groupId(groupId)
            .posts(posts)
            .build();
    }
}
