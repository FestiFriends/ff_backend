package site.festifriends.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.post.dto.PostListRequest;
import site.festifriends.domain.post.dto.PostResponse;

@Tag(name = "Post", description = "모임 게시글 관련 API")
public interface PostApi {

    @Operation(
            summary = "모임 내 게시글 목록 조회",
            description = """
                    모임 내 게시글 목록을 커서 기반 페이지네이션으로 조회합니다.

                    **요청 파라미터:**
                    - cursorId: 이전 응답에서 받은 커서값, 없으면 첫 페이지 조회
                    - size: 한 번에 가져올 게시글 개수, 기본값 20

                    **응답:**
                    - 게시글 목록은 고정 게시글(isPinned=true)이 먼저 표시되고, 그 다음 최신순으로 정렬됩니다.
                    - 각 게시글에는 작성자 정보, 내용, 이미지, 댓글 수, 반응 수 등이 포함됩니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
                    @ApiResponse(responseCode = "403", description = "해당 모임에 속한 회원만 조회 가능"),
                    @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
            }
    )
    ResponseEntity<CursorResponseWrapper<PostResponse>> getPostsByGroupId(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Parameter(description = "모임 ID") @PathVariable Long groupId,
            @Parameter(description = "요청 파라미터 (cursorId, size)") @ModelAttribute PostListRequest request);
}
