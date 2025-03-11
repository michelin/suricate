/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.michelin.suricate.security.ldap;

import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.enumeration.AuthenticationProvider;
import com.michelin.suricate.property.ApplicationProperties;
import com.michelin.suricate.service.api.UserService;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

/** User details service for LDAP. */
@Slf4j
@Service
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "ldap")
public class UserDetailsServiceLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Get authorities for authenticated user.
     *
     * @param userData The user data from LDAP
     * @param username The username of the connected user
     * @return The user authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(
            DirContextOperations userData, String username) {
        log.debug("Authenticating user <{}> with LDAP", username);

        String firstname = userData.getStringAttribute(
                applicationProperties.getAuthentication().getLdap().getFirstNameAttributeName());
        String lastname = userData.getStringAttribute(
                applicationProperties.getAuthentication().getLdap().getLastNameAttributeName());
        String email = userData.getStringAttribute(
                applicationProperties.getAuthentication().getLdap().getMailAttributeName());
        AuthenticationProvider authenticationMethod = AuthenticationProvider.LDAP;

        User registeredUser =
                userService.registerUser(username, firstname, lastname, email, StringUtils.EMPTY, authenticationMethod);

        return registeredUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }
}
