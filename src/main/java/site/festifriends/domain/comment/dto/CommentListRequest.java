package site.festifriends.domain.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentListRequest {

    private Long cursor;
    private Integer size;

    public Long getCursorId() {
        return cursor;
    }
}
