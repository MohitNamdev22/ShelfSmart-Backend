package com.shelfsmart.shelfsmart_backend.security;

import com.shelfsmart.shelfsmart_backend.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Static secret key to ensure consistency across instances
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long expiration = 1000 * 60 * 60; // 1 hour
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            logger.info("Token validated successfully: {}", token);
            return true;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage(), e);
            return false;
        }
    }
}