package site.festifriends.domain.chat.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.festifriends.common.exception.BusinessException;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.CursorResponseWrapper;
import site.festifriends.domain.chat.dto.ChatMessageDto;
import site.festifriends.domain.chat.dto.ChatMessageRequest;
import site.festifriends.domain.chat.dto.ChatMessageResponse;
import site.festifriends.domain.chat.repository.ChatMessageRepository;
import site.festifriends.domain.chat.repository.ChatRoomRepository;
import site.festifriends.domain.chat.repository.MemberChatRoomRepository;
import site.festifriends.domain.image.dto.ImageDto;
import site.festifriends.domain.member.repository.MemberImageRepository;
import site.festifriends.domain.member.service.MemberService;
import site.festifriends.entity.ChatMessage;
import site.festifriends.entity.ChatRoom;
import site.festifriends.entity.Group;
import site.festifriends.entity.Member;
import site.festifriends.entity.MemberChatRoom;
import site.festifriends.entity.MemberImage;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberService memberService;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MemberImageRepository memberImageRepository;

    private static final String CHAT_ROOM_PREFIX = "/sub/chat/";

    @Transactional
    public ChatRoom createChatRoom(Group group) {
        ChatRoom newChatRoom = ChatRoom.builder()
            .group(group)
            .build();

        return chatRoomRepository.save(newChatRoom);
    }

    @Transactional
    public MemberChatRoom memberJoinChatRoom(Member member, Group group) {
        ChatRoom chatRoom = chatRoomRepository.findByGroup(group)
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "해당하는 채팅방이 없습니다."));

        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
            .member(member)
            .chatRoom(chatRoom)
            .build();

        return memberChatRoomRepository.save(memberChatRoom);
    }

    @Transactional
    public void leaveChatRoom(Member member, Group group) {
        ChatRoom chatRoom = chatRoomRepository.findByGroup(group)
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "해당하는 채팅방이 없습니다."));

        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberAndChatRoom(member, chatRoom)
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "회원이 채팅방에 참여되어 있지 않습니다."));

        memberChatRoomRepository.delete(memberChatRoom);
    }

    @Transactional
    public ChatMessageResponse sendToChatRoom(String chatRoomId, ChatMessageRequest message) {
        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatRoomId))
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "해당하는 채팅방이 없습니다."));

        Member member = memberService.getMemberById(message.getSenderId());

        ChatMessage newMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .member(member)
            .content(message.getContent())
            .build();

        chatMessageRepository.save(newMessage);

        MemberImage senderImage = memberImageRepository.findByMemberId(member.getId()).orElse(null);

        ImageDto senderImageDto = senderImage != null ?
            ImageDto.builder()
                .id(senderImage.getId().toString())
                .src(senderImage.getSrc())
                .alt(senderImage.getAlt())
                .build() : null;

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
            .chatId(newMessage.getId())
            .senderId(member.getId())
            .senderName(member.getNickname())
            .senderImage(senderImageDto)
            .content(message.getContent())
            .createdAt(newMessage.getCreatedAt())
            .build();

        simpMessagingTemplate.convertAndSend(CHAT_ROOM_PREFIX + chatRoomId, chatMessageResponse);

        return chatMessageResponse;
    }

    @Transactional(readOnly = true)
    public CursorResponseWrapper<ChatMessageResponse> getChatMessages(Long chatRoomId, Long cursorId, int size) {
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "해당하는 채팅방이 없습니다.");
        }

        Pageable pageable = PageRequest.of(0, size);
        Slice<ChatMessageDto> slice = chatMessageRepository.getChatMessages(chatRoomId, cursorId, pageable);

        if (slice.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "채팅 메시지가 없습니다.");
        }

        List<ChatMessageResponse> response = new ArrayList<>();

        for (ChatMessageDto chatMessage : slice.getContent()) {
            response.add(ChatMessageResponse.builder()
                .chatId(chatMessage.getChatId())
                .senderId(chatMessage.getSenderId())
                .senderName(chatMessage.getSenderName())
                .senderImage(chatMessage.getSenderImage())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .build());
        }

        Long nextCursorId = null;
        if (slice.hasNext()) {
            response.remove(response.size() - 1);
            nextCursorId = slice.getContent().get(size).getChatId();
        }

        return CursorResponseWrapper.success(
            "채팅 메시지를 성공적으로 조회했습니다.",
            response,
            nextCursorId,
            slice.hasNext()
        );
    }

    public void joinChatRoom(Member member, ChatRoom chatRoom) {
        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
            .member(member)
            .chatRoom(chatRoom)
            .build();

        memberChatRoomRepository.save(memberChatRoom);
    }

    public ChatRoom getChatRoomByGroup(Group group) {
        return chatRoomRepository.findByGroup(group)
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "해당하는 채팅방이 없습니다."));
    }
}
