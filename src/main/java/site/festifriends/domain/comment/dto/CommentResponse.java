package site.festifriends.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import site.festifriends.entity.Comments;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private Long postId;
    private CommentAuthorResponse author;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonProperty("isReported")
    private boolean isReported;

    @JsonProperty("isMine")
    private boolean isMine;

    public static CommentResponse from(Comments comments) {
        return from(comments, null);
    }

    public static CommentResponse from(Comments comments, Long currentUserId) {
        boolean isMine = currentUserId != null && comments.isMine(currentUserId);

        return CommentResponse.builder()
            .id(comments.getId())
            .postId(comments.getPost().getId())
            .author(CommentAuthorResponse.from(comments.getAuthor()))
            .content(comments.getContent())
            .createdAt(comments.getCreatedAt())
            .isReported(comments.isReported())
            .isMine(isMine)
            .build();
    }
}
