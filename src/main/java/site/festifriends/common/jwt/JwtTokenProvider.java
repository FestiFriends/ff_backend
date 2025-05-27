package site.festifriends.common.jwt;

import java.util.Date;

public interface JwtTokenProvider {

    String generateToken(Long memberId);

    Boolean validateToken(String token);

    String getSubject(String token);

    Date getExpiration(String token);
}
