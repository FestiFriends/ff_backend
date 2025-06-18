package site.festifriends.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.festifriends.common.jwt.JwtAuthenticationFilter;
import site.festifriends.common.jwt.JwtExceptionFilter;
import site.festifriends.common.jwt.JwtTokenProvider;
import site.festifriends.domain.auth.service.BlackListTokenService;
import site.festifriends.domain.auth.service.CustomUserDetailsService;

@Configuration
public class JwtFilterConfig {

    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider accessTokenProvider;
    private final BlackListTokenService blackListTokenService;

    JwtFilterConfig(
        ObjectMapper objectMapper,
        CustomUserDetailsService userDetailsService,
        @Qualifier("accessTokenProvider") JwtTokenProvider accessTokenProvider,
        BlackListTokenService blackListTokenService
    ) {
        this.objectMapper = objectMapper;
        this.userDetailsService = userDetailsService;
        this.accessTokenProvider = accessTokenProvider;
        this.blackListTokenService = blackListTokenService;
    }

    @Bean
    public JwtExceptionFilter jwtExceptionFilter() {
        return new JwtExceptionFilter(objectMapper);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(accessTokenProvider, userDetailsService, blackListTokenService);
    }
}
