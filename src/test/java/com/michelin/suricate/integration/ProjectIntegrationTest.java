package com.michelin.suricate.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.dto.api.project.ProjectRequestDto;
import com.michelin.suricate.model.dto.api.project.ProjectResponseDto;
import com.michelin.suricate.model.dto.api.token.JwtAuthenticationResponseDto;
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
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectIntegrationTest {
    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateProject() {
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

        ProjectRequestDto noNameProjectRequestDto = new ProjectRequestDto();
        noNameProjectRequestDto.setWidgetHeight(350);
        noNameProjectRequestDto.setMaxColumn(5);
        noNameProjectRequestDto.setCssStyle("css");

        // Create project with no name
        ResponseEntity<ApiErrorDto> noNameProjectResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/projects",
                POST, new HttpEntity<>(noNameProjectRequestDto, headers), ApiErrorDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, noNameProjectResponse.getStatusCode());
        assertNotNull(noNameProjectResponse.getBody());
        assertEquals("Name must not be blank", noNameProjectResponse.getBody().getMessage());

        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setName("name");
        projectRequestDto.setWidgetHeight(350);
        projectRequestDto.setMaxColumn(5);
        projectRequestDto.setCssStyle("css");

        // Create project
        ResponseEntity<ProjectResponseDto> projectResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/projects",
                POST, new HttpEntity<>(projectRequestDto, headers), ProjectResponseDto.class);

        assertEquals(HttpStatus.CREATED, projectResponse.getStatusCode());
        assertNotNull(projectResponse.getBody());
    }

    @Test
    void shouldGetProjectUsersWithRequiredInformation() {
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

        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setName("name");
        projectRequestDto.setWidgetHeight(350);
        projectRequestDto.setMaxColumn(5);
        projectRequestDto.setCssStyle("css");

        // Create project
        ResponseEntity<ProjectResponseDto> projectResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/projects",
                POST, new HttpEntity<>(projectRequestDto, headers), ProjectResponseDto.class);

        assertEquals(HttpStatus.CREATED, projectResponse.getStatusCode());
        assertNotNull(projectResponse.getBody());

        // Get project users
        ResponseEntity<List<UserResponseDto>> projectUsersResponse = restTemplate
            .exchange("http://localhost:" + port + "/api/v1/projects/"
                    + projectResponse.getBody().getToken() + "/users",
                GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, projectUsersResponse.getStatusCode());
        assertNotNull(projectUsersResponse.getBody());
        assertNotNull(projectUsersResponse.getBody().getFirst().getId());
        assertEquals("username", projectUsersResponse.getBody().getFirst().getUsername());
        assertEquals("firstName", projectUsersResponse.getBody().getFirst().getFirstname());
        assertEquals("lastName", projectUsersResponse.getBody().getFirst().getLastname());
    }
}
