package com.michelin.suricate.integration;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.dto.api.token.JwtAuthenticationResponseDto;
import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenRequestDto;
import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.dto.api.user.SignInRequestDto;
import com.michelin.suricate.model.dto.api.user.UserRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import java.util.List;
import org.junit.jupiter.api.Test;
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

@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthenticationIntegrationTest {
    @Value(value = "${local.server.port}")
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
        ResponseEntity<UserResponseDto> signUpResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/users/signup",
                POST, new HttpEntity<>(userRequestDto), UserResponseDto.class);

        assertEquals(HttpStatus.CREATED, signUpResponse.getStatusCode());

        SignInRequestDto badSignInRequestDto = new SignInRequestDto();
        badSignInRequestDto.setUsername("username");
        badSignInRequestDto.setPassword("badPassword");

        // Sign in with bad password
        ResponseEntity<ApiErrorDto> badSignInResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/auth/signin",
                POST, new HttpEntity<>(badSignInRequestDto), ApiErrorDto.class);

        assertEquals(HttpStatus.UNAUTHORIZED, badSignInResponse.getStatusCode());
        assertNotNull(badSignInResponse.getBody());
        assertEquals("Bad credentials", badSignInResponse.getBody().getMessage());

        SignInRequestDto emptyUsernamePasswordSignInRequestDto = new SignInRequestDto();
        emptyUsernamePasswordSignInRequestDto.setUsername(EMPTY);
        emptyUsernamePasswordSignInRequestDto.setPassword(EMPTY);

        // Sign in with empty username/password
        ResponseEntity<ApiErrorDto> emptyUsernamePasswordSignInResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/auth/signin",
                POST, new HttpEntity<>(emptyUsernamePasswordSignInRequestDto), ApiErrorDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, emptyUsernamePasswordSignInResponse.getStatusCode());
        assertNotNull(emptyUsernamePasswordSignInResponse.getBody());
        assertTrue(emptyUsernamePasswordSignInResponse.getBody().getMessage().contains("Username must not be blank"));
        assertTrue(emptyUsernamePasswordSignInResponse.getBody().getMessage().contains("Password must not be blank"));

        SignInRequestDto signInRequestDto = new SignInRequestDto();
        signInRequestDto.setUsername("username");
        signInRequestDto.setPassword("none");

        // Sign in with good password
        ResponseEntity<JwtAuthenticationResponseDto> signInResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/auth/signin",
                POST, new HttpEntity<>(signInRequestDto), JwtAuthenticationResponseDto.class);

        assertEquals(HttpStatus.OK, signInResponse.getStatusCode());
        assertNotNull(signInResponse.getBody());
        assertNotNull(signInResponse.getBody().getAccessToken());
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
        ResponseEntity<JwtAuthenticationResponseDto> signInResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/auth/signin",
                POST, new HttpEntity<>(signInRequestDto), JwtAuthenticationResponseDto.class);

        assertNotNull(signInResponse.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(signInResponse.getBody().getAccessToken());

        PersonalAccessTokenRequestDto personalAccessTokenRequestDto = new PersonalAccessTokenRequestDto();
        personalAccessTokenRequestDto.setName("newToken");

        // Create PAT
        ResponseEntity<PersonalAccessTokenResponseDto> createPatResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/users/personal-access-token",
                POST, new HttpEntity<>(personalAccessTokenRequestDto, headers), PersonalAccessTokenResponseDto.class);

        assertNotNull(createPatResponse.getBody());

        HttpHeaders patHeaders = new HttpHeaders();
        patHeaders.set("Authorization", "Token " + createPatResponse.getBody().getValue());

        // Wrong HTTP verb
        ResponseEntity<ApiErrorDto> methodNotSupportedResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/users/personal-access-token",
                PATCH, new HttpEntity<>(patHeaders), ApiErrorDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, methodNotSupportedResponse.getStatusCode());
        assertNotNull(methodNotSupportedResponse.getBody());
        assertEquals("Request method 'PATCH' is not supported", methodNotSupportedResponse.getBody().getMessage());

        // Get personal access tokens
        ResponseEntity<List<PersonalAccessTokenResponseDto>> response = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/users/personal-access-token",
                GET, new HttpEntity<>(patHeaders),
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }
}
