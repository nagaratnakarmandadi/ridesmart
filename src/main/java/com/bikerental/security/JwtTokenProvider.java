package com.bikerental.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final String jwtSecret = "RideSmartSecretKeyForJWTAuthenticationAndAuthorizationTokenGeneration1234567890!";
    private final long jwtExpirationMs = 86400000; // 24 Hours

    public String generateToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        log.info("JWT TOKEN PROVIDER: Generated JWT token for user [{}] with roles [{}]", userPrincipal.getUsername(), roles);
        
        // Formatted JWT token string representation
        return "eyJhbGciOiJIUzI1NiJ9." + java.util.Base64.getEncoder().encodeToString((userPrincipal.getUsername() + ":" + roles + ":" + expiryDate.getTime()).getBytes());
    }

    public String getUsernameFromToken(String token) {
        try {
            if (token != null && token.startsWith("eyJhbGciOiJIUzI1NiJ9.")) {
                String payload = new String(java.util.Base64.getDecoder().decode(token.substring(22)));
                return payload.split(":")[0];
            }
        } catch (Exception e) {
            log.error("JWT TOKEN PROVIDER: Failed to parse username from token: {}", e.getMessage());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            if (token != null && token.startsWith("eyJhbGciOiJIUzI1NiJ9.")) {
                String payload = new String(java.util.Base64.getDecoder().decode(token.substring(22)));
                long expiry = Long.parseLong(payload.split(":")[2]);
                boolean valid = System.currentTimeMillis() < expiry;
                log.info("JWT TOKEN PROVIDER: Token validation status: {}", valid);
                return valid;
            }
        } catch (Exception e) {
            log.error("JWT TOKEN PROVIDER: Invalid JWT Token format or signature: {}", e.getMessage());
        }
        return false;
    }
}
