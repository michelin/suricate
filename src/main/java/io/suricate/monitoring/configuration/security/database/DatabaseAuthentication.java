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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Database Authentification configurations
 */
@Configuration
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "database")
public class DatabaseAuthentication {

    /**
     * User details service for database connection type
     */
    private final UserDetailsDatabaseService userDetailsDatabaseService;

    /**
     * Password encoder/decoder service
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor
     *
     * @param userDetailsDatabaseService User Details service injection
     * @param passwordEncoder Password encoder injection
     */
    @Autowired
    public DatabaseAuthentication(UserDetailsDatabaseService userDetailsDatabaseService, final PasswordEncoder passwordEncoder) {
        this.userDetailsDatabaseService = userDetailsDatabaseService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Database configuration
     *
     * @param auth The authentication manager
     * @throws Exception When errors occurred while retrieving users
     */
    @Autowired
    public void configureDatabase(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsDatabaseService)
            .passwordEncoder(passwordEncoder);
    }
}
