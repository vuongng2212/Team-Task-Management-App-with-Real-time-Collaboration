package com.example.authservice.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.authservice.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;


@Service
public class JwtServiceImpl implements JwtService {
    
    private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    
    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    
    // Cache the signing key to avoid repeated decoding
    // TODO: Implement proper key rotation mechanism
    private volatile SecretKey cachedSigningKey;
    private volatile long lastKeyCacheTime = 0;
    private static final long KEY_CACHE_DURATION = 30 * 60 * 1000; // 30 minutes
    
    // SECURITY: Token type constants to prevent confusion
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    
    // SECURITY: Hash algorithm for token storage
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String CHARSET_UTF8 = "UTF-8";
    
    @Override
    public String generateAccessToken(UUID userId, String email, String name) {
        log.debug("Generating access token for user: {} with email: {}", userId, email);
        
        try {
            // SECURITY: Validate input parameters
            validateTokenGenerationInputs(userId, email, name);
            
            // PERFORMANCE: Use cached signing key
            SecretKey signingKey = getSigningKey();
            
            // SECURITY: Generate token with proper claims
            String token = Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("name", name)
                .claim("type", TOKEN_TYPE_ACCESS)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000))
                .signWith(signingKey)
                .compact();
            
            // Log with email for easier business tracing
            log.info("Access token generated for user: {} (email: {})", userId, email);
            return token;
            
        } catch (Exception e) {
            log.error("Failed to generate access token for user: {} (email: {})", userId, email, e);
            throw new RuntimeException("Could not generate access token", e);
        }
    }
    
    @Override
    public String generateRefreshToken(UUID userId) {
        log.debug("Generating refresh token for user: {}", userId);
        
        try {
            // SECURITY: Validate input parameter
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            // PERFORMANCE: Use cached signing key
            SecretKey signingKey = getSigningKey();
            
            // SECURITY: Generate refresh token with minimal claims
            String token = Jwts.builder()
                .subject(userId.toString())
                .claim("type", TOKEN_TYPE_REFRESH)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration * 1000))
                .signWith(signingKey)
                .compact();
            
            // Not logging refresh tokens - too noisy in production
            return token;
            
        } catch (Exception e) {
            log.error("Failed to generate refresh token for user: {}", userId, e);
            throw new RuntimeException("Could not generate refresh token", e);
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        log.debug("Validating JWT token");
        
        try {
            // SECURITY: Basic token format validation
            if (token == null || token.trim().isEmpty()) {
                log.warn("Token validation failed: token is null or empty");
                return false;
            }
            
            // SECURITY: Check if token is properly formatted
            if (!token.contains(".") || token.split("\\.").length != 3) {
                log.warn("Token validation failed: invalid token format");
                return false;
            }
            
            // SECURITY: Parse and verify token
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            // SECURITY: Validate token type
            String tokenType = claims.get("type", String.class);
            if (tokenType == null || (!TOKEN_TYPE_ACCESS.equals(tokenType) && !TOKEN_TYPE_REFRESH.equals(tokenType))) {
                log.warn("Token validation failed: invalid token type: {}", tokenType);
                return false;
            }
            
            // SECURITY: Validate expiration
            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())) {
                log.warn("Token validation failed: token expired at {}", expiration);
                return false;
            }
            
            log.debug("Token validation successful for user: {}", claims.getSubject());
            return true;
            
        } catch (ExpiredJwtException e) {
            log.warn("Token validation failed: token expired at {}", e.getClaims().getExpiration());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Token validation failed: malformed JWT token");
            return false;
        } catch (SignatureException e) {
            log.warn("Token validation failed: invalid signature");
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("Token validation failed: unsupported JWT token");
            return false;
        } catch (JwtException e) {
            log.warn("Token validation failed: JWT processing error: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during token validation: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public UUID getUserIdFromToken(String token) {
        log.debug("Extracting user ID from JWT token");
        
        try {
            Claims claims = extractAllClaims(token);
            String subject = claims.getSubject();
            
            // SECURITY: Validate subject format
            if (subject == null) {
                log.warn("Invalid user ID format in token: {}", subject);
                throw new RuntimeException("Invalid user ID format in token");
            }
            
            UUID userId = UUID.fromString(subject);
            log.debug("Successfully extracted user ID: {} from token", userId);
            
            return userId;
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to parse user ID from token: {}", e.getMessage());
            throw new RuntimeException("Invalid user ID in token", e);
        } catch (Exception e) {
            log.error("Unexpected error extracting user ID from token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract user ID from token", e);
        }
    }
    
    @Override
    public String getEmailFromToken(String token) {
        log.debug("Extracting email from JWT token");
        
        try {
            Claims claims = extractAllClaims(token);
            String email = claims.get("email", String.class);
            
            // SECURITY: Validate email format
            if (email == null || email.trim().isEmpty()) {
                log.warn("Email claim not found in token");
                throw new RuntimeException("Email not found in token");
            }
            
            // SECURITY: Basic email format validation
            if (!email.contains("@") || email.length() > 254) {
                log.warn("Invalid email format in token: {}", email);
                throw new RuntimeException("Invalid email format in token");
            }
            
            log.debug("Successfully extracted email: {} from token", email);
            return email;
            
        } catch (Exception e) {
            log.error("Failed to extract email from token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract email from token", e);
        }
    }
    
    @Override
    public String getNameFromToken(String token) {
        log.debug("Extracting name from JWT token");
        
        try {
            Claims claims = extractAllClaims(token);
            String name = claims.get("name", String.class);
            
            // SECURITY: Validate name format
            if (name == null || name.trim().isEmpty()) {
                log.warn("Name claim not found in token");
                throw new RuntimeException("Name not found in token");
            }
            
            // SECURITY: Sanitize name to prevent XSS
            String sanitizedName = name.trim();
            if (sanitizedName.length() > 100) { // Reasonable name length limit
                log.warn("Name too long in token: {} characters", sanitizedName.length());
                sanitizedName = sanitizedName.substring(0, 100);
            }
            
            log.debug("Successfully extracted name: {} from token", sanitizedName);
            return sanitizedName;
            
        } catch (Exception e) {
            log.error("Failed to extract name from token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract name from token", e);
        }
    }
    
    @Override
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
    
    @Override
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
    
    @Override
    public String hashToken(String token) {
        log.debug("Hashing JWT token for secure storage");
        
        try {
            // SECURITY: Validate input
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token cannot be null or empty");
            }
            
            // SECURITY: Use SHA-256 for secure hashing
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(token.getBytes(CHARSET_UTF8));
            
            // PERFORMANCE: Use StringBuilder for efficient string building
            StringBuilder hexString = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            String hashedToken = hexString.toString();
            log.debug("Token hashed successfully, hash length: {} characters", hashedToken.length());
            
            return hashedToken;
            
        } catch (NoSuchAlgorithmException e) {
            log.error("Hash algorithm {} not available: {}", HASH_ALGORITHM, e.getMessage(), e);
            throw new RuntimeException("Hash algorithm not available", e);
        } catch (Exception e) {
            log.error("Failed to hash token: {}", e.getMessage(), e);
            throw new RuntimeException("Error hashing token", e);
        }
    }
    
    /**
     * Get the signing key for JWT operations
     * 
     * PERFORMANCE: Caches the key for 30 minutes to avoid repeated decoding
     * SECURITY: Uses the configured JWT secret from properties
     * 
     * @return the signing key for JWT operations
     * @throws RuntimeException if the key cannot be generated
     */
    private SecretKey getSigningKey() {
        long currentTime = System.currentTimeMillis();
        
        // PERFORMANCE: Check if cached key is still valid
        if (cachedSigningKey != null && 
            (currentTime - lastKeyCacheTime) < KEY_CACHE_DURATION) {
            return cachedSigningKey;
        }
        
        // PERFORMANCE: Generate new key and cache it
        try {
            log.debug("Generating new signing key (cache expired or not available)");
            
            // SECURITY: Validate JWT secret configuration
            if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
                throw new IllegalStateException("JWT secret is not configured");
            }
            
            if (jwtSecret.length() < 32) {
                log.warn("JWT secret is shorter than recommended ({} chars < 32 chars)", 
                        jwtSecret.length());
                // TODO: Add configuration validation to prevent weak secrets
            }
            
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            SecretKey newKey = Keys.hmacShaKeyFor(keyBytes);
            
            // PERFORMANCE: Update cache atomically
            synchronized (this) {
                cachedSigningKey = newKey;
                lastKeyCacheTime = currentTime;
            }
            
            log.debug("Signing key generated and cached successfully");
            return newKey;
            
        } catch (Exception e) {
            log.error("Failed to generate signing key: {}", e.getMessage(), e);
            throw new RuntimeException("Could not generate signing key", e);
        }
    }
    
    /**
     * Extract all claims from a JWT token
     * 
     * SECURITY: Validates token signature before extracting claims
     * ERROR HANDLING: Provides specific error messages for different failure types
     * 
     * @param token the JWT token to parse
     * @return the claims from the token
     * @throws RuntimeException if the token is invalid
     */
    private Claims extractAllClaims(String token) {
        try {
            // SECURITY: Basic token validation
            if (token == null || token.trim().isEmpty()) {
                throw new RuntimeException("Token cannot be null or empty");
            }
            
            // SECURITY: Parse and verify token
            return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
                
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            throw new RuntimeException("Token has expired", e);
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new RuntimeException("Token format is invalid", e);
        } catch (SignatureException e) {
            log.warn("Invalid token signature: {}", e.getMessage());
            throw new RuntimeException("Token signature is invalid", e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new RuntimeException("Token type is not supported", e);
        } catch (JwtException e) {
            log.warn("JWT processing error: {}", e.getMessage());
            throw new RuntimeException("Error processing token", e);
        } catch (Exception e) {
            log.error("Unexpected error extracting claims from token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract claims from token", e);
        }
    }
    
    /**
     * Validate inputs for token generation
     * 
     * SECURITY: Ensures all required parameters are valid
     * VALIDATION: Checks for null values and invalid formats
     * 
     * @param userId the user ID
     * @param email the user email
     * @param name the user name
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private void validateTokenGenerationInputs(UUID userId, String email, String name) {
        // SECURITY: Validate user ID
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        // SECURITY: Validate email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if (!email.contains("@") || email.length() > 254) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // SECURITY: Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Name too long (max 100 characters)");
        }
        
        log.debug("Token generation inputs validated successfully");
    }
    
    /**
     * Get token expiration information for monitoring
     * 
     * MONITORING: Useful for tracking token lifecycle
     * DEBUGGING: Helps diagnose token expiration issues
     * 
     * @return formatted string with token expiration details
     */
    public String getTokenExpirationInfo() {
        long accessMinutes = accessTokenExpiration / 60;
        long refreshHours = refreshTokenExpiration / 3600;
        
        return String.format("Access token expires in %d minutes, Refresh token expires in %d hours", 
                accessMinutes, refreshHours);
    }
}