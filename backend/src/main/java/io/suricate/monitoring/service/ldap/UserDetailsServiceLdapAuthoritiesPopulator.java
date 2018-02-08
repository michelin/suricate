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

package io.suricate.monitoring.service.ldap;

import io.suricate.monitoring.config.security.token.TokenService;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.repository.RoleRepository;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.utils.ApplicationConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceLdapAuthoritiesPopulator.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TokenService tokenService;

    @Transactional
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
        LOGGER.debug("Authenticating {}", username);
        String lowercaseLogin = username.toLowerCase(Locale.ENGLISH);
        User currentUser =  userRepository.findByUsername(lowercaseLogin);
        if (currentUser == null) {
            currentUser = initUser(lowercaseLogin);
        }
        Optional<User> userFromDatabase = Optional.ofNullable(currentUser);
        return userFromDatabase.map(user -> user.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList()))
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not authorized"));
    }

    /**
     * Method used to create the first admin user in the application
     * @param username username to add
     */
    private User initUser(String username){
        LOGGER.debug("No user found. Add {}", username);
        User user = new User();
        user.setUsername(username);
        user.setToken(tokenService.generateToken());
        user.setRoles(Collections.singletonList(roleRepository.findByName(userRepository.count() == 0 ? ApplicationConstant.ROLE_ADMIN : ApplicationConstant.ROLE_USER)));
        return userRepository.saveAndFlush(user);
    }
}
