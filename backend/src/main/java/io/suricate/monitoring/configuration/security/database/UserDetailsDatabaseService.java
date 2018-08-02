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

package io.suricate.monitoring.configuration.security.database;

import io.suricate.monitoring.configuration.security.ConnectedUser;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This service has for role to retrieve users in database when DATABASE Type is selected on properties file
 */
@Service("userDetailsService")
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "database")
public class UserDetailsDatabaseService implements UserDetailsService {

    /**
     * The user service
     */
    private final UserService userService;

    /**
     * Constructor
     *
     * @param userService The user service
     */
    @Autowired
    public UserDetailsDatabaseService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Load user from database by username
     *
     * @param username The username of the user to search
     * @return The user connected
     * @throws UsernameNotFoundException When no user has been found
     */
    @Override
    @Transactional
    public ConnectedUser loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> currentUser = userService.getOneByUsername(username);

        if(!currentUser.isPresent()) {
            throw new UsernameNotFoundException("The specified user has not been found");
        }

        Collection<? extends GrantedAuthority> authorities = currentUser
                                                                    .map(user -> user.getRoles().stream()
                                                                        .map( roles -> new SimpleGrantedAuthority(roles.getName()))
                                                                        .collect(Collectors.toList()))
                                                                    .orElseThrow(() -> new UsernameNotFoundException("User " + username + " was not authorized"));

        return new ConnectedUser(currentUser.get(), authorities);
    }
}
