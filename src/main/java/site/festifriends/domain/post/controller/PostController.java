package site.festifriends.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.post.dto.PostListRequest;
import site.festifriends.domain.post.dto.PostResponse;
import site.festifriends.domain.post.service.PostService;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class PostController implements PostApi {

    private final PostService postService;

    @Override
    @GetMapping("/{groupId}/posts")
    public ResponseEntity<CursorResponseWrapper<PostResponse>> getPostsByGroupId(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PathVariable Long groupId,
        PostListRequest request
    ) {
        CursorResponseWrapper<PostResponse> response = postService.getPostsByGroupId(groupId, user.getMemberId(), request);

        return ResponseEntity.ok(response);
    }
}
