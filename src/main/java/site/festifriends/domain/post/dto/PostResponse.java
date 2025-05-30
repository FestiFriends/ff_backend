package site.festifriends.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PostResponse {

    private Long id;
    private Long groupId;
    private String content;

    @JsonProperty("isPinned")
    private boolean isPinned;

    @JsonProperty("isReported")
    private boolean isReported;

    private int imageCount;
    private List<PostImageResponse> images;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int commentCount;
    private int reactionCount;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .groupId(post.getGroup().getId())
                .content(post.getContent())
                .isPinned(post.isPinned())
                .isReported(post.isReported())
                .imageCount(post.getImageCount())
                .images(post.getImages().stream()
                        .map(PostImageResponse::from)
                        .collect(Collectors.toList()))
                .authorId(post.getAuthor().getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .commentCount(post.getCommentCount())
                .reactionCount(post.getReactionCount())
                .build();
    }
}
