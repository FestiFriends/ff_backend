package site.festifriends.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostGroupResponse {
    private Long groupId;
    private List<PostResponse> posts;
}