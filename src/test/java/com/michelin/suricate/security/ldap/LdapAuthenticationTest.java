package com.michelin.suricate.security.ldap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.property.ApplicationProperties;
import com.michelin.suricate.service.api.UserService;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
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

        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> mapper.mapUserFromContext(dirContextOperations, "username", Collections.emptyList())
        );

        assertEquals("Bad credentials", exception.getMessage());
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

        assertEquals("username", actual.getUsername());
        assertEquals("password", actual.getPassword());
        assertEquals("ROLE_ADMIN", actual.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList()
            .getFirst());
    }
}
