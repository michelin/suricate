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

package io.suricate.monitoring.config.security.provider.impl;

import io.suricate.monitoring.config.ldap.LdapConfig;
import io.suricate.monitoring.config.security.provider.AbstractLdapConfigurerHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "security.authentication-provider", havingValue = "ldif")
public class LdifLdapConfigurerHelper extends AbstractLdapConfigurerHelper {
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .userDetailsContextMapper(userDetailsContextMapper)
                .ldapAuthoritiesPopulator(userDetailsServiceLdapAuthoritiesPopulator)
                .userSearchFilter("(&(uid={0})(objectclass=inetOrgPerson))")
                .contextSource()
                .ldif("classpath:/schema.ldif")
                .root("dc=Suricate,ou=FR,ou=ZONE3,o=SURICATE,c=FR")
                .port(LdapConfig.APACHE_DS_PORT);
    }


}
