package com.michelin.suricate.services.token;

import com.michelin.suricate.properties.ApplicationProperties;
import io.jsonwebtoken.*;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.security.LocalUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtHelperService {
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Build a JWT token
     * @param authentication The authentication information
     * @return The JWT token
     */
    public String createToken(Authentication authentication) {
        LocalUser userPrincipal = (LocalUser) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + applicationProperties.getAuthentication().getJwt().getTokenValidityMs());

        Map<String, Object> claims = new HashedMap<>();
        claims.put("firstname", userPrincipal.getUser().getFirstname());
        claims.put("lastname", userPrincipal.getUser().getLastname());
        claims.put("email", userPrincipal.getUser().getEmail());
        claims.put("avatar_url", userPrincipal.getUser().getAvatarUrl());
        claims.put("authorities", userPrincipal.getUser().getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        claims.put("mode", userPrincipal.getUser().getAuthenticationMethod());

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setExpiration(expiryDate)
                .setIssuedAt(now)
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS512, applicationProperties.getAuthentication().getJwt().getSigningKey())
                .compact();
    }

    /**
     * Extract the username from the given token
     * @param token The token
     * @return The username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(applicationProperties.getAuthentication().getJwt().getSigningKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validate a given JWT token
     * @param authToken The token to validate
     * @return true if it is valid, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(applicationProperties.getAuthentication().getJwt().getSigningKey())
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
        } catch (Exception e) {
            log.error("Error reading public key", e);
        }
        return false;
    }
}
