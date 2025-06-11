package site.festifriends.domain.chat.interceptor;

import java.security.Principal;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import site.festifriends.common.jwt.AccessTokenProvider;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final AccessTokenProvider accessTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert accessor != null;
        if (accessor.getCommand() == StompCommand.CONNECT) {
            String token = accessor.getNativeHeader("Authorization").get(0);
            token = token.substring(7);

            Long memberId = Long.valueOf(accessTokenProvider.getSubject(token));

            Principal principal = new UsernamePasswordAuthenticationToken(
                memberId.toString(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            accessor.setUser(principal);
            accessor.getSessionAttributes().put("memberId", memberId);
        }

        return message;
    }
}
