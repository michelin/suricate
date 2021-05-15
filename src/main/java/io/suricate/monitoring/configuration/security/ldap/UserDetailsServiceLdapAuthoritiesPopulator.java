/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.configuration.security.ldap;

import io.suricate.monitoring.configuration.ApplicationProperties;
import io.suricate.monitoring.configuration.security.ConnectedUser;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.services.api.UserService;
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

import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User details service for LDAP
 */
@Service
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "ldap")
public class UserDetailsServiceLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {
    /**
     * The LDAP
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceLdapAuthoritiesPopulator.class);

    /**
     * The user service
     */
    private final UserService userService;

    /**
     * The application properties (from properties files)
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Constructor
     *
     * @param userService User service
     * @param applicationProperties Application properties
     */
    @Autowired
    public UserDetailsServiceLdapAuthoritiesPopulator(UserService userService, ApplicationProperties applicationProperties) {
        this.userService = userService;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Get authorities for authenticated user
     *
     * @param userData The user data from LDAP
     * @param username The username of the connected user
     * @return The user authorities
     */
    @Transactional
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
        LOGGER.debug("Authenticating {}", username);
        String lowercaseLogin = username.toLowerCase(Locale.ENGLISH);
        Optional<User> currentUser =  userService.getOneByUsername(lowercaseLogin);
        ConnectedUser connectedUser = new ConnectedUser(lowercaseLogin, userData, applicationProperties.authentication.ldap);

        if (!currentUser.isPresent()) {
            // Call service to add user
            currentUser = userService.initUser(connectedUser);
        } else {
            currentUser = userService.updateUserLdapInformation(currentUser.get(), connectedUser);
        }

        return currentUser.map(user -> user.getRoles().stream()
            .map( roles -> new SimpleGrantedAuthority(roles.getName()))
            .collect(Collectors.toList()))
            .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not authorized"));
    }
}
