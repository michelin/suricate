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

package io.suricate.monitoring.configuration.security.common;

import io.suricate.monitoring.model.enums.AuthenticationMethod;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Hold the user connected
 *
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConnectedUser extends User {
    /**
     * The ID
     */
    protected Long id;

    /**
     * The firstname
     */
    protected String firstname;

    /**
     * The lastname
     */
    protected String lastname;

    /**
     * The mail
     */
    protected String email;

    /**
     * The authentication method
     */
    protected AuthenticationMethod authenticationMethod;

    /**
     * Constructor
     * @param username The username
     * @param password The password
     * @param enabled Is enabled ?
     * @param accountNonExpired Is account non expired ?
     * @param credentialsNonExpired Is credentials non expired ?
     * @param accountNonLocked Is account non-locked ?
     * @param authorities The authorities
     */
    public ConnectedUser(String username, String password, boolean enabled,
                         boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
                         Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
}
