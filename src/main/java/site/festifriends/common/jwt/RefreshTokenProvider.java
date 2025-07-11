package site.festifriends.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenProvider implements JwtTokenProvider {

    @Value("${jwt.refresh.secret}")
    private String refreshSecretKey;
    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(Long memberId) {
        return Jwts
            .builder()
            .header()
            .type("JWT")
            .and()
            .issuer("festifriends")
            .subject(memberId.toString())
            .claim("tokenType", "refresh")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
            .signWith(key)
            .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getSubject(String token) {
        Claims claims = Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }

    @Override
    public Date getExpiration(String token) {
        return Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration();
    }

    @Override
    public String getTokenType(String token) {
        return Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("tokenType", String.class);
    }
}
