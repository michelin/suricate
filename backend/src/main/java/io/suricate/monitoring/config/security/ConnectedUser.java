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

package io.suricate.monitoring.config.security;

import io.suricate.monitoring.config.ApplicationProperties;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class ConnectedUser extends User {

    private Long id;
    private String firstname;
    private String lastname;
    private String fullname;
    private String mail;

    /**
     * Constructor using fields
     * @param username user name
     * @param userData user ldap data
     * @param authorities list of user authorities
     * @param id user id
     */
    public ConnectedUser(String username, DirContextOperations userData , Collection<? extends GrantedAuthority> authorities, Long id, ApplicationProperties.Ldap ldapProperties) {
        super(username, "", true, true, true, true, authorities);
        // Add commons ldap field
        if (userData != null) {
            this.firstname = userData.getStringAttribute(ldapProperties.getFirstNameAttributName());
            this.lastname = userData.getStringAttribute(ldapProperties.getLastNameAttributName());
            this.fullname = this.firstname + " " + this.lastname;
            this.mail = userData.getStringAttribute(ldapProperties.getMailAttributName());
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
}
