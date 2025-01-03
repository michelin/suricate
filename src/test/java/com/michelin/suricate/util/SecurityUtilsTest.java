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
