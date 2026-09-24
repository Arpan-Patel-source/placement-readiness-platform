package com.majorproject.backend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for all JWT operations:
 * - Generating tokens from a UserDetails object
 * - Extracting the username (email) from a token
 * - Validating a token against a UserDetails object
 *
 * Uses HMAC-SHA256 (HS256) with a Base64-encoded secret configured in properties.
 */
@Service
public class JwtService {

    @Value("${jwt.secret:dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5MTI=}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /** Extract the subject (email) from the token. */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Generic claim extractor using a resolver function. */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /** Generate a JWT for the given user with no extra claims. */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /** Generate a JWT with additional custom claims. */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /** Returns true if the token belongs to the user and has not expired. */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            // No secret configured — derive a key from application name as a last-resort fallback.
            // In production, always set the jwt.secret environment variable.
            try {
                byte[] derived = MessageDigest.getInstance("SHA-256")
                        .digest("placement-ai-assistant".getBytes(StandardCharsets.UTF_8));
                return Keys.hmacShaKeyFor(derived);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 not available", e);
            }
        }

        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey.trim());
        } catch (Exception ignored) {
            // Not valid Base64 — fall back to raw UTF-8 bytes
            keyBytes = null;
        }

        if (keyBytes == null || keyBytes.length < 32) {
            byte[] rawBytes = secretKey.trim().getBytes(StandardCharsets.UTF_8);
            if (rawBytes.length >= 32) {
                keyBytes = rawBytes;
            } else {
                try {
                    keyBytes = MessageDigest.getInstance("SHA-256").digest(rawBytes);
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException("SHA-256 not available", e);
                }
            }
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
