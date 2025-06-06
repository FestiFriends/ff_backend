package site.festifriends.domain.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import site.festifriends.common.response.ResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.comment.dto.CommentCreateRequest;
import site.festifriends.domain.comment.dto.CommentListCursorResponse;
import site.festifriends.domain.comment.dto.CommentListRequest;

@Tag(name = "Comment", description = "게시글 댓글 관련 API")
public interface CommentApi {

    @Operation(
        summary = "게시글 댓글 목록 조회",
        description = """
            특정 게시글의 댓글 목록을 커서 기반 페이지네이션으로 조회합니다.
            
            **요청 파라미터:**
            - cursor: 이전 응답에서 받은 커서값, 없으면 첫 페이지 조회
            - size: 한 번에 가져올 댓글 개수, 기본값 20
            
            **응답:**
            - 댓글 목록은 작성 시간 순으로 정렬됩니다.
            - 각 댓글에는 작성자 정보, 내용, 작성 시간, 신고 여부 등이 포함됩니다.
            - 현재 사용자가 작성한 댓글인지 여부(isMine)도 포함됩니다.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "댓글 목록을 불러오는데 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "해당 모임에 속한 회원만 조회 가능"),
            @ApiResponse(responseCode = "404", description = "모임 또는 게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        }
    )
    ResponseEntity<CommentListCursorResponse> getCommentsByPostId(
        @AuthenticationPrincipal UserDetailsImpl user,
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @Parameter(description = "게시글 ID") @PathVariable Long postId,
        @Parameter(description = "요청 파라미터 (cursor, size)") CommentListRequest request);

    @Operation(
        summary = "게시글 댓글 작성",
        description = """
            모임에 참가한 사용자가 특정 게시글에 댓글을 작성합니다.
            
            **요청:**
            - content: 댓글 내용 (필수, 최대 500자)
            
            **응답:**
            - 작성된 댓글의 정보가 반환됩니다.
            - 댓글 ID, 작성자 정보, 내용, 작성 시간이 포함됩니다.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @ApiResponse(responseCode = "403", description = "댓글 등록 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "해당 모임을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 댓글 등록에 실패했습니다.")
        }
    )
    ResponseEntity<ResponseWrapper<Void>> createComment(
        @AuthenticationPrincipal UserDetailsImpl user,
        @Parameter(description = "모임 ID") @PathVariable Long groupId,
        @Parameter(description = "게시글 ID") @PathVariable Long postId,
        @Parameter(description = "댓글 작성 요청") @RequestBody CommentCreateRequest request);
}
