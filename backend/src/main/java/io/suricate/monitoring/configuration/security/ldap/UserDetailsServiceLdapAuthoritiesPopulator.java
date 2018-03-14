package io.suricate.monitoring.configuration.security.ldap;

import io.suricate.monitoring.configuration.ApplicationProperties;
import io.suricate.monitoring.configuration.security.ConnectedUser;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.service.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "ldap")
public class UserDetailsServiceLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceLdapAuthoritiesPopulator.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public UserDetailsServiceLdapAuthoritiesPopulator(UserRepository userRepository, UserService userService, ApplicationProperties applicationProperties) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.applicationProperties = applicationProperties;
    }

    @Transactional
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
        LOGGER.debug("Authenticating {}", username);
        String lowercaseLogin = username.toLowerCase(Locale.ENGLISH);
        Optional<User> currentUser =  userRepository.findByUsernameIgnoreCase(lowercaseLogin);

        if (!currentUser.isPresent()) {
            // Call service to add user
            currentUser = userService.initUser(new ConnectedUser(lowercaseLogin, userData, applicationProperties.authentication.ldap));
        }

        return currentUser.map(user -> user.getRoles().stream()
            .map( roles -> new SimpleGrantedAuthority(roles.getName()))
            .collect(Collectors.toList()))
            .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not authorized"));
    }
}
