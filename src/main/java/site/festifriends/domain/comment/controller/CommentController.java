package site.festifriends.domain.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.comment.dto.CommentCreateRequest;
import site.festifriends.domain.comment.dto.CommentListCursorResponse;
import site.festifriends.domain.comment.dto.CommentListRequest;
import site.festifriends.domain.comment.dto.CommentResponse;
import site.festifriends.domain.comment.service.CommentService;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @Override
    @GetMapping("/{groupId}/posts/{postId}/comments")
    public ResponseEntity<CommentListCursorResponse> getCommentsByPostId(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long groupId,
        @PathVariable Long postId,
        CommentListRequest request
    ) {
        CursorResponseWrapper<CommentResponse> commentResponse = commentService.getCommentsByPostId(
            groupId, postId, user.getMemberId(), request);

        CommentListCursorResponse response = CommentListCursorResponse.success(
            commentResponse.getMessage(),
            commentResponse.getData(),
            commentResponse.getCursorId(),
            commentResponse.getHasNext()
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{groupId}/posts/{postId}/comments")
    public ResponseEntity<ResponseWrapper<Void>> createComment(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long groupId,
        @PathVariable Long postId,
        @RequestBody CommentCreateRequest request
    ) {
        commentService.createComment(groupId, postId, user.getMemberId(), request);

        return ResponseEntity.ok(ResponseWrapper.success("댓글이 성공적으로 등록되었습니다."));
    }
}
