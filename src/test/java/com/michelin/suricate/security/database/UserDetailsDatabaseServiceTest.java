/*
 *
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.michelin.suricate.security.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.UserService;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsDatabaseServiceTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserDetailsDatabaseService userDetailsDatabaseService;

    @Test
    void shouldThrowUsernameNotFound() {
        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsDatabaseService.loadUserByUsername("username"))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("Bad credentials");
    }

    @Test
    void shouldGetLocalUser() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.of(user));

        LocalUser actual = userDetailsDatabaseService.loadUserByUsername("username");

        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().get(0))
            .isEqualTo("ROLE_ADMIN");
    }
}
