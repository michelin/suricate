package io.suricate.monitoring.configuration.security.jwt;

import io.jsonwebtoken.*;
import io.suricate.monitoring.model.entities.Role;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.properties.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
public class TokenProvider {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Build a JWT token
     * @param User The user
     * @return The JWT token
     */
    public String createToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + applicationProperties.getAuthentication().getJwt().getTokenValidityMs());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim(Claims.EXPIRATION, expiryDate)
                .claim(Claims.ISSUED_AT, now)
                .claim(Claims.ISSUER, "toto")
                .claim("username", user.getUsername())
                .claim("firstname", user.getFirstname())
                .claim("lastname", user.getLastname())
                .claim("email", user.getEmail())
                .claim("authorities", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS512, applicationProperties.getAuthentication().getJwt().getSigningKey())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey("!B7\"8_wreS@Vqh)R").parseClaimsJws(token).getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey("!B7\"8_wreS@Vqh)R").parseClaimsJws(authToken);
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
