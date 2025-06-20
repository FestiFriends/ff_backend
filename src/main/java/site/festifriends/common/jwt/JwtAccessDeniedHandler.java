package site.festifriends.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import site.festifriends.common.exception.ErrorCode;
import site.festifriends.common.response.ResponseWrapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {
        log.warn("Access denied: {}, method={}, uri={}",
            accessDeniedException.getMessage(),
            request.getMethod(),
            request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json; charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), ResponseWrapper.error(ErrorCode.FORBIDDEN));
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
