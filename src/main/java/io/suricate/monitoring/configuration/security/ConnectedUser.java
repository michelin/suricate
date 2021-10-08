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

package io.suricate.monitoring.configuration.security;

import io.suricate.monitoring.configuration.ApplicationProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

/**
 * Hold the user connected
 *
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConnectedUser extends User {
    /**
     * The user id
     */
    private Long id;

    /**
     * The user firstname
     */
    private String firstname;

    /**
     * The lastname of the user
     */
    private String lastname;

    /**
     * The mail of the user
     */
    private String mail;

    /**
     * LDAP Constructor using fields
     *
     * @param username user name
     * @param userData user ldap data
     * @param authorities list of user authorities
     * @param id user id
     */
    public ConnectedUser(String username, DirContextOperations userData , Collection<? extends GrantedAuthority> authorities, Long id, ApplicationProperties.Ldap ldapProperties) {
        super(username, "", true, true, true, true, authorities);
        // Add commons ldap field
        if (userData != null) {
            this.firstname = userData.getStringAttribute(ldapProperties.firstNameAttributName);
            this.lastname = userData.getStringAttribute(ldapProperties.lastNameAttributName);
            this.mail = userData.getStringAttribute(ldapProperties.mailAttributName);
        }
        this.id = id;
    }

    /**
     * LDAP constructor with minimal informations
     *
     * @param username The username of the user
     * @param userData The LDAP data
     * @param ldap The LDAP properties form properties file
     */
    public ConnectedUser(String username, DirContextOperations userData, ApplicationProperties.Ldap ldap) {
        this(username, userData, Collections.emptyList(), null, ldap);
    }

    /**
     * Database constructor
     *
     * @param user The user to connect
     * @param authorities The list of authorities for this user
     */
    public ConnectedUser(io.suricate.monitoring.model.entities.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);

        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.mail = user.getEmail();
    }
}
