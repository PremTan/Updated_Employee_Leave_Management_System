package com.employee.elms.security;
// utility class for generating/validating JWT

import com.employee.elms.entity.Employee;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "my_super_secret_key_123456789012345678901234567890";
    private static final long EXPIRATION_TIME = 3600000;

    private Key key;

    // Use @PostConstruct to initialize the key after property injection
    @PostConstruct
    public void initKey(){
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT
    public String generateToken(Employee employee){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", employee.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(employee.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract Claims safely
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            System.err.println("JWT processing error: " + e.getMessage());
            return null;    // token invalid or expired
        }

    }

    public Claims extractAllClaims(String token) {
        return getClaims(token); // reuse private method
    }

    // Extract email from token
    public String extractEmail(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    // Extract role from token
    public String extractRole(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    // Validate token
    public boolean isTokenValid(String token) {
        Claims claims = getClaims(token);
        return claims != null && !isTokenExpired(claims);
    }

    // Check if token expired
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
