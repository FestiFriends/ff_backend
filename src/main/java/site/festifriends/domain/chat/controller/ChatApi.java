package site.festifriends.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.chat.dto.ChatMessageResponse;

public interface ChatApi {

    @Operation(
        summary = "채팅방 메시지 목록 조회",
        description = "채팅방의 메시지 목록을 조회합니다.(커서 방식)",
        responses = {
            @ApiResponse(responseCode = "200", description = "메시지 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
        }
    )
    ResponseEntity<CursorResponseWrapper<ChatMessageResponse>> getChatMessages(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(description = "채팅방 ID")
        @RequestParam Long chatRoomId,
        @Parameter(description = "이전 응답에서 받은 커서값, 없으면 첫 페이지 조회")
        @RequestParam(required = false) Long cursorId,
        @Parameter(description = "한 번에 가져올 신청서 개수, 기본값 20")
        @RequestParam(defaultValue = "20") int size
    );
}
