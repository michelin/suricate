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

package com.michelin.suricate.security.oauth2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.enumeration.AuthenticationProvider;
import com.michelin.suricate.property.ApplicationProperties;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.api.UserService;
import com.michelin.suricate.util.exception.Oauth2AuthenticationProcessingException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;

@ExtendWith(MockitoExtension.class)
class OpenIdcUserServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private Oauth2UserService oauth2UserService;

    @InjectMocks
    private OpenIdcUserService openIdcUserService;

    @Test
    void shouldNotLoadUserWhenIdProviderNotRecognized() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "myUsername");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));
        LocalUser localUser = new LocalUser(user, claims);

        when(oauth2UserService.loadUser(any()))
            .thenReturn(localUser);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("unknownIDP")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("localhost:8080")
            .authorizationUri("localhost:8080/authorizationUri")
            .tokenUri("localhost:8080/tokenUri")
            .userInfoUri("localhost:8080/userInfoUri")
            .userNameAttributeName("username")
            .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "token",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z")
        );

        OidcIdToken oidcToken = new OidcIdToken(
            "oidcToken",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z"),
            claims
        );

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

        Oauth2AuthenticationProcessingException exception = assertThrows(
            Oauth2AuthenticationProcessingException.class,
            () -> openIdcUserService.loadUser(request)
        );

        assertEquals("ID provider unknownIDP is not recognized", exception.getMessage());
    }

    @Test
    void shouldNotLoadUserWhenUsernameBlank() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "   ");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));
        LocalUser localUser = new LocalUser(user, claims);

        when(oauth2UserService.loadUser(any()))
            .thenReturn(localUser);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("gitlab")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("localhost:8080")
            .authorizationUri("localhost:8080/authorizationUri")
            .tokenUri("localhost:8080/tokenUri")
            .userInfoUri("localhost:8080/userInfoUri")
            .userNameAttributeName("username")
            .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "token",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z")
        );

        OidcIdToken oidcToken = new OidcIdToken(
            "oidcToken",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z"),
            claims
        );

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

        Oauth2AuthenticationProcessingException exception = assertThrows(
            Oauth2AuthenticationProcessingException.class,
            () -> openIdcUserService.loadUser(request)
        );

        assertEquals("Username not found from gitlab", exception.getMessage());
    }

    @Test
    void shouldNotLoadUserWhenEmailBlank() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "myUsername");
        claims.put("email", "   ");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));
        LocalUser localUser = new LocalUser(user, claims);

        when(oauth2UserService.loadUser(any()))
            .thenReturn(localUser);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("gitlab")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("localhost:8080")
            .authorizationUri("localhost:8080/authorizationUri")
            .tokenUri("localhost:8080/tokenUri")
            .userInfoUri("localhost:8080/userInfoUri")
            .userNameAttributeName("username")
            .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "token",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z")
        );

        OidcIdToken oidcToken = new OidcIdToken(
            "oidcToken",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z"),
            claims
        );

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

        Oauth2AuthenticationProcessingException exception = assertThrows(
            Oauth2AuthenticationProcessingException.class,
            () -> openIdcUserService.loadUser(request)
        );

        assertEquals("Email not found from Gitlab", exception.getMessage());
    }

    @Test
    void shouldRegisterUser() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "myUsername");
        claims.put("email", "myEmail");
        claims.put("name", "myFirstName myLastName");
        claims.put("avatar_url", "myAvatar");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername("username");
        createdUser.setPassword("password");
        createdUser.setRoles(Collections.singleton(role));

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setSocialProviders(Collections.singletonList("gitlab"));

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, claims);
        when(applicationProperties.getAuthentication())
            .thenReturn(authProperties);
        when(oauth2UserService.loadUser(any()))
            .thenReturn(localUser);
        when(userService.registerUser(any(), any(), any(), any(), any(), any()))
            .thenReturn(createdUser);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("gitlab")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("localhost:8080")
            .authorizationUri("localhost:8080/authorizationUri")
            .tokenUri("localhost:8080/tokenUri")
            .userInfoUri("localhost:8080/userInfoUri")
            .userNameAttributeName("username")
            .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "token",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z")
        );

        OidcIdToken oidcToken = new OidcIdToken(
            "oidcToken",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z"),
            claims
        );

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

        LocalUser actual = (LocalUser) openIdcUserService.loadUser(request);

        assertEquals("username", actual.getUsername());
        assertEquals("password", actual.getPassword());
        assertEquals("myUsername", actual.getAttributes().get("username"));
        assertEquals("myEmail", actual.getAttributes().get("email"));
        assertEquals("myFirstName myLastName", actual.getAttributes().get("name"));
        assertEquals("myAvatar", actual.getAttributes().get("avatar_url"));
        assertEquals(createdUser, actual.getUser());
        assertEquals(oidcToken, actual.getIdToken());
        verify(userService)
            .registerUser("myUsername", "myFirstName", "myLastName", "myEmail", "myAvatar",
                AuthenticationProvider.GITLAB);
    }

    @Test
    void shouldRegisterUserParsingNameByCaseAndPicture() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "myUsername");
        claims.put("email", "myEmail");
        claims.put("name", "MYLASTNAME myFirstName");
        claims.put("picture", "myPicture");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername("username");
        createdUser.setPassword("password");
        createdUser.setRoles(Collections.singleton(role));

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setSocialProviders(Collections.singletonList("gitlab"));
        ApplicationProperties.SocialProvidersConfig config = new ApplicationProperties.SocialProvidersConfig();
        config.setNameCaseParse(true);
        authProperties.setSocialProvidersConfig(Collections.singletonMap("gitlab", config));

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, claims);
        when(applicationProperties.getAuthentication())
            .thenReturn(authProperties);
        when(oauth2UserService.loadUser(any()))
            .thenReturn(localUser);
        when(userService.registerUser(any(), any(), any(), any(), any(), any()))
            .thenReturn(createdUser);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("gitlab")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("localhost:8080")
            .authorizationUri("localhost:8080/authorizationUri")
            .tokenUri("localhost:8080/tokenUri")
            .userInfoUri("localhost:8080/userInfoUri")
            .userNameAttributeName("username")
            .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "token",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z"));

        OidcIdToken oidcToken = new OidcIdToken(
            "oidcToken",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z"),
            claims
        );

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

        LocalUser actual = (LocalUser) openIdcUserService.loadUser(request);

        assertEquals("username", actual.getUsername());
        assertEquals("password", actual.getPassword());
        assertEquals("myUsername", actual.getAttributes().get("username"));
        assertEquals("myEmail", actual.getAttributes().get("email"));
        assertEquals("MYLASTNAME myFirstName", actual.getAttributes().get("name"));
        assertEquals("myPicture", actual.getAttributes().get("picture"));
        assertEquals(createdUser, actual.getUser());
        assertEquals(oidcToken, actual.getIdToken());
        verify(userService)
            .registerUser("myUsername", "myFirstName", "MYLASTNAME", "myEmail", "myPicture",
                AuthenticationProvider.GITLAB);
    }

    @Test
    void shouldRegisterUserNoNameNoAvatar() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "myUsername");
        claims.put("email", "myEmail");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername("username");
        createdUser.setPassword("password");
        createdUser.setRoles(Collections.singleton(role));

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, claims);
        when(oauth2UserService.loadUser(any()))
            .thenReturn(localUser);
        when(userService.registerUser(any(), any(), any(), any(), any(), any()))
            .thenReturn(createdUser);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("gitlab")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("localhost:8080")
            .authorizationUri("localhost:8080/authorizationUri")
            .tokenUri("localhost:8080/tokenUri")
            .userInfoUri("localhost:8080/userInfoUri")
            .userNameAttributeName("username")
            .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "token",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z")
        );

        OidcIdToken oidcToken = new OidcIdToken(
            "oidcToken",
            Instant.parse("2000-01-01T01:00:00.00Z"),
            Instant.parse("2000-01-02T01:00:00.00Z"),
            claims
        );

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

        LocalUser actual = (LocalUser) openIdcUserService.loadUser(request);

        assertEquals("username", actual.getUsername());
        assertEquals("password", actual.getPassword());
        assertEquals("myUsername", actual.getAttributes().get("username"));
        assertEquals("myEmail", actual.getAttributes().get("email"));
        assertEquals(createdUser, actual.getUser());
        assertEquals(oidcToken, actual.getIdToken());
        verify(userService)
            .registerUser("myUsername", null, null, "myEmail", null, AuthenticationProvider.GITLAB);
    }
}
