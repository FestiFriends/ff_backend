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
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.post.dto.PostCreateRequest;
import site.festifriends.domain.post.dto.PostCreateResponse;
import site.festifriends.domain.post.dto.PostListCursorResponse;
import site.festifriends.domain.post.dto.PostListRequest;
import site.festifriends.domain.post.dto.PostPinRequest;
import site.festifriends.domain.post.dto.PostReactionRequest;
import site.festifriends.domain.post.dto.PostUpdateDeleteResponse;
import site.festifriends.domain.post.dto.PostUpdateRequest;

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
    ResponseEntity<PostListCursorResponse> getPostsByGroupId(
        @AuthenticationPrincipal UserDetailsImpl user,
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @Parameter(description = "요청 파라미터 (cursorId, size)") PostListRequest request);

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

    @Operation(
        summary = "모임 내 게시글 고정/해제",
        description = """
            모임 내 게시글을 고정하거나 고정을 해제합니다. 해당 모임에 속한 회원이면 누구나 고정/해제할 수 있습니다.
            
            **요청 본문:**
            - isPinned: 고정 여부 (true: 고정, false: 해제)
            
            **응답:**
            - 고정 시: "게시글이 고정되었습니다."
            - 해제 시: "게시글 고정이 해제되었습니다."
            
            **참고:**
            - 게시글을 고정(isPinned=true)하면 같은 모임 내 기존 고정글은 자동으로 고정 해제됩니다.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "게시글 고정/해제가 성공적으로 처리되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "해당 모임에 속한 회원만 게시글을 고정/해제할 수 있습니다."),
            @ApiResponse(responseCode = "404", description = "해당 모임 또는 게시글을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 게시글 고정/해제에 실패했습니다.")
        }
    )
    ResponseEntity<ResponseWrapper<Void>> pinPost(
        @AuthenticationPrincipal UserDetailsImpl user,
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @Parameter(description = "게시글 ID") @PathVariable Long postId,
        @Parameter(description = "게시글 고정/해제 정보") @RequestBody PostPinRequest request);

    @Operation(
        summary = "모임 내 게시글 반응 등록/취소",
        description = """
            모임 내 게시글에 반응을 등록하거나 취소합니다. 해당 모임에 속한 회원만 가능합니다.
            
            **요청 본문:**
            - hasReactioned: 반응 여부 (true: 반응 등록, false: 반응 취소)
            
            **응답:**
            - 반응 등록 시: "게시글에 반응이 등록되었습니다."
            - 반응 취소 시: "게시글 반응이 취소되었습니다."
            
            **참고:**
            - 한 회원당 하나의 게시글에 최대 1개의 반응만 가능합니다.
            - 반응이 등록/취소될 때마다 게시글의 reactionCount가 자동으로 업데이트됩니다.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "게시글 반응이 성공적으로 처리되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "해당 모임에 속한 회원만 게시글에 반응할 수 있습니다."),
            @ApiResponse(responseCode = "404", description = "해당 모임 또는 게시글을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 게시글 반응 처리에 실패했습니다.")
        }
    )
    ResponseEntity<ResponseWrapper<Void>> togglePostReaction(
        @AuthenticationPrincipal UserDetailsImpl user,
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @Parameter(description = "게시글 ID") @PathVariable Long postId,
        @Parameter(description = "게시글 반응 정보") @RequestBody PostReactionRequest request);
}
