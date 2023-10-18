package com.michelin.suricate.security.oauth2;

import static org.apache.commons.lang3.StringUtils.SPACE;

import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.enums.AuthenticationProvider;
import com.michelin.suricate.properties.ApplicationProperties;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.UserService;
import com.michelin.suricate.utils.exceptions.Oauth2AuthenticationProcessingException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * OAuth2 user service.
 */
@Slf4j
@Service
public class Oauth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Load a user after he has been successfully authenticated with OAuth2 ID providers.
     *
     * @param userRequest The user information
     * @return An OAuth2 user
     * @throws OAuth2AuthenticationException Any OAuth2 authentication exception
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            Optional<AuthenticationProvider> authenticationMethod = Arrays.stream(AuthenticationProvider.values())
                .filter(authMethod -> userRequest.getClientRegistration().getRegistrationId()
                    .equalsIgnoreCase(authMethod.name()))
                .findAny();

            if (authenticationMethod.isEmpty()) {
                throw new Oauth2AuthenticationProcessingException(
                    String.format("ID provider %s is not recognized",
                        userRequest.getClientRegistration().getRegistrationId()));
            }

            String username = null;
            if (oauth2User.getAttribute("login") != null) {
                username = oauth2User.getAttribute("login");
            } else if (oauth2User.getAttribute("username") != null) {
                username = oauth2User.getAttribute("username");
            }

            if (StringUtils.isBlank(username)) {
                throw new Oauth2AuthenticationProcessingException(String.format("Username not found from %s",
                    userRequest.getClientRegistration().getRegistrationId()));
            }

            String email = oauth2User.getAttribute("email");
            if (StringUtils.isBlank(email)) {
                throw new Oauth2AuthenticationProcessingException(
                    String.format("Email not found from %s", StringUtils.capitalize(userRequest.getClientRegistration()
                        .getRegistrationId())));
            }

            String firstName = null;
            String lastName = null;
            String name = oauth2User.getAttribute("name");
            if (StringUtils.isNotBlank(name)) {
                List<String> splitName = new LinkedList<>(Arrays.asList(name.split(SPACE)));
                if (applicationProperties.getAuthentication().getSocialProvidersConfig()
                    .containsKey(userRequest.getClientRegistration().getRegistrationId().toLowerCase())
                    && applicationProperties.getAuthentication().getSocialProvidersConfig()
                    .get(userRequest.getClientRegistration().getRegistrationId().toLowerCase())
                    .isNameCaseParse()) {
                    firstName = splitName.stream().filter(word -> !word.equals(word.toUpperCase()))
                        .collect(Collectors.joining(SPACE));
                    lastName = splitName.stream().filter(word -> word.equals(word.toUpperCase()))
                        .collect(Collectors.joining(SPACE));
                } else {
                    firstName = splitName.get(0);
                    lastName = String.join(SPACE, splitName.subList(1, splitName.size()));
                }
            }

            String avatarUrl = null;
            if (oauth2User.getAttribute("avatar_url") != null) {
                avatarUrl = oauth2User.getAttribute("avatar_url");
            } else if (oauth2User.getAttribute("picture") != null) {
                avatarUrl = oauth2User.getAttribute("picture");
            }

            User user = userService.registerUser(username, firstName, lastName, email, avatarUrl,
                authenticationMethod.get());

            log.debug("Authenticated user <{}> with {}", username,
                userRequest.getClientRegistration().getRegistrationId());

            return new LocalUser(user, oauth2User.getAttributes());
        } catch (Exception e) {
            log.error("An error occurred authenticating user <{}> with {} in OAuth2 mode", oauth2User.getName(),
                userRequest.getClientRegistration().getRegistrationId(), e);
            throw new Oauth2AuthenticationProcessingException(e.getMessage(), e.getCause());
        }
    }
}
