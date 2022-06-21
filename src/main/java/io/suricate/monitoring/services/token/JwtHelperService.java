package io.suricate.monitoring.services.token;

import io.jsonwebtoken.*;
import io.suricate.monitoring.model.entities.Role;
import io.suricate.monitoring.properties.ApplicationProperties;
import io.suricate.monitoring.security.LocalUser;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtHelperService {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtHelperService.class);

    /**
     * The application properties
     */
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
            LOGGER.error("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            LOGGER.error("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty", e);
        } catch (Exception e) {
            LOGGER.error("Error reading public key", e);
        }
        return false;
    }
}
