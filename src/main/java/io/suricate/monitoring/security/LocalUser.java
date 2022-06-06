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

package io.suricate.monitoring.security;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Hold the user connected
 *
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class LocalUser extends User implements OAuth2User {
    /**
     * The user
     */
    private io.suricate.monitoring.model.entities.User user;

    /**
     * The OAuth2 attributes
     */
    private Map<String, Object> attributes;

    /**
     * Constructor
     * @param user The user entity
     * @param attributes The OAuth2 attributes
     */
    public LocalUser(io.suricate.monitoring.model.entities.User user, Map<String, Object> attributes) {
        super(user.getUsername(), user.getPassword() == null ? StringUtils.EMPTY : user.getPassword(), true, true, true, true,
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));
        this.user = user;
        this.attributes = attributes;
    }

    /**
     * Get the OAuth2 attributes
     * @return The OAuth2 attributes
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Get the username
     * @return The username
     */
    @Override
    public String getName() {
        return user.getUsername();
    }
}
