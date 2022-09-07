package io.suricate.monitoring.security.oauth2;

import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.AuthenticationProvider;
import io.suricate.monitoring.properties.ApplicationProperties;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.utils.exceptions.OAuth2AuthenticationProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Slf4j
@Service
public class OIDCUserService extends OidcUserService {
    /**
     * The user service
     */
    @Autowired
    private UserService userService;

    /**
     * The application properties
     */
    @Autowired
    private ApplicationProperties applicationProperties;

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

            String firstName = null;
            String lastName = null;
            String name = oidcUser.getAttribute("name");
            if (StringUtils.isNotBlank(name)) {
                List<String> splitName = new LinkedList<>(Arrays.asList(name.split(SPACE)));
                int firstNamePosition = 0;
                if (applicationProperties.getAuthentication().getSocialProvidersConfig().containsKey(userRequest.getClientRegistration().getRegistrationId().toLowerCase())
                        && applicationProperties.getAuthentication().getSocialProvidersConfig().get(userRequest.getClientRegistration().getRegistrationId().toLowerCase())
                        .isFirstNameLastNameReverted()) {
                    firstNamePosition = splitName.size() - 1;
                }

                firstName = splitName.get(firstNamePosition);
                splitName.remove(firstNamePosition);
                lastName = String.join(SPACE, splitName);
            }

            String avatarUrl = null;
            if (oidcUser.getAttribute("avatar_url") != null) {
                avatarUrl = oidcUser.getAttribute("avatar_url");
            } else if (oidcUser.getAttribute("picture") != null ) {
                avatarUrl = oidcUser.getAttribute("picture");
            }

            User user = userService.registerUser(username, firstName, lastName, email, avatarUrl, authenticationMethod);

            log.debug("Authenticated user <{}> with {}", username, userRequest.getClientRegistration().getRegistrationId());

            return new LocalUser(user, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
        } catch (Exception e) {
            log.error("An error occurred authenticating user <{}> with {} in OIDC mode", oidcUser.getName(), userRequest.getClientRegistration().getRegistrationId(), e);
            throw new OAuth2AuthenticationProcessingException(e.getMessage(), e.getCause());
        }
    }
}
