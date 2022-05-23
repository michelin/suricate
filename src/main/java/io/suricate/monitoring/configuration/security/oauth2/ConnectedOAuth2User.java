package io.suricate.monitoring.configuration.security.oauth2;

import io.suricate.monitoring.model.entities.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectedOAuth2User implements OAuth2User {
    /**
     * The OAuth2 user
     */
    private final OAuth2User oauth2User;

    /**
     * Constructor
     * @param oauth2User The OAuth2 user
     */
    public ConnectedOAuth2User(OAuth2User oauth2User) {
        this.oauth2User = oauth2User;
    }

    /**
     * Getter for the OAuth2 attributes
     * @return The attributes
     */
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    /**
     * Getter for the authorities
     * @return The authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    /**
     * Getter for the name
     * @return The name
     */
    @Override
    public String getName() {
        return oauth2User.getAttribute("name");
    }
}
