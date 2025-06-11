package site.festifriends.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.chat.dto.ChatMessageRequest;
import site.festifriends.domain.chat.dto.ChatMessageResponse;
import site.festifriends.domain.chat.service.ChatService;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController implements ChatApi {

    private final ChatService chatService;

    @MessageMapping("/chat/{chatRoomId}")
    public ChatMessageResponse chat(@DestinationVariable String chatRoomId, ChatMessageRequest message) {
        return chatService.sendToChatRoom(chatRoomId, message);
    }

    @Override
    @GetMapping("/list")
    public ResponseEntity<CursorResponseWrapper<ChatMessageResponse>> getChatMessages(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam Long chatRoomId,
        @RequestParam(required = false) Long cursorId,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(chatService.getChatMessages(chatRoomId, cursorId, size));
    }

}
