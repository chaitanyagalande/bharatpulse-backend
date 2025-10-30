package com.example.CityPolling.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
//The Token Maker & Checker
public class JwtUtil {
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12"; // must be >= 32 chars
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes()); // Making key for token generation

    // Token Maker
    public String generateToken(String email) { // generates the token by signing with above key
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1-hour
                .signWith(key)
                .compact();
    }

    // Token Checker
    public String extractEmail(String token) { // Extracts the email out of the Token
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}