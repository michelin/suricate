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

package com.michelin.suricate.security.database;

import com.michelin.suricate.services.api.UserService;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.security.LocalUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service("userDetailsService")
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "database")
public class UserDetailsDatabaseService implements UserDetailsService {
    @Autowired
    private UserService userService;

    /**
     * Load user from database by username
     * @param username The username of the user to search
     * @return The user connected
     * @throws UsernameNotFoundException When no user has been found
     */
    @Override
    public LocalUser loadUserByUsername(String username) {
        log.debug("Authenticating user <{}> with database", username);

        Optional<User> currentUser = userService.getOneByUsername(username);

        if (!currentUser.isPresent()) {
            throw new UsernameNotFoundException("Bad credentials");
        }

        return new LocalUser(currentUser.get(), Collections.emptyMap());
    }
}
