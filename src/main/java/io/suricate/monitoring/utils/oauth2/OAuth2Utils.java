package io.suricate.monitoring.utils.oauth2;

import io.suricate.monitoring.model.enums.AuthenticationProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Arrays;
import java.util.stream.Collectors;

public class OAuth2Utils {
    /**
     * From a given OAuth2 user and an authentication method, extract the username
     * @param user The OAuth2 user
     * @param provider The authentication method
     * @return The username
     */
    public static String extractUsername(OAuth2User user, AuthenticationProvider provider) {
        String username = null;

        if (AuthenticationProvider.GITHUB.equals(provider)) {
            username = user.getAttribute("login").toString().toLowerCase();
        } else if (AuthenticationProvider.GITLAB.equals(provider)) {
            username = user.getAttribute("username").toString().toLowerCase();
        }

        return username;
    }

    /**
     * Check if a given authentication method is a social login method
     * @param authenticationMethod The authentication method to check
     * @return true if it is, false otherwise
     */
    public static boolean isSocialLogin(AuthenticationProvider authenticationMethod) {
        return Arrays.stream(AuthenticationProvider.values())
                .filter(method -> !Arrays.asList(AuthenticationProvider.LDAP, AuthenticationProvider.DATABASE).contains(method))
                .collect(Collectors.toList())
                .contains(authenticationMethod);
    }
}
