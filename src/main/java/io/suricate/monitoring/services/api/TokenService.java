package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Token;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.repositories.TokenRepository;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.utils.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TokenService {
    /**
     * The token repository
     */
    @Autowired
    private TokenRepository tokenRepository;

    /**
     * The JWT utils
     */
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Get all user tokens
     * @param user The user
     * @return The user tokens
     */
    public List<Token> findAllByUser(User user) {
        return tokenRepository.findAllByUser(user);
    }

    /**
     * Create a JWT token
     * @param tokenName The token name
     */
    @Transactional
    public Token create(String tokenName, Authentication authentication) {
        String tokenValue = jwtUtils.createToken(authentication, true);

        Token token = new Token();
        token.setName(tokenName);
        token.setValue(tokenValue);
        token.setUser(((LocalUser) authentication.getPrincipal()).getUser());

        return tokenRepository.save(token);
    }
}
