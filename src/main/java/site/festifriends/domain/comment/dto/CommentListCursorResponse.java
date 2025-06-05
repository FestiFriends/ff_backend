package site.festifriends.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;
import site.festifriends.common.response.CursorResponseWrapper;

import java.util.List;

@Getter
@Builder
public class CommentListCursorResponse {

    private int code;
    private String message;
    private List<CommentResponse> data;
    private Long cursorId;
    private boolean hasNext;

    public static CommentListCursorResponse success(String message, List<CommentResponse> data, Long cursorId, boolean hasNext) {
        return CommentListCursorResponse.builder()
            .code(200)
            .message(message)
            .data(data)
            .cursorId(cursorId)
            .hasNext(hasNext)
            .build();
    }

    public static CommentListCursorResponse empty(String message) {
        return CommentListCursorResponse.builder()
            .code(200)
            .message(message)
            .data(List.of())
            .cursorId(null)
            .hasNext(false)
            .build();
    }
}
