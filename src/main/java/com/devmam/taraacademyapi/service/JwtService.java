package com.devmam.taraacademyapi.service;

import com.nimbusds.jwt.JWTClaimsSet;

import java.util.Set;

public interface JwtService {
    String buildScope(Set<String> roles);

    String generateToken(String id, String email, Set<String> roles, String userAgent);

    Long getUserId(String token);

    String getKeycloakUserIdFromToken(String token);

    String getCurrentUserId();

    JWTClaimsSet getClaimsFromToken(String token);

    String getTokenFromAuthHeader(String authHeader);
}
