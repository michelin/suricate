package com.michelin.suricate.security.oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.enums.AuthenticationProvider;
import com.michelin.suricate.properties.ApplicationProperties;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.UserService;
import com.michelin.suricate.utils.exceptions.Oauth2AuthenticationProcessingException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestOperations;

@ExtendWith(MockitoExtension.class)
class Oauth2UserServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private RestOperations restOperations;

    @InjectMocks
    private Oauth2UserService oauth2UserService;

    @Test
    void shouldNotLoadUserWhenIdProviderNotRecognized() {
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

        OAuth2AccessToken token =
            new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        OAuth2UserRequest request = new OAuth2UserRequest(clientRegistration, token);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("username", "myUsername");

        when(restOperations.exchange(any(), any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(attributes));

        assertThatThrownBy(() -> oauth2UserService.loadUser(request))
            .isInstanceOf(Oauth2AuthenticationProcessingException.class)
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

        OAuth2AccessToken token =
            new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        OAuth2UserRequest request = new OAuth2UserRequest(clientRegistration, token);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("username", "    ");

        when(restOperations.exchange(any(), any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(attributes));

        assertThatThrownBy(() -> oauth2UserService.loadUser(request))
            .isInstanceOf(Oauth2AuthenticationProcessingException.class)
            .hasMessage("Username not found from gitlab");
    }

    @Test
    void shouldNotLoadUserWhenEmailBlank() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("username", "myUsername");
        attributes.put("email", "   ");

        when(restOperations.exchange(any(), any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(attributes));

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

        OAuth2AccessToken token =
            new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        OAuth2UserRequest request = new OAuth2UserRequest(clientRegistration, token);

        assertThatThrownBy(() -> oauth2UserService.loadUser(request))
            .isInstanceOf(Oauth2AuthenticationProcessingException.class)
            .hasMessage("Email not found from gitlab");
    }

    @Test
    void shouldRegisterUser() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("username", "myUsername");
        attributes.put("email", "myEmail");
        attributes.put("name", "myFirstName myLastName");
        attributes.put("avatar_url", "myAvatar");

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

        when(applicationProperties.getAuthentication())
            .thenReturn(authProperties);
        when(restOperations.exchange(any(), any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(attributes));
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

        OAuth2AccessToken token =
            new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        OAuth2UserRequest request = new OAuth2UserRequest(clientRegistration, token);

        LocalUser actual = (LocalUser) oauth2UserService.loadUser(request);

        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getAttributes()).containsEntry("username", "myUsername");
        assertThat(actual.getAttributes()).containsEntry("email", "myEmail");
        assertThat(actual.getAttributes()).containsEntry("name", "myFirstName myLastName");
        assertThat(actual.getAttributes()).containsEntry("avatar_url", "myAvatar");
        assertThat(actual.getUser()).isEqualTo(createdUser);
        verify(userService)
            .registerUser("myUsername", "myFirstName", "myLastName", "myEmail", "myAvatar",
                AuthenticationProvider.GITLAB);
    }

    @Test
    void shouldRegisterUserByLoginParsingNameByCaseAndPicture() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("username", "myUsername");
        attributes.put("login", "myUsername");
        attributes.put("email", "myEmail");
        attributes.put("name", "MYLASTNAME myFirstName");
        attributes.put("picture", "myPicture");

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

        when(applicationProperties.getAuthentication())
            .thenReturn(authProperties);
        when(restOperations.exchange(any(), any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(attributes));
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

        OAuth2AccessToken token =
            new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        OAuth2UserRequest request = new OAuth2UserRequest(clientRegistration, token);

        LocalUser actual = (LocalUser) oauth2UserService.loadUser(request);

        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getAttributes()).containsEntry("username", "myUsername");
        assertThat(actual.getAttributes()).containsEntry("login", "myUsername");
        assertThat(actual.getAttributes()).containsEntry("email", "myEmail");
        assertThat(actual.getAttributes()).containsEntry("name", "MYLASTNAME myFirstName");
        assertThat(actual.getAttributes()).containsEntry("picture", "myPicture");
        assertThat(actual.getUser()).isEqualTo(createdUser);
        verify(userService)
            .registerUser("myUsername", "myFirstName", "MYLASTNAME", "myEmail", "myPicture",
                AuthenticationProvider.GITLAB);
    }

    @Test
    void shouldRegisterUserNoNameNoAvatar() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("username", "myUsername");
        attributes.put("login", "myUsername");
        attributes.put("email", "myEmail");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername("username");
        createdUser.setPassword("password");
        createdUser.setRoles(Collections.singleton(role));

        when(restOperations.exchange(any(), any(ParameterizedTypeReference.class)))
            .thenReturn(ResponseEntity.ok(attributes));
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

        OAuth2AccessToken token =
            new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", Instant.parse("2000-01-01T01:00:00.00Z"),
                Instant.parse("2000-01-02T01:00:00.00Z"));

        OAuth2UserRequest request = new OAuth2UserRequest(clientRegistration, token);

        LocalUser actual = (LocalUser) oauth2UserService.loadUser(request);

        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getAttributes()).containsEntry("username", "myUsername");
        assertThat(actual.getAttributes()).containsEntry("login", "myUsername");
        assertThat(actual.getAttributes()).containsEntry("email", "myEmail");
        assertThat(actual.getUser()).isEqualTo(createdUser);
        verify(userService)
            .registerUser("myUsername", null, null, "myEmail", null, AuthenticationProvider.GITLAB);
    }
}
