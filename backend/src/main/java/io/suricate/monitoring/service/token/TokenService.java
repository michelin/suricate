package io.suricate.monitoring.service.token;

import io.jsonwebtoken.*;
import io.suricate.monitoring.config.ApplicationProperties;
import io.suricate.monitoring.config.security.ConnectedUser;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.service.UserService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    private static final String AUTHORITIES_KEY = "auth";
    private static final String FIRSTNAME_KEY = "firstName";
    private static final String LASTNAME_KEY = "lastName";
    private static final String DATABASE_KEY = "dbCheck";

    /**
     * Secret key used to sign the jwt token
     */
    private String secretKey;

    /**
     * Token validity in milliseconds
     */
    private long tokenValidityInMilliseconds;

    /**
     * Token validity in millisecond when remember me is checked
     */
    private long tokenValidityInMillisecondsForRememberMe;

    /**
     * Global application properties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * User Service
     */
    private final UserService userService;

    /**
     * Service constructor
     * @param applicationProperties global properties configuration
     */
    @Autowired
    public TokenService(ApplicationProperties applicationProperties, UserService userService) {
        this.applicationProperties = applicationProperties;
        this.userService = userService;
    }

    /**
     * Load configuration for config file
     */
    @PostConstruct
    public void init() {
        this.secretKey = applicationProperties.authentication.jwt.secret;
        this.tokenValidityInMilliseconds = DateUtils.MILLIS_PER_SECOND * applicationProperties.authentication.jwt.tokenValidity;
        this.tokenValidityInMillisecondsForRememberMe = DateUtils.MILLIS_PER_SECOND * applicationProperties.authentication.jwt.tokenValidityRememberMe;
    }

    /**
     * Method used to create a new token
     * @param authentication the user authentication
     * @param rememberMe if the user check remember me
     * @param databaseCheck indicate if we need to check the token in database
     * @return the jwt token
     */
    public String createToken(Authentication authentication, Boolean rememberMe, boolean databaseCheck) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        ConnectedUser connectedUser = (ConnectedUser) authentication.getPrincipal();

        return Jwts.builder()
            .setSubject(authentication.getName())
            .setIssuedAt(new Date())
            .claim(AUTHORITIES_KEY, authorities)
            .claim(DATABASE_KEY, databaseCheck)
            .claim(LASTNAME_KEY, connectedUser.getLastname())
            .claim(FIRSTNAME_KEY, connectedUser.getFirstname())
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setExpiration(validity)
            .compact();
    }

    /**
     * Get authentication from a token
     * @param token the user token
     * @return the authentication object
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Optional<User> userOptional = userService.getByUsername(claims.getSubject());

        if(userOptional.isPresent()) {
            ConnectedUser principal = new ConnectedUser(claims.getSubject(), null, authorities, userOptional.get().getId(), null);
            principal.setFirstname((String) claims.get(FIRSTNAME_KEY));
            principal.setLastname((String) claims.get(LASTNAME_KEY));

            if(!token.equals(userOptional.get().getToken())) {
                //TODO: Throw error
            }

            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        }

        return null;
    }


    /**
     * Method used to validate a Jwt token
     * @param authToken the jwt tocken to validate
     * @return true if the token is valid false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.info("Invalid JWT signature.");
            LOGGER.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            LOGGER.info("Invalid JWT token.");
            LOGGER.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            LOGGER.info("Expired JWT token.");
            LOGGER.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            LOGGER.info("Unsupported JWT token.");
            LOGGER.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            LOGGER.info("JWT token compact of handler are invalid.");
            LOGGER.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }

}
