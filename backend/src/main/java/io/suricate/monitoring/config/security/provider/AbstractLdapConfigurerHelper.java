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


package io.suricate.monitoring.config.security.provider;

import io.suricate.monitoring.config.ApplicationProperties;
import io.suricate.monitoring.config.security.ConnectedUser;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.service.ldap.UserDetailsServiceLdapAuthoritiesPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

public abstract class AbstractLdapConfigurerHelper implements SecurityConfigurerHelper {

    @Autowired
    protected ApplicationProperties applicationProperties;

    @Autowired
    protected UserDetailsServiceLdapAuthoritiesPopulator userDetailsServiceLdapAuthoritiesPopulator;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserDetailsContextMapper userDetailsContextMapper;


    /**
     * Method used to store all user Ldap attribute inside the Security context holder
     * @return
     */
    @Bean
    public UserDetailsContextMapper userDetailsContextMapper() {
        return new LdapUserDetailsMapper() {
            @Override
            public UserDetails mapUserFromContext(DirContextOperations ctx, String username, java.util.Collection<? extends GrantedAuthority> authorities) {
                Long id = userRepository.getIdByUsername(username.toLowerCase());
                return new ConnectedUser(username, ctx, authorities, id, applicationProperties.getAuthentication().getLdap());
            }
        };
    }
}
