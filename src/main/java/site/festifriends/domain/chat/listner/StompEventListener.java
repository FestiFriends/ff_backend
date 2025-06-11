package site.festifriends.domain.chat.listner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompEventListener extends DefaultHandshakeHandler {

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();

        String userId = accessor.getSessionAttributes().get("memberId").toString();
        Long chatRoomId = Long.parseLong(destination.substring(destination.lastIndexOf("/") + 1));

        log.info("회원{}이 {}번채팅방을 구독했습니다.", userId, chatRoomId);
    }

    @EventListener
    public void sessionConnectEvent(SessionConnectedEvent event) {
    }

    @EventListener
    public void sessionDisconnectEvent(SessionDisconnectEvent event) {
    }

}
