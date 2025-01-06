package com.michelin.suricate.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.dto.api.token.JwtAuthenticationResponseDto;
import com.michelin.suricate.model.dto.api.user.SignInRequestDto;
import com.michelin.suricate.model.dto.api.user.UserRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.dto.api.user.UserSettingRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("integration-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserIntegrationTest {
    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldGetUser() {
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

        // Get user that does not exist
        ResponseEntity<ApiErrorDto> userNotFoundResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/users/2",
                GET, new HttpEntity<>(headers), ApiErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, userNotFoundResponse.getStatusCode());
        assertNotNull(userNotFoundResponse.getBody());
        assertEquals("User '2' not found", userNotFoundResponse.getBody().getMessage());

        // Get user with bad parameter type
        ResponseEntity<ApiErrorDto> badParameterResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/users/badParameter",
                GET, new HttpEntity<>(headers), ApiErrorDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, badParameterResponse.getStatusCode());
        assertNotNull(badParameterResponse.getBody());
        assertEquals("Bad request", badParameterResponse.getBody().getMessage());

        // Get user that exist
        ResponseEntity<UserResponseDto> userFoundResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/users/1",
                GET, new HttpEntity<>(headers), UserResponseDto.class);

        assertEquals(HttpStatus.OK, userFoundResponse.getStatusCode());
        assertNotNull(userFoundResponse.getBody());
        assertEquals("username", userFoundResponse.getBody().getUsername());
        assertEquals("firstName", userFoundResponse.getBody().getFirstname());
        assertEquals("lastName", userFoundResponse.getBody().getLastname());
    }

    @Test
    void shouldUpdateUserSettings() {
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

        UserRequestDto anotherUserRequestDto = new UserRequestDto();
        anotherUserRequestDto.setUsername("username2");
        anotherUserRequestDto.setEmail("email2");
        anotherUserRequestDto.setFirstname("firstName2");
        anotherUserRequestDto.setLastname("lastName2");
        anotherUserRequestDto.setPassword("none2");
        anotherUserRequestDto.setConfirmPassword("none2");

        // Sign up another user
        restTemplate.exchange("http://localhost:" + port + "/api/v1/users/signup",
            POST, new HttpEntity<>(anotherUserRequestDto), UserResponseDto.class);

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

        UserSettingRequestDto userSettingRequestDto = new UserSettingRequestDto();
        userSettingRequestDto.setAllowedSettingValueId(1L);
        userSettingRequestDto.setUnconstrainedValue("value");

        // Should fail to update settings of another user
        ResponseEntity<ApiErrorDto> accessDeniedResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/users/username2/settings/1",
                PUT, new HttpEntity<>(userSettingRequestDto, headers), ApiErrorDto.class);

        assertEquals(HttpStatus.FORBIDDEN, accessDeniedResponse.getStatusCode());
        assertNotNull(accessDeniedResponse.getBody());
        assertEquals("You don't have permission to access to this resource",
            accessDeniedResponse.getBody().getMessage());

        // Should update settings
        ResponseEntity<ApiErrorDto> response = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/users/username/settings/1",
                PUT, new HttpEntity<>(userSettingRequestDto, headers), ApiErrorDto.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
