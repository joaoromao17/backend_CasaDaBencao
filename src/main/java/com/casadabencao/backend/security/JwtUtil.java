package com.casadabencao.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final long EXPIRATION_TIME = 86400000; // 1 dia
    private final long ACCESS_EXPIRATION = 60 * 60 * 1000; // 1 hora
    private final long REFRESH_EXPIRATION = 60L * 24 * 60 * 60 * 1000; // 60 dias

public SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
}

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
        return claims.get("userId", Long.class);
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
        return claims.getSubject();
    }

    public String getSecret() {
        return secret;
    }

    public String generateAccessToken(Long userId, String email, List<String> roles) {
        return Jwts.builder()
                .setSubject(email.toLowerCase())
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Long userId, String email, List<String> roles) {
        return Jwts.builder()
                .setSubject(email.toLowerCase())
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parseToken(token);
        return "refresh".equals(claims.get("type", String.class));
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }

    public List<String> getRolesFromToken(String token) {
        return parseToken(token).get("roles", List.class);
    }
}
