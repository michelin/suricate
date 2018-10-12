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
import io.suricate.monitoring.service.api.UserService;
import io.suricate.monitoring.utils.exception.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import javax.annotation.PostConstruct;

/**
 * The LDAP authentication
 */
@Configuration
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "ldap")
public class LdapAuthentication {

    /**
     * The user service
     */
    private final UserService userService;
    private final ApplicationProperties applicationProperties;
    private final UserDetailsServiceLdapAuthoritiesPopulator userDetailsServiceLdapAuthoritiesPopulator;
    private final ApplicationProperties.Ldap ldap;

    @Autowired
    public LdapAuthentication(UserService userService, ApplicationProperties applicationProperties, UserDetailsServiceLdapAuthoritiesPopulator userDetailsServiceLdapAuthoritiesPopulator, @Qualifier("applicationProperties") ApplicationProperties props) {
        this.userService = userService;
        this.applicationProperties = applicationProperties;
        this.userDetailsServiceLdapAuthoritiesPopulator = userDetailsServiceLdapAuthoritiesPopulator;
        this.ldap = props.getAuthentication().ldap;
    }

    /**
     * Method used to check the ldap configuration before launching the Application
     */
    @PostConstruct
    private void checkLdapConfiguration() {
        if (StringUtils.isBlank(applicationProperties.authentication.ldap.url)) {
            throw new ConfigurationException("The Ldap url is mandatory when the provider is ldap", "application.authentication.ldap.url");
        }
    }

    /**
     * Method used to configure the ldap
     *
     * @param auth the authentication manager
     * @throws Exception
     */
    @Autowired
    public void configureLdap(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
            .userDetailsContextMapper(userDetailsContextMapper())
            .ldapAuthoritiesPopulator(userDetailsServiceLdapAuthoritiesPopulator)
            .userSearchFilter(applicationProperties.authentication.ldap.userSearchFilter)
            .userSearchBase(ldap.userSearchBase)
            .contextSource(contextSource());
    }

    /**
     * Method used to store all user Ldap attribute inside the Security context holder
     *
     * @return the userDetails context
     */
    public UserDetailsContextMapper userDetailsContextMapper() {
        return new LdapUserDetailsMapper() {
            @Override
            public UserDetails mapUserFromContext(DirContextOperations ctx, String username, java.util.Collection<? extends GrantedAuthority> authorities) {
                Long userId = userService.getIdByUsername(username);
                return new ConnectedUser(username, ctx, authorities, userId, applicationProperties.authentication.ldap);
            }
        };
    }

    /**
     * Initialize LDAP context
     * @return
     */
    private DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(ldap.url);

        if (!StringUtils.isEmpty(ldap.referral)) { contextSource.setReferral(ldap.referral); }

        if (!StringUtils.isEmpty(ldap.username) && !StringUtils.isEmpty(ldap.password) ) {
            contextSource.setAuthenticationSource(new AuthenticationSource() {
                @Override
                public String getPrincipal() {
                    return ldap.username;
                }

                @Override
                public String getCredentials() {
                    return ldap.password;
                }
            });
        }

        contextSource.setCacheEnvironmentProperties(false);
        return contextSource;
    }

}