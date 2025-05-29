package site.festifriends.common.jwt;

import java.util.Date;

public interface JwtTokenProvider {

    String generateToken(Long memberId);

    boolean validateToken(String token);

    String getSubject(String token);

    Date getExpiration(String token);

    String getTokenType(String token);
}
