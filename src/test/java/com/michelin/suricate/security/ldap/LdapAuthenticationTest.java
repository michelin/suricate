package com.michelin.suricate.security.ldap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.properties.ApplicationProperties;
import com.michelin.suricate.services.api.UserService;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

@ExtendWith(MockitoExtension.class)
class LdapAuthenticationTest {
    @Mock
    private UserService userService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private DirContextOperations dirContextOperations;

    @InjectMocks
    private LdapAuthentication ldapAuthentication;

    @Test
    void shouldThrowUsernameNotFound() {
        ApplicationProperties.Ldap ldapProperties = new ApplicationProperties.Ldap();
        ldapProperties.setUrl("ldapUrl");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setLdap(ldapProperties);

        when(userService.getOneByEmail(any()))
            .thenReturn(Optional.empty());
        when(applicationProperties.getAuthentication())
            .thenReturn(authProperties);
        when(dirContextOperations.getStringAttribute(any()))
            .thenReturn("email");

        UserDetailsContextMapper mapper = ldapAuthentication.userDetailsContextMapper();

        assertThatThrownBy(() -> mapper.mapUserFromContext(dirContextOperations, "username", Collections.emptyList()))
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

        ApplicationProperties.Ldap ldapProperties = new ApplicationProperties.Ldap();
        ldapProperties.setUrl("ldapUrl");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setLdap(ldapProperties);

        when(userService.getOneByEmail(any()))
            .thenReturn(Optional.of(user));
        when(applicationProperties.getAuthentication())
            .thenReturn(authProperties);
        when(dirContextOperations.getStringAttribute(any()))
            .thenReturn("email");

        UserDetailsContextMapper mapper = ldapAuthentication.userDetailsContextMapper();
        UserDetails actual = mapper.mapUserFromContext(dirContextOperations, "username", Collections.emptyList());

        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(Lists.newArrayList(actual.getAuthorities()).get(0).getAuthority()).isEqualTo("ROLE_ADMIN");
    }
}
