package com.michelin.suricate.security.oauth2;

import com.michelin.suricate.properties.ApplicationProperties;
import com.michelin.suricate.services.api.UserService;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.enums.AuthenticationProvider;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.utils.exceptions.OAuth2AuthenticationProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
                if (applicationProperties.getAuthentication().getSocialProvidersConfig().containsKey(userRequest.getClientRegistration().getRegistrationId().toLowerCase())
                        && applicationProperties.getAuthentication().getSocialProvidersConfig().get(userRequest.getClientRegistration().getRegistrationId().toLowerCase())
                        .isNameCaseParse()) {
                    firstName = splitName.stream().filter(word -> !word.equals(word.toUpperCase())).collect(Collectors.joining(SPACE));
                    lastName = splitName.stream().filter(word -> word.equals(word.toUpperCase())).collect(Collectors.joining(SPACE));
                } else {
                    firstName = splitName.get(0);
                    lastName = String.join(SPACE, splitName.subList(1, splitName.size()));
                }
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
