package com.michelin.suricate.integrations;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.dto.api.token.JwtAuthenticationResponseDto;
import com.michelin.suricate.model.dto.api.user.SignInRequestDto;
import com.michelin.suricate.model.dto.api.user.UserRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

        ResponseEntity<UserResponseDto> signUpResponse = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/users/signup",
                userRequestDto, UserResponseDto.class);

        assertThat(signUpResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        SignInRequestDto badSignInRequestDto = new SignInRequestDto();
        badSignInRequestDto.setUsername("username");
        badSignInRequestDto.setPassword("badPassword");

        ResponseEntity<ApiErrorDto> badSignInResponse = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/signin",
                badSignInRequestDto, ApiErrorDto.class);

        assertThat(badSignInResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        SignInRequestDto signInRequestDto = new SignInRequestDto();
        signInRequestDto.setUsername("username");
        signInRequestDto.setPassword("none");

        ResponseEntity<JwtAuthenticationResponseDto> signInResponse = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/signin",
                signInRequestDto, JwtAuthenticationResponseDto.class);

        assertThat(signInResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(signInResponse.getBody()).isNotNull();
        assertThat(signInResponse.getBody().getAccessToken()).isNotNull();
    }
}
