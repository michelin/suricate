package com.michelin.suricate.integrations;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.dto.api.token.JwtAuthenticationResponseDto;
import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenRequestDto;
import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.dto.api.user.SignInRequestDto;
import com.michelin.suricate.model.dto.api.user.UserRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.apache.directory.api.util.StringConstants.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthenticationIntegrationTest {
    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRegisterAndAuthenticate() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");
        userRequestDto.setEmail("email");
        userRequestDto.setFirstname("firstName");
        userRequestDto.setLastname("lastName");
        userRequestDto.setPassword("none");
        userRequestDto.setConfirmPassword("none");

        // Sign up
        ResponseEntity<UserResponseDto> signUpResponse = restTemplate.exchange("http://localhost:" + port + "/api/v1/users/signup",
                POST, new HttpEntity<>(userRequestDto), UserResponseDto.class);

        assertThat(signUpResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        SignInRequestDto badSignInRequestDto = new SignInRequestDto();
        badSignInRequestDto.setUsername("username");
        badSignInRequestDto.setPassword("badPassword");

        // Sign in with bad password
        ResponseEntity<ApiErrorDto> badSignInResponse = restTemplate.exchange("http://localhost:" + port + "/api/v1/auth/signin",
                POST, new HttpEntity<>(badSignInRequestDto), ApiErrorDto.class);

        assertThat(badSignInResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(badSignInResponse.getBody()).isNotNull();
        assertThat(badSignInResponse.getBody().getMessage()).isEqualTo("Bad credentials");

        SignInRequestDto emptyUsernamePasswordSignInRequestDto = new SignInRequestDto();
        emptyUsernamePasswordSignInRequestDto.setUsername(EMPTY);
        emptyUsernamePasswordSignInRequestDto.setPassword(EMPTY);

        // Sign in with empty username/password
        ResponseEntity<ApiErrorDto> emptyUsernamePasswordSignInResponse = restTemplate.exchange("http://localhost:" + port + "/api/v1/auth/signin",
                POST, new HttpEntity<>(emptyUsernamePasswordSignInRequestDto), ApiErrorDto.class);

        assertThat(emptyUsernamePasswordSignInResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(emptyUsernamePasswordSignInResponse.getBody()).isNotNull();
        assertThat(emptyUsernamePasswordSignInResponse.getBody().getMessage()).contains("Username must not be blank");
        assertThat(emptyUsernamePasswordSignInResponse.getBody().getMessage()).contains("Password must not be blank");

        SignInRequestDto signInRequestDto = new SignInRequestDto();
        signInRequestDto.setUsername("username");
        signInRequestDto.setPassword("none");

        // Sign in with good password
        ResponseEntity<JwtAuthenticationResponseDto> signInResponse = restTemplate.exchange("http://localhost:" + port + "/api/v1/auth/signin",
                POST, new HttpEntity<>(signInRequestDto), JwtAuthenticationResponseDto.class);

        assertThat(signInResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(signInResponse.getBody()).isNotNull();
        assertThat(signInResponse.getBody().getAccessToken()).isNotNull();
    }

    @Test
    void shouldAuthenticateWithPersonalAccessToken() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");
        userRequestDto.setEmail("email");
        userRequestDto.setFirstname("firstName");
        userRequestDto.setLastname("lastName");
        userRequestDto.setPassword("none");
        userRequestDto.setConfirmPassword("none");

        // Sign up
        restTemplate.exchange("http://localhost:" + port + "/api/v1/users/signup",
                POST, new HttpEntity<>(userRequestDto), UserResponseDto.class);

        SignInRequestDto signInRequestDto = new SignInRequestDto();
        signInRequestDto.setUsername("username");
        signInRequestDto.setPassword("none");

        // Sign in
        ResponseEntity<JwtAuthenticationResponseDto> signInResponse = restTemplate.exchange("http://localhost:" + port + "/api/v1/auth/signin",
                POST, new HttpEntity<>(signInRequestDto), JwtAuthenticationResponseDto.class);

        assertThat(signInResponse.getBody()).isNotNull();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(signInResponse.getBody().getAccessToken());

        PersonalAccessTokenRequestDto personalAccessTokenRequestDto = new PersonalAccessTokenRequestDto();
        personalAccessTokenRequestDto.setName("newToken");

        // Create PAT
        ResponseEntity<PersonalAccessTokenResponseDto> createPATResponse = restTemplate.exchange("http://localhost:" + port + "/api/v1/users/personal-access-token",
                POST, new HttpEntity<>(personalAccessTokenRequestDto, headers), PersonalAccessTokenResponseDto.class);

        assertThat(createPATResponse.getBody()).isNotNull();

        HttpHeaders patHeaders = new HttpHeaders();
        patHeaders.set("Authorization", "Token " + createPATResponse.getBody().getValue());

        // Wrong HTTP verb
        ResponseEntity<ApiErrorDto> methodNotSupportedResponse = restTemplate.exchange("http://localhost:" + port + "/api/v1/users/personal-access-token",
                PATCH, new HttpEntity<>(patHeaders), ApiErrorDto.class);

        assertThat(methodNotSupportedResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(methodNotSupportedResponse.getBody()).isNotNull();
        assertThat(methodNotSupportedResponse.getBody().getMessage()).isEqualTo("Request method 'PATCH' not supported");

        // Get personal access tokens
        ResponseEntity<List<PersonalAccessTokenResponseDto>> response = restTemplate.exchange("http://localhost:" + port + "/api/v1/users/personal-access-token",
                GET, new HttpEntity<>(patHeaders), new ParameterizedTypeReference<List<PersonalAccessTokenResponseDto>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
    }
}
