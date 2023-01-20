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

package io.suricate.monitoring.security.ldap;

import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.AuthenticationProvider;
import io.suricate.monitoring.properties.ApplicationProperties;
import io.suricate.monitoring.services.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "ldap")
public class UserDetailsServiceLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Get authorities for authenticated user
     * @param userData The user data from LDAP
     * @param username The username of the connected user
     * @return The user authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
        log.debug("Authenticating user <{}> with LDAP", username);

        String firstname = userData.getStringAttribute(applicationProperties.getAuthentication().getLdap().getFirstNameAttributeName());
        String lastname = userData.getStringAttribute(applicationProperties.getAuthentication().getLdap().getLastNameAttributeName());
        String email = userData.getStringAttribute(applicationProperties.getAuthentication().getLdap().getMailAttributeName());
        AuthenticationProvider authenticationMethod = AuthenticationProvider.LDAP;

        User registeredUser = userService.registerUser(username, firstname, lastname, email, StringUtils.EMPTY, authenticationMethod);

        return registeredUser.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
