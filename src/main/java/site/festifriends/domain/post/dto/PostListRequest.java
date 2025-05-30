package site.festifriends.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostListRequest {

    private Long cursorId; // 이전 응답에서 받은 커서값, 없으면 첫 페이지 조회
    private Integer size = 20; // 한 번에 가져올 게시글 개수, 기본값 20
}
