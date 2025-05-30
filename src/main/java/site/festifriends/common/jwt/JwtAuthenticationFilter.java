package site.festifriends.common.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import site.festifriends.domain.auth.UserDetailsImpl;
import site.festifriends.domain.auth.service.CustomUserDetailsService;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider accessTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    private final List<String> jwtIgnoreUrls = List.of("/api/v1/auth/token");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        if (shouldIgnoreRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = TokenResolver.extractAccessToken(request);

        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                String accessTokenType = accessTokenProvider.getTokenType(accessToken);
                if ("access".equals(accessTokenType)) {
                    UserDetailsImpl userDetails = getUserDetails(accessToken);
                    setAuthenticationUser(userDetails, request);
                }
            } catch (ExpiredJwtException e1) {
                throw new ServletException();
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldIgnoreRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        Optional<String> judge = jwtIgnoreUrls.stream()
            .filter(v ->
                Pattern.matches(v.replace("**", ".*"), uri) ||
                    Pattern.matches(v.replace("/**", ""), uri)
            )
            .findFirst();
        return judge.isPresent() || "OPTIONS".equals(method);
    }

    private UserDetailsImpl getUserDetails(String accessToken) {
        Long userId = Long.valueOf(accessTokenProvider.getSubject(accessToken));

        return userDetailsService.loadUserById(userId);
    }

    private void setAuthenticationUser(UserDetailsImpl userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
