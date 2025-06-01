package site.festifriends.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostListCursorResponse {

    private Integer code;
    private String message;
    private PostListResponse data;
    private Long cursorId;
    private Boolean hasNext;

    public static PostListCursorResponse success(String message, PostListResponse data, Long cursorId,
        Boolean hasNext) {
        return PostListCursorResponse.builder()
            .code(200)
            .message(message)
            .data(data)
            .cursorId(cursorId)
            .hasNext(hasNext)
            .build();
    }
}
