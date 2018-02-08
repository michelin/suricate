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
import io.suricate.monitoring.config.security.ConnectedUser;
import io.suricate.monitoring.service.ldap.UserDetailsServiceLdapAuthoritiesPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

@Component
public class ConnectedUserAttributesMapper implements AttributesMapper<ConnectedUser> {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private UserDetailsServiceLdapAuthoritiesPopulator userDetailsServiceLdapAuthoritiesPopulator;

    @Override
    public ConnectedUser mapFromAttributes(Attributes attributes) throws NamingException {
        if (attributes == null) {
            return null;
        }
        DirContextAdapter contextAdapter = new DirContextAdapter(attributes, null);
        String uid = attributes.get("uid").get().toString();

        return new ConnectedUser(
                uid,
                contextAdapter,
                userDetailsServiceLdapAuthoritiesPopulator.getGrantedAuthorities(contextAdapter,uid),
                null,
                applicationProperties.getAuthentication().getLdap());
    }
}
