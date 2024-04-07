package com.michelin.suricate.integration;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        ResponseEntity<JwtAuthenticationResponseDto> signInResponse =
            restTemplate.exchange("http://localhost:" + port + "/api/v1/auth/signin",
                POST, new HttpEntity<>(signInRequestDto), JwtAuthenticationResponseDto.class);

        assertThat(signInResponse.getBody()).isNotNull();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(signInResponse.getBody().getAccessToken());

        // Get user that does not exist
        ResponseEntity<ApiErrorDto> userNotFoundResponse =
            restTemplate.exchange("http://localhost:" + port + "/api/v1/users/2",
                GET, new HttpEntity<>(headers), ApiErrorDto.class);

        assertThat(userNotFoundResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(userNotFoundResponse.getBody()).isNotNull();
        assertThat(userNotFoundResponse.getBody().getMessage()).isEqualTo("User '2' not found");

        // Get user with bad parameter type
        ResponseEntity<ApiErrorDto> badParameterResponse =
            restTemplate.exchange("http://localhost:" + port + "/api/v1/users/badParameter",
                GET, new HttpEntity<>(headers), ApiErrorDto.class);

        assertThat(badParameterResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(badParameterResponse.getBody()).isNotNull();
        assertThat(badParameterResponse.getBody().getMessage()).isEqualTo("Bad request");

        // Get user that exist
        ResponseEntity<UserResponseDto> userFoundResponse =
            restTemplate.exchange("http://localhost:" + port + "/api/v1/users/1",
                GET, new HttpEntity<>(headers), UserResponseDto.class);

        assertThat(userFoundResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userFoundResponse.getBody()).isNotNull();
        assertThat(userFoundResponse.getBody().getUsername()).isEqualTo("username");
        assertThat(userFoundResponse.getBody().getFirstname()).isEqualTo("firstName");
        assertThat(userFoundResponse.getBody().getLastname()).isEqualTo("lastName");
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
        ResponseEntity<JwtAuthenticationResponseDto> signInResponse =
            restTemplate.exchange("http://localhost:" + port + "/api/v1/auth/signin",
                POST, new HttpEntity<>(signInRequestDto), JwtAuthenticationResponseDto.class);

        assertThat(signInResponse.getBody()).isNotNull();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(signInResponse.getBody().getAccessToken());

        UserSettingRequestDto userSettingRequestDto = new UserSettingRequestDto();
        userSettingRequestDto.setAllowedSettingValueId(1L);
        userSettingRequestDto.setUnconstrainedValue("value");

        // Should fail to update settings of another user
        ResponseEntity<ApiErrorDto> accessDeniedResponse =
            restTemplate.exchange("http://localhost:" + port + "/api/v1/users/username2/settings/1",
                PUT, new HttpEntity<>(userSettingRequestDto, headers), ApiErrorDto.class);

        assertThat(accessDeniedResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(accessDeniedResponse.getBody()).isNotNull();
        assertThat(accessDeniedResponse.getBody().getMessage()).isEqualTo(
            "You don't have permission to access to this resource");

        // Should update settings
        ResponseEntity<ApiErrorDto> response =
            restTemplate.exchange("http://localhost:" + port + "/api/v1/users/username/settings/1",
                PUT, new HttpEntity<>(userSettingRequestDto, headers), ApiErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}
