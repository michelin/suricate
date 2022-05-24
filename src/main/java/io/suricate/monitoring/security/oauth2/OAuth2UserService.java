package io.suricate.monitoring.security.oauth2;

import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.utils.exceptions.OAuth2AuthenticationProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;

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
            AuthenticationMethod authenticationMethod = Arrays.stream(AuthenticationMethod.values())
                    .filter(authMethod -> userRequest.getClientRegistration().getClientName().equalsIgnoreCase(authMethod.name()))
                    .findAny()
                    .orElseThrow(() -> new OAuth2AuthenticationProcessingException(String.format("ID provider %s is not recognized", userRequest.getClientRegistration().getClientName())));

            String username = null;
            if (AuthenticationMethod.GITHUB.equals(authenticationMethod)) {
                username = user.getAttribute("login").toString().toLowerCase();
            } else if (AuthenticationMethod.GITLAB.equals(authenticationMethod)) {
                username = user.getAttribute("username").toString().toLowerCase();
            }

            String firstname = user.getAttribute("name").toString().split(" ")[0];
            String lastname = user.getAttribute("name").toString().split(" ")[1];
            String email = user.getAttribute("email");

            userService.registerUser(username, firstname, lastname, email, authenticationMethod);

            LOGGER.debug("Authenticated user <{}> with {}", username, userRequest.getClientRegistration().getRegistrationId());

            return user;
        } catch (Exception e) {
            LOGGER.error("An error occurred authenticating user <{}> with {}", user.getAttribute("login"), userRequest.getClientRegistration().getRegistrationId(), e);
            throw new OAuth2AuthenticationProcessingException(e.getMessage(), e.getCause());
        }
    }
}
