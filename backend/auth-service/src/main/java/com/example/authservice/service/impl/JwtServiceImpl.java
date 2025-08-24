package com.example.authservice.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.authservice.service.JwtService;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${app.jwt.secret:mySecretKey}")
    private String jwtSecret;
    
    @Value("${app.jwt.access-token-expiration:86400}") // 24 hours
    private long accessTokenExpiration;
    
    @Value("${app.jwt.refresh-token-expiration:604800}") // 7 days
    private long refreshTokenExpiration;
    
    @Override
    public String generateAccessToken(UUID userId, String email,String role) {
        return Jwts.builder()
        .setSubject(userId.toString())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
        .signWith(SignatureAlgorithm.HS256, jwtSecret)
        .compact();
    }
}
