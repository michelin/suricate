package io.suricate.monitoring.configuration.security.oauth2;

import io.suricate.monitoring.configuration.security.common.ConnectedUser;
import io.suricate.monitoring.configuration.security.ldap.LdapConnectedUser;
import io.suricate.monitoring.services.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2UserService.class);

    /**
     * The user service
     */
    @Autowired
    private UserService userService;

    /**
     * Load a user after he has been successfully authenticated with OAuth2 ID providers
     * @param userRequest The user information
     * @return An OAuth2 user
     * @throws OAuth2AuthenticationException Any OAuth2 authentication exception
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            LOGGER.debug("Authenticating user <{}> with {}", user.getAttribute("login"), userRequest.getClientRegistration().getRegistrationId());

            ConnectedUser connectedUser = new GitHubConnectedUser(user);
            userService.registerUser(connectedUser);

            return new ConnectedOAuth2User(user);
        } catch (Exception e) {
            e.printStackTrace();
            // Throwing an instance of AuthenticationException will trigger the
            // OAuth2AuthenticationFailureHandler
            //throw new OAuth2AuthenticationProcessingException(ex.getMessage(), ex.getCause());
        }

        return user;
    }
}
