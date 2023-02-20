package com.michelin.suricate.security.oauth2;

import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.enums.AuthenticationProvider;
import com.michelin.suricate.properties.ApplicationProperties;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.UserService;
import com.michelin.suricate.utils.exceptions.OAuth2AuthenticationProcessingException;
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

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OIDCUserServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private OAuth2UserService oAuth2UserService;

    @InjectMocks
    private OIDCUserService oidcUserService;

    @Test
    void shouldNotLoadUserWhenIDProviderNotRecognized() {
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

        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "myUsername");

        OidcIdToken oidcToken = new OidcIdToken("oidcToken", Instant.parse("2000-01-01T01:00:00.00Z"), Instant.parse("2000-01-02T01:00:00.00Z"),
                claims);

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));
        LocalUser localUser = new LocalUser(user, claims);

        when(oAuth2UserService.loadUser(any()))
                .thenReturn(localUser);

        assertThatThrownBy(() -> oidcUserService.loadUser(request))
                .isInstanceOf(OAuth2AuthenticationProcessingException.class)
                .hasMessage("ID provider unknownIDP is not recognized");
    }

    @Test
    void shouldNotLoadUserWhenUsernameBlank() {
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

        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "   ");

        OidcIdToken oidcToken = new OidcIdToken("oidcToken", Instant.parse("2000-01-01T01:00:00.00Z"), Instant.parse("2000-01-02T01:00:00.00Z"),
                claims);

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));
        LocalUser localUser = new LocalUser(user, claims);

        when(oAuth2UserService.loadUser(any()))
                .thenReturn(localUser);

        assertThatThrownBy(() -> oidcUserService.loadUser(request))
                .isInstanceOf(OAuth2AuthenticationProcessingException.class)
                .hasMessage("Username not found from gitlab");
    }

    @Test
    void shouldNotLoadUserWhenEmailBlank() {
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

        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "myUsername");
        claims.put("email", "   ");

        OidcIdToken oidcToken = new OidcIdToken("oidcToken", Instant.parse("2000-01-01T01:00:00.00Z"), Instant.parse("2000-01-02T01:00:00.00Z"),
                claims);

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));
        LocalUser localUser = new LocalUser(user, claims);

        when(oAuth2UserService.loadUser(any()))
                .thenReturn(localUser);

        assertThatThrownBy(() -> oidcUserService.loadUser(request))
                .isInstanceOf(OAuth2AuthenticationProcessingException.class)
                .hasMessage("Email not found from gitlab");
    }

    @Test
    void shouldRegisterUser() {
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

        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "myUsername");
        claims.put("email", "myEmail");
        claims.put("name", "myFirstName myLastName");
        claims.put("avatar_url", "myAvatar");

        OidcIdToken oidcToken = new OidcIdToken("oidcToken", Instant.parse("2000-01-01T01:00:00.00Z"), Instant.parse("2000-01-02T01:00:00.00Z"),
                claims);

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

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
        when(oAuth2UserService.loadUser(any()))
                .thenReturn(localUser);
        when(userService.registerUser(any(), any(), any(), any(), any(), any()))
                .thenReturn(createdUser);

        LocalUser actual = (LocalUser) oidcUserService.loadUser(request);

        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getAttributes()).containsEntry("username", "myUsername");
        assertThat(actual.getAttributes()).containsEntry("email", "myEmail");
        assertThat(actual.getAttributes()).containsEntry("name", "myFirstName myLastName");
        assertThat(actual.getAttributes()).containsEntry("avatar_url", "myAvatar");
        assertThat(actual.getUser()).isEqualTo(createdUser);
        verify(userService)
                .registerUser("myUsername", "myFirstName", "myLastName", "myEmail", "myAvatar", AuthenticationProvider.GITLAB);
    }

    @Test
    void shouldRegisterUserParsingNameByCaseAndPicture() {
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

        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "mySubject");
        claims.put("username", "myUsername");
        claims.put("nickname", "myUsername");
        claims.put("email", "myEmail");
        claims.put("name", "MYLASTNAME myFirstName");
        claims.put("picture", "myPicture");

        OidcIdToken oidcToken = new OidcIdToken("oidcToken", Instant.parse("2000-01-01T01:00:00.00Z"), Instant.parse("2000-01-02T01:00:00.00Z"),
                claims);

        OidcUserRequest request = new OidcUserRequest(clientRegistration, token, oidcToken);

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
        when(oAuth2UserService.loadUser(any()))
                .thenReturn(localUser);
        when(userService.registerUser(any(), any(), any(), any(), any(), any()))
                .thenReturn(createdUser);

        LocalUser actual = (LocalUser) oidcUserService.loadUser(request);

        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getAttributes()).containsEntry("username", "myUsername");
        assertThat(actual.getAttributes()).containsEntry("email", "myEmail");
        assertThat(actual.getAttributes()).containsEntry("name", "MYLASTNAME myFirstName");
        assertThat(actual.getAttributes()).containsEntry("picture", "myPicture");
        assertThat(actual.getUser()).isEqualTo(createdUser);
        verify(userService)
                .registerUser("myUsername", "myFirstName", "MYLASTNAME", "myEmail", "myPicture", AuthenticationProvider.GITLAB);
    }
}
