package io.suricate.monitoring.security.oauth2;

import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.AuthenticationProvider;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.utils.exceptions.OAuth2AuthenticationProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class OIDCUserService extends OidcUserService {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OIDCUserService.class);

    /**
     * The user service
     */
    @Autowired
    private UserService userService;

    /**
     * Load a user after he has been successfully authenticated with OIDC ID providers
     * @param userRequest The user information
     * @return An OIDC user
     * @throws OAuth2AuthenticationException Any OAuth2 authentication exception
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            AuthenticationProvider authenticationMethod = Arrays.stream(AuthenticationProvider.values())
                    .filter(authMethod -> userRequest.getClientRegistration().getRegistrationId().equalsIgnoreCase(authMethod.name()))
                    .findAny()
                    .orElseThrow(() -> new OAuth2AuthenticationProcessingException(String.format("ID provider %s is not recognized", userRequest.getClientRegistration().getRegistrationId())));

            String username = oidcUser.getAttribute("nickname");
            if (StringUtils.isBlank(username)) {
                throw new OAuth2AuthenticationProcessingException(String.format("Username not found from %s", userRequest.getClientRegistration().getRegistrationId()));
            }

            String email = oidcUser.getAttribute("email");
            if (StringUtils.isBlank(email)) {
                throw new OAuth2AuthenticationProcessingException(String.format("Email not found from %s", userRequest.getClientRegistration().getRegistrationId()));
            }

            String name = oidcUser.getAttribute("name");
            String firstname = null;
            String lastname = null;
            if (StringUtils.isNotBlank(name)) {
                firstname = name.split(StringUtils.SPACE)[0];
                lastname = name.split(StringUtils.SPACE)[1];
            }

            String avatarUrl = null;
            if (oidcUser.getAttribute("avatar_url") != null) {
                avatarUrl = oidcUser.getAttribute("avatar_url");
            } else if (oidcUser.getAttribute("picture") != null ) {
                avatarUrl = oidcUser.getAttribute("picture");
            }

            User user = userService.registerUser(username, firstname, lastname, email, avatarUrl, authenticationMethod);

            LOGGER.debug("Authenticated user <{}> with {}", username, userRequest.getClientRegistration().getRegistrationId());

            return new LocalUser(user, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
        } catch (Exception e) {
            LOGGER.error("An error occurred authenticating user <{}> with {} in OIDC mode", oidcUser.getName(), userRequest.getClientRegistration().getRegistrationId(), e);
            throw new OAuth2AuthenticationProcessingException(e.getMessage(), e.getCause());
        }
    }
}
