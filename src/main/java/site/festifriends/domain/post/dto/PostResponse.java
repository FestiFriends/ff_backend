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

    @JsonProperty("isMine")
    private boolean isMine;

    @JsonProperty("hasReactioned")
    private boolean hasReactioned;

    private int imageCount;
    private List<PostImageResponse> images;
    private PostAuthorResponse author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int commentCount;
    private int reactionCount;

    public static PostResponse from(Post post) {
        return from(post, null, false);
    }

    public static PostResponse from(Post post, Long currentUserId) {
        return from(post, currentUserId, false);
    }

    public static PostResponse from(Post post, Long currentUserId, boolean hasReactioned) {
        boolean isMine = currentUserId != null && post.isMine(currentUserId);

        return PostResponse.builder()
            .id(post.getId())
            .groupId(post.getGroup().getId())
            .content(post.getContent())
            .isPinned(post.isPinned())
            .isReported(post.isReported())
            .isMine(isMine)
            .hasReactioned(hasReactioned)
            .imageCount(post.getImageCount())
            .images(post.getImages().stream()
                .map(PostImageResponse::from)
                .collect(Collectors.toList()))
            .author(PostAuthorResponse.from(post.getAuthor()))
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .commentCount(post.getCommentCount())
            .reactionCount(post.getReactionCount())
            .build();
    }
}
