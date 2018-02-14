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


package io.suricate.monitoring.config.security.ldap;

import io.suricate.monitoring.config.ApplicationProperties;
import io.suricate.monitoring.config.security.ConnectedUser;
import io.suricate.monitoring.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "ldap")
public class LdapAuthentication {

    private final UserRepository userRepository;
    private final ApplicationProperties applicationProperties;
    private final UserDetailsServiceLdapAuthoritiesPopulator userDetailsServiceLdapAuthoritiesPopulator;

    @Autowired
    public LdapAuthentication(UserRepository userRepository, ApplicationProperties applicationProperties, UserDetailsServiceLdapAuthoritiesPopulator userDetailsServiceLdapAuthoritiesPopulator) {
        this.userRepository = userRepository;
        this.applicationProperties = applicationProperties;
        this.userDetailsServiceLdapAuthoritiesPopulator = userDetailsServiceLdapAuthoritiesPopulator;
    }

    /**
     * Method used to check the ldap configuration before launching the Application
     */
    @PostConstruct
    private void checkLdapConfiguration() {
        if (StringUtils.isBlank(applicationProperties.getAuthentication().getLdap().getUrl())) {
            throw new IllegalArgumentException("The Ldap url is mandatory when the provider is ldap");
        }
    }

    /**
     * Method used to configure the ldap
     * @param auth the authentication manager
     * @throws Exception
     */
    @Autowired
    public void configureLdap(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .userDetailsContextMapper(userDetailsContextMapper())
                .ldapAuthoritiesPopulator(userDetailsServiceLdapAuthoritiesPopulator)
                .userSearchFilter(applicationProperties.getAuthentication().getLdap().getUserSearchFilter())
            .contextSource()
            .url(applicationProperties.getAuthentication().getLdap().getUrl());
    }

    /**
     * Method used to store all user Ldap attribute inside the Security context holder
     * @return the userDetails context
     */
    public UserDetailsContextMapper userDetailsContextMapper() {
        return new LdapUserDetailsMapper() {
            @Override
            public UserDetails mapUserFromContext(DirContextOperations ctx, String username, java.util.Collection<? extends GrantedAuthority> authorities) {
                Long userId = userRepository.getIdByUsername(username);
                return new ConnectedUser(username, ctx, authorities, userId, applicationProperties.getAuthentication().getLdap());
            }
        };
    }
}