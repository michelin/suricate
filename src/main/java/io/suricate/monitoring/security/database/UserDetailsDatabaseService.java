/*
 * Copyright 2012-2021 the original author or authors.
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

package io.suricate.monitoring.security.database;

import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * This service has for role to retrieve users in database when DATABASE Type is selected on properties file
 */
@Service("userDetailsService")
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "database")
public class UserDetailsDatabaseService implements UserDetailsService {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsDatabaseService.class);

    /**
     * The user service
     */
    @Autowired
    private UserService userService;

    /**
     * Load user from database by username
     *
     * @param username The username of the user to search
     * @return The user connected
     * @throws UsernameNotFoundException When no user has been found
     */
    @Override
    public LocalUser loadUserByUsername(String username) {
        LOGGER.debug("Authenticating user <{}> with database", username);

        Optional<User> currentUser = userService.getOneByUsername(username);

        if (!currentUser.isPresent()) {
            throw new UsernameNotFoundException("The specified user has not been found");
        }

        return new LocalUser(currentUser.get(), Collections.emptyMap());
    }
}
