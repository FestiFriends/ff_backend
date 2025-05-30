package site.festifriends.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.post.dto.PostCreateRequest;
import site.festifriends.domain.post.dto.PostCreateResponse;
import site.festifriends.domain.post.dto.PostListRequest;
import site.festifriends.domain.post.dto.PostResponse;
import site.festifriends.domain.post.dto.PostUpdateRequest;
import site.festifriends.domain.post.dto.PostUpdateDeleteResponse;

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

    @Operation(
            summary = "모임 내 게시글 등록",
            description = """
                    모임 내 게시글을 등록합니다.

                    **요청 본문:**
                    - content: 게시글 내용 (필수)
                    - isPinned: 상단 고정 여부 (선택, 기본값 false)
                    - images: 첨부 이미지 배열 (선택)

                    **응답:**
                    - 게시글 등록 성공 여부(result)만 반환됩니다.
                    - isPinned가 true인 경우, 같은 모임 내 기존 고정글은 자동으로 고정 해제(isPinned=false)됩니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 등록되었습니다."),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
                    @ApiResponse(responseCode = "403", description = "게시글 등록 권한이 없습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류로 인해 게시글 등록에 실패했습니다.")
            }
    )
    ResponseEntity<ResponseWrapper<PostCreateResponse>> createPost(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Parameter(description = "모임 ID") @PathVariable Long groupId,
            @Parameter(description = "게시글 등록 정보") @RequestBody PostCreateRequest request);

    @Operation(
            summary = "모임 내 게시글 수정",
            description = """
                    모임 내 게시글을 수정합니다. 게시글 작성자만 수정할 수 있습니다.

                    **요청 본문:**
                    - content: 게시글 내용 (선택)
                    - isPinned: 상단 고정 여부 (선택)
                    - images: 첨부 이미지 배열 (선택)

                    **응답:**
                    - 게시글 수정 성공 여부(result)만 반환됩니다.
                    - isPinned가 true인 경우, 같은 모임 내 기존 고정글은 자동으로 고정 해제(isPinned=false)됩니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 수정되었습니다."),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
                    @ApiResponse(responseCode = "403", description = "게시글 수정 권한이 없습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 모임 또는 게시글을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류로 인해 게시글 수정에 실패했습니다.")
            }
    )
    ResponseEntity<ResponseWrapper<PostUpdateDeleteResponse>> updatePost(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Parameter(description = "모임 ID") @PathVariable Long groupId,
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "게시글 수정 정보") @RequestBody PostUpdateRequest request);

    @Operation(
            summary = "모임 내 게시글 삭제",
            description = """
                    모임 내 게시글을 삭제합니다. 게시글 작성자만 삭제할 수 있습니다.

                    **응답:**
                    - 게시글 삭제 성공 여부(result)만 반환됩니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글이 성공적으로 삭제되었습니다."),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
                    @ApiResponse(responseCode = "403", description = "게시글 삭제 권한이 없습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 모임 또는 게시글을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "500", description = "서버 오류로 인해 게시글 삭제에 실패했습니다.")
            }
    )
    ResponseEntity<ResponseWrapper<PostUpdateDeleteResponse>> deletePost(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Parameter(description = "모임 ID") @PathVariable Long groupId,
            @Parameter(description = "게시글 ID") @PathVariable Long postId);
}
