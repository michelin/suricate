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


package io.suricate.monitoring.config.ldap;

import io.suricate.monitoring.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {

    /**
     * Port to expose embedded ldap with apache DS
     */
    public static final int APACHE_DS_PORT = 38654;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private LdapContextSource ldapContextSource;

    @Bean(name = "ldapTemplate")
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(ldapContextSource);
    }

    @Bean(name = "contextSource")
    @ConditionalOnProperty(name = "security.authentication-provider", havingValue = "ldap")
    public LdapContextSource ldapContextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(applicationProperties.getAuthentication().getLdap().getUrl());
        ldapContextSource.setPooled(true);
        ldapContextSource.afterPropertiesSet();
        return ldapContextSource;
    }


    @Bean(name = "contextSource")
    @ConditionalOnProperty(name = "security.authentication-provider", havingValue = "ldif")
    public LdapContextSource ldifContextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl("ldap://localhost:"+APACHE_DS_PORT);
        ldapContextSource.setPooled(true);
        ldapContextSource.afterPropertiesSet();
        return ldapContextSource;
    }
}