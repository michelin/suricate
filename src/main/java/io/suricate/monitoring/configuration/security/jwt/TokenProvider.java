package io.suricate.monitoring.configuration.security.jwt;

import io.jsonwebtoken.*;
import io.suricate.monitoring.configuration.security.common.ConnectedUser;
import io.suricate.monitoring.configuration.security.ldap.UserDetailsServiceLdapAuthoritiesPopulator;
import io.suricate.monitoring.configuration.security.oauth2.ConnectedOAuth2User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenProvider {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

    public String createToken(Authentication authentication) {
        ConnectedOAuth2User userPrincipal = (ConnectedOAuth2User) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 864000000);

        return Jwts.builder().setSubject(userPrincipal.getName()).setIssuedAt(new Date()).setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, "!B7\"8_wreS@Vqh)R").compact();
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
