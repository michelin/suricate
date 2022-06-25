package io.suricate.monitoring.utils.oauth2;

import io.suricate.monitoring.model.enums.AuthenticationProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class OAuth2Utils {
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

    /**
     * Constructor
     */
    private OAuth2Utils() { }
}
