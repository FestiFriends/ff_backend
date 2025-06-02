package site.festifriends.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.post.dto.PostCreateRequest;
import site.festifriends.domain.post.dto.PostCreateResponse;
import site.festifriends.domain.post.dto.PostListCursorResponse;
import site.festifriends.domain.post.dto.PostListRequest;
import site.festifriends.domain.post.dto.PostListResponse;
import site.festifriends.domain.post.dto.PostPinRequest;
import site.festifriends.domain.post.dto.PostResponse;
import site.festifriends.domain.post.dto.PostUpdateDeleteResponse;
import site.festifriends.domain.post.dto.PostUpdateRequest;
import site.festifriends.domain.post.service.PostService;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class PostController implements PostApi {

    private final PostService postService;

    @Override
    @GetMapping("/{groupId}/posts")
    public ResponseEntity<PostListCursorResponse> getPostsByGroupId(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long groupId,
        PostListRequest request
    ) {
        CursorResponseWrapper<PostResponse> postResponse = postService.getPostsByGroupId(groupId, user.getMemberId(),
            request);

        PostListResponse data = PostListResponse.of(groupId, postResponse.getData());

        PostListCursorResponse response = PostListCursorResponse.success(
            "게시글 목록 조회 성공.",
            data,
            postResponse.getCursorId(),
            postResponse.getHasNext()
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{groupId}/posts")
    public ResponseEntity<ResponseWrapper<PostCreateResponse>> createPost(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long groupId,
        @RequestBody PostCreateRequest request
    ) {
        PostCreateResponse response = postService.createPost(groupId, user.getMemberId(), request);

        return ResponseEntity.ok(ResponseWrapper.success("게시글이 성공적으로 등록되었습니다.", response));
    }

    @Override
    @PatchMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<ResponseWrapper<PostUpdateDeleteResponse>> updatePost(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long groupId,
        @PathVariable Long postId,
        @RequestBody PostUpdateRequest request
    ) {
        PostUpdateDeleteResponse response = postService.updatePost(groupId, postId, user.getMemberId(), request);

        return ResponseEntity.ok(ResponseWrapper.success("게시글이 성공적으로 수정되었습니다.", response));
    }

    @Override
    @DeleteMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<ResponseWrapper<PostUpdateDeleteResponse>> deletePost(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long groupId,
        @PathVariable Long postId
    ) {
        PostUpdateDeleteResponse response = postService.deletePost(groupId, postId, user.getMemberId());

        return ResponseEntity.ok(ResponseWrapper.success("게시글이 성공적으로 삭제되었습니다.", response));
    }

    @Override
    @PatchMapping("/{groupId}/posts/{postId}/pinned")
    public ResponseEntity<ResponseWrapper<Void>> pinPost(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long groupId,
        @PathVariable Long postId,
        @RequestBody PostPinRequest request
    ) {
        postService.pinPost(groupId, postId, user.getMemberId(), request);

        String message = Boolean.TRUE.equals(request.getIsPinned())
            ? "게시글이 고정되었습니다."
            : "게시글 고정이 해제되었습니다.";

        return ResponseEntity.ok(ResponseWrapper.success(message));
    }
}
