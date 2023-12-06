package com.michelin.suricate.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.enums.UserRoleEnum;
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
        assertThat(actual).isFalse();
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
        assertThat(actual).isTrue();
    }

    @Test
    void shouldHasRoleNullUser() {
        assertThat(SecurityUtils.hasRole(null, UserRoleEnum.ROLE_USER.name())).isFalse();
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
        assertThat(actual).isTrue();
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
        assertThat(actual).isFalse();
    }
}
