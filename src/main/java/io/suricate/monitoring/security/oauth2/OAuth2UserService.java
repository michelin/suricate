package io.suricate.monitoring.security.oauth2;

import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.AuthenticationProvider;
import io.suricate.monitoring.properties.ApplicationProperties;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.utils.exceptions.OAuth2AuthenticationProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.SPACE;

@Slf4j
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
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
     * Load a user after he has been successfully authenticated with OAuth2 ID providers
     * @param userRequest The user information
     * @return An OAuth2 user
     * @throws OAuth2AuthenticationException Any OAuth2 authentication exception
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            AuthenticationProvider authenticationMethod = Arrays.stream(AuthenticationProvider.values())
                    .filter(authMethod -> userRequest.getClientRegistration().getRegistrationId().equalsIgnoreCase(authMethod.name()))
                    .findAny()
                    .orElseThrow(() -> new OAuth2AuthenticationProcessingException(String.format("ID provider %s is not recognized", userRequest.getClientRegistration().getRegistrationId())));

            String username = null;
            if (oAuth2User.getAttribute("login") != null) {
                username = oAuth2User.getAttribute("login");
            } else if (oAuth2User.getAttribute("username") != null ) {
                username = oAuth2User.getAttribute("username");
            }

            if (StringUtils.isBlank(username)) {
                throw new OAuth2AuthenticationProcessingException(String.format("Username not found from %s", userRequest.getClientRegistration().getRegistrationId()));
            }

            String email = oAuth2User.getAttribute("email");
            if (StringUtils.isBlank(email)) {
                throw new OAuth2AuthenticationProcessingException(String.format("Email not found from %s", userRequest.getClientRegistration().getRegistrationId()));
            }

            String firstName = null;
            String lastName = null;
            String name = oAuth2User.getAttribute("name");
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
            if (oAuth2User.getAttribute("avatar_url") != null) {
                avatarUrl = oAuth2User.getAttribute("avatar_url");
            } else if (oAuth2User.getAttribute("picture") != null ) {
                avatarUrl = oAuth2User.getAttribute("picture");
            }

            User user = userService.registerUser(username, firstName, lastName, email, avatarUrl, authenticationMethod);

            log.debug("Authenticated user <{}> with {}", username, userRequest.getClientRegistration().getRegistrationId());

            return new LocalUser(user, oAuth2User.getAttributes());
        } catch (Exception e) {
            log.error("An error occurred authenticating user <{}> with {} in OAuth2 mode", oAuth2User.getName(), userRequest.getClientRegistration().getRegistrationId(), e);
            throw new OAuth2AuthenticationProcessingException(e.getMessage(), e.getCause());
        }
    }
}
