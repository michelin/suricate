package io.suricate.monitoring.utils.oauth2;

import io.suricate.monitoring.model.enums.AuthenticationMethod;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuth2Utils {
    /**
     * From a given OAuth2 user and an authentication method, extract the username
     * @param user The OAuth2 user
     * @param provider The authentication method
     * @return The username
     */
    public static String extractUsername(OAuth2User user, AuthenticationMethod provider) {
        String username = null;

        if (AuthenticationMethod.GITHUB.equals(provider)) {
            username = user.getAttribute("login").toString().toLowerCase();
        } else if (AuthenticationMethod.GITLAB.equals(provider)) {
            username = user.getAttribute("username").toString().toLowerCase();
        }

        return username;
    }
}
