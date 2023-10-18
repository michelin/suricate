package com.michelin.suricate.services.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.enums.AuthenticationProvider;
import com.michelin.suricate.model.enums.UserRoleEnum;
import com.michelin.suricate.properties.ApplicationProperties;
import com.michelin.suricate.security.LocalUser;
import io.jsonwebtoken.security.WeakKeyException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@ExtendWith(MockitoExtension.class)
class JwtHelperServiceTest {
    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private JwtHelperService jwtHelperService;

    @Test
    void shouldThrowWeakKeyExceptionWhenCreatingToken() {
        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("weakKey");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("key", "value");

                Role role = new Role();
                role.setId(1L);
                role.setName(UserRoleEnum.ROLE_USER.name());

                User user = new User();
                user.setId(1L);
                user.setUsername("username");
                user.setPassword("password");
                user.setEmail("email");
                user.setAvatarUrl("avatar");
                user.setRoles(Collections.singleton(role));
                user.setAuthenticationMethod(AuthenticationProvider.GITLAB);

                return new LocalUser(user, attributes);
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        };

        assertThatThrownBy(() -> jwtHelperService.createToken(authentication))
            .isInstanceOf(WeakKeyException.class)
            .hasMessage("The specified key byte array is 56 bits which "
                + "is not secure enough for any JWT HMAC-SHA algorithm.  "
                + "The JWT JWA Specification (RFC 7518, Section 3.2) states "
                + "that keys used with HMAC-SHA algorithms MUST have a size >= 256 bits "
                + "(the key size must be greater than or equal to the hash output size).  "
                + "Consider using the Jwts.SIG.HS256.key() builder (or HS384.key() or HS512.key()) "
                + "to create a key guaranteed to be secure enough for your preferred HMAC-SHA algorithm.  "
                + "See https://tools.ietf.org/html/rfc7518#section-3.2 for more information.");
    }

    @Test
    void shouldCreateToken() {
        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(100);
        jwtProperties.setSigningKey("changeitchangeitchangeitchangeit");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("key", "value");

                Role role = new Role();
                role.setId(1L);
                role.setName(UserRoleEnum.ROLE_USER.name());

                User user = new User();
                user.setId(1L);
                user.setUsername("username");
                user.setPassword("password");
                user.setEmail("email");
                user.setAvatarUrl("avatar");
                user.setRoles(Collections.singleton(role));
                user.setAuthenticationMethod(AuthenticationProvider.GITLAB);

                return new LocalUser(user, attributes);
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        };

        String actual = jwtHelperService.createToken(authentication);

        assertThat(actual).startsWith("eyJhbGciOiJIUzI1NiJ9");
    }

    @Test
    void shouldGetUsernameFromToken() {
        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("changeitchangeitchangeitchangeit");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("key", "value");

                Role role = new Role();
                role.setId(1L);
                role.setName(UserRoleEnum.ROLE_USER.name());

                User user = new User();
                user.setId(1L);
                user.setUsername("username");
                user.setPassword("password");
                user.setEmail("email");
                user.setAvatarUrl("avatar");
                user.setRoles(Collections.singleton(role));
                user.setAuthenticationMethod(AuthenticationProvider.GITLAB);

                return new LocalUser(user, attributes);
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        };

        String actual = jwtHelperService.getUsernameFromToken(jwtHelperService.createToken(authentication));

        assertThat(actual).isEqualTo("username");
    }

    @Test
    void shouldValidateToken() {
        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("changeitchangeitchangeitchangeit");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("key", "value");

                Role role = new Role();
                role.setId(1L);
                role.setName(UserRoleEnum.ROLE_USER.name());

                User user = new User();
                user.setId(1L);
                user.setUsername("username");
                user.setPassword("password");
                user.setEmail("email");
                user.setAvatarUrl("avatar");
                user.setRoles(Collections.singleton(role));
                user.setAuthenticationMethod(AuthenticationProvider.GITLAB);

                return new LocalUser(user, attributes);
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        };

        boolean actual = jwtHelperService.validateToken(jwtHelperService.createToken(authentication));

        assertThat(actual).isTrue();
    }

    @Test
    void shouldNotValidateTokenBecauseSignatureException() {
        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("changeitchangeitchangeitchangeit");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        ApplicationProperties.Jwt jwtPropertiesForValidation = new ApplicationProperties.Jwt();
        jwtPropertiesForValidation.setTokenValidityMs(60000);
        jwtPropertiesForValidation.setSigningKey("otherotherotherotherotherotherot");

        ApplicationProperties.Authentication authPropertiesForValidation = new ApplicationProperties.Authentication();
        authPropertiesForValidation.setJwt(jwtPropertiesForValidation);

        when(applicationProperties.getAuthentication())
            .thenReturn(authProperties)
            .thenReturn(authProperties)
            .thenReturn(authPropertiesForValidation);

        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("key", "value");

                Role role = new Role();
                role.setId(1L);
                role.setName(UserRoleEnum.ROLE_USER.name());

                User user = new User();
                user.setId(1L);
                user.setUsername("username");
                user.setPassword("password");
                user.setEmail("email");
                user.setAvatarUrl("avatar");
                user.setRoles(Collections.singleton(role));
                user.setAuthenticationMethod(AuthenticationProvider.GITLAB);

                return new LocalUser(user, attributes);
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        };

        boolean actual = jwtHelperService.validateToken(jwtHelperService.createToken(authentication));

        assertThat(actual).isFalse();
    }

    @Test
    void shouldNotValidateTokenBecauseExpiredException() {
        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("changeitchangeitchangeitchangeit");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        boolean actual = jwtHelperService.validateToken(
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImV4cCI"
                + "6MTY5NzQ5NTI1NCwiaWF0IjoxNjk3NDk1MjU0LCJtb2RlIjoiR0lUTEFCIi"
                + "wiYXZhdGFyX3VybCI6ImF2YXRhciIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLC"
                + "JlbWFpbCI6ImVtYWlsIn0.hWX18w6n-_4LDN66iSv1t5itKHd-0QcZQdB1BADvObE");

        assertThat(actual).isFalse();
    }

    @Test
    void shouldNotValidateTokenBecauseMalformedJwtException() {
        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("changeitchangeitchangeitchangeit");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        boolean actual = jwtHelperService.validateToken("malformedJWT");

        assertThat(actual).isFalse();
    }
}
