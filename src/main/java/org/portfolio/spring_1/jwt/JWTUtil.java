package org.portfolio.spring_1.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret-key}") String secret) {
        secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm() // == HmacSHA256
        );
    }

    // 클레임 값 반환 메소드
    private Claims getClaimValue(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getClaim(String token, String claimName) {
        return getClaimValue(token).get(claimName, String.class);
    }

    public Boolean isExpired(String token) {
        return getClaimValue(token).getExpiration().before(new Date());
    }

    public String createJwt(String serial, String role, String category, String name, String provider, Long expiredMs) {
        return Jwts.builder()
                .claim("serial", serial)
                .claim("role", role)
                .claim("category", category)
                .claim("name", name)
                .claim("provider", provider)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
