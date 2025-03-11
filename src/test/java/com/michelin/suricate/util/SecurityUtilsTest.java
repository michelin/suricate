/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.michelin.suricate.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.enumeration.UserRoleEnum;
import com.michelin.suricate.security.LocalUser;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class SecurityUtilsTest {
    @Test
    void shouldNotBeAdmin() {
        Role role = new Role();
        role.setName(UserRoleEnum.ROLE_USER.name());

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        boolean actual = SecurityUtils.isAdmin(localUser);
        assertFalse(actual);
    }

    @Test
    void shouldBeAdmin() {
        Role role = new Role();
        role.setName(UserRoleEnum.ROLE_ADMIN.name());

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        boolean actual = SecurityUtils.isAdmin(localUser);
        assertTrue(actual);
    }

    @Test
    void shouldHasRoleNullUser() {
        assertFalse(SecurityUtils.hasRole(null, UserRoleEnum.ROLE_USER.name()));
    }

    @Test
    void shouldHasRoleUser() {
        Role role = new Role();
        role.setName(UserRoleEnum.ROLE_USER.name());

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        boolean actual = SecurityUtils.hasRole(localUser, UserRoleEnum.ROLE_USER.name());
        assertTrue(actual);
    }

    @Test
    void shouldHasNotRoleAdmin() {
        Role role = new Role();
        role.setName(UserRoleEnum.ROLE_USER.name());

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        boolean actual = SecurityUtils.hasRole(localUser, UserRoleEnum.ROLE_ADMIN.name());
        assertFalse(actual);
    }
}
