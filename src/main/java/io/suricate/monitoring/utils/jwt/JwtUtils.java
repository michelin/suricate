package io.suricate.monitoring.utils.jwt;

import io.jsonwebtoken.*;
import io.suricate.monitoring.model.entities.Role;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.properties.ApplicationProperties;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.utils.oauth2.OAuth2Utils;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtUtils {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

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
        //claims.put(Claims.ISSUED_AT, now);
        //claims.put(Claims.EXPIRATION, expiryDate);
        claims.put("username", userPrincipal.getUsername());
        claims.put("firstname", userPrincipal.getUser().getFirstname());
        claims.put("lastname", userPrincipal.getUser().getLastname());
        claims.put("email", userPrincipal.getUser().getEmail());
        claims.put("avatar_url", userPrincipal.getUser().getAvatarUrl());
        claims.put("authorities", userPrincipal.getUser().getRoles().stream().map(Role::getName).collect(Collectors.toList()));

        if (OAuth2Utils.isSocialLogin(userPrincipal.getUser().getAuthenticationMethod())) {
            claims.put("idp", userPrincipal.getUser().getAuthenticationMethod().toString().toLowerCase());
        }

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
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
        Claims claims = Jwts.parser().setSigningKey(applicationProperties.getAuthentication().getJwt().getSigningKey()).parseClaimsJws(token).getBody();

        return claims.getSubject();
    }

    /**
     * Validate a given JWT token
     * @param authToken The token to validate
     * @return true if it is valid, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(applicationProperties.getAuthentication().getJwt().getSigningKey()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty.");
        }
        return false;
    }
}
