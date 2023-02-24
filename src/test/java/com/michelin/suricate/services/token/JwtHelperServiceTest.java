package com.michelin.suricate.services.token;

import com.michelin.suricate.properties.ApplicationProperties;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.enums.AuthenticationProvider;
import com.michelin.suricate.model.enums.UserRoleEnum;
import com.michelin.suricate.security.LocalUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtHelperServiceTest {
    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private JwtHelperService jwtHelperService;

    @Test
    void shouldCreateToken() {
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
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };

        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("signingKey");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        String actual = jwtHelperService.createToken(authentication);

        assertThat(actual).startsWith("eyJhbGciOiJIUzUxMiJ9");
    }

    @Test
    void shouldGetUsernameFromToken() {
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
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };

        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("signingKey");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        String actual = jwtHelperService.getUsernameFromToken(jwtHelperService.createToken(authentication));

        assertThat(actual).isEqualTo("username");
    }

    @Test
    void shouldValidateToken() {
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
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };

        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("signingKey");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        boolean actual = jwtHelperService.validateToken(jwtHelperService.createToken(authentication));

        assertThat(actual).isTrue();
    }

    @Test
    void shouldNotValidateTokenBecauseSignatureException() {
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
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };

        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("signingKey");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        ApplicationProperties.Jwt jwtPropertiesForValidation = new ApplicationProperties.Jwt();
        jwtPropertiesForValidation.setTokenValidityMs(60000);
        jwtPropertiesForValidation.setSigningKey("anotherSigningKey");

        ApplicationProperties.Authentication authPropertiesForValidation = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtPropertiesForValidation);

        when(applicationProperties.getAuthentication())
                .thenReturn(authProperties)
                .thenReturn(authProperties)
                .thenReturn(authPropertiesForValidation);

        boolean actual = jwtHelperService.validateToken(jwtHelperService.createToken(authentication));

        assertThat(actual).isFalse();
    }

    @Test
    void shouldNotValidateTokenBecauseExpiredException() {
        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("wrongSigningKey");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        boolean actual = jwtHelperService.validateToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImV4cCI6MTY3NDgyNjI0NSwiaWF0IjoxNjc0ODI2MTg1LCJsYXN0bmFtZSI6bnVsbCwiYXZhdGFyX3VybCI6ImF2YXRhciIsIm1vZGUiOiJHSVRMQUIiLCJmaXJzdG5hbWUiOm51bGwsImVtYWlsIjoiZW1haWwiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXX0.YWwEZvHVstlfZnGddvBKI3aYq2fkNJcIb0MzDTGxRBCHN7xr3U91tmsyjDTRuMdF3IbLUE5A1DyC70JxzSHFTw");

        assertThat(actual).isFalse();
    }

    @Test
    void shouldNotValidateTokenBecauseMalformedJWTException() {
        ApplicationProperties.Jwt jwtProperties = new ApplicationProperties.Jwt();
        jwtProperties.setTokenValidityMs(60000);
        jwtProperties.setSigningKey("wrongSigningKey");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setJwt(jwtProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        boolean actual = jwtHelperService.validateToken("malformedJWT");

        assertThat(actual).isFalse();
    }
}
