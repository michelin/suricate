package com.michelin.suricate.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.project.ProjectRequestDto;
import com.michelin.suricate.model.dto.api.project.ProjectResponseDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetPositionRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.dto.websocket.WebsocketClient;
import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectGrid;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.ProjectGridService;
import com.michelin.suricate.services.api.ProjectService;
import com.michelin.suricate.services.api.ProjectWidgetService;
import com.michelin.suricate.services.api.UserService;
import com.michelin.suricate.services.mapper.ProjectGridMapper;
import com.michelin.suricate.services.mapper.ProjectMapper;
import com.michelin.suricate.services.mapper.UserMapper;
import com.michelin.suricate.services.websocket.DashboardWebSocketService;
import com.michelin.suricate.utils.exceptions.ApiException;
import com.michelin.suricate.utils.exceptions.InvalidFileException;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectGridService projectGridService;

    @Mock
    private ProjectWidgetService projectWidgetService;

    @Mock
    private UserService userService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectGridMapper projectGridMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private DashboardWebSocketService dashboardWebSocketService;

    @InjectMocks
    private ProjectController projectController;

    @Test
    void shouldGetAll() {
        Project project = new Project();
        project.setId(1L);

        ProjectResponseDto projectResponseDto = new ProjectResponseDto();
        projectResponseDto.setName("name");

        when(projectService.getAll(any(), any()))
            .thenReturn(new PageImpl<>(Collections.singletonList(project)));
        when(projectMapper.toProjectDtoNoAsset(any()))
            .thenReturn(projectResponseDto);

        Page<ProjectResponseDto> actual = projectController.getAll("search", Pageable.unpaged());

        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).hasSize(1);
        assertThat(actual.get().collect(Collectors.toList()).get(0)).isEqualTo(projectResponseDto);
    }

    @Test
    void shouldCreateProjectUserNotFound() {
        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setName("name");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.createProject(localUser, projectRequestDto))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("User 'username' not found");
    }

    @Test
    void shouldCreateProject() {
        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setName("name");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        ProjectResponseDto projectResponseDto = new ProjectResponseDto();
        projectResponseDto.setName("name");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.of(user));
        when(projectService.createProjectForUser(any(), any()))
            .thenReturn(new Project());
        when(projectGridMapper.toProjectGridEntity(any(Project.class)))
            .thenReturn(new ProjectGrid());
        when(projectGridService.create(any()))
            .thenReturn(new ProjectGrid());
        when(projectMapper.toProjectDto(any()))
            .thenReturn(projectResponseDto);

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ResponseEntity<ProjectResponseDto> actual = projectController.createProject(localUser, projectRequestDto);

        assertThat(actual.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody()).isEqualTo(projectResponseDto);
    }

    @Test
    void shouldGetOneByTokenNotFound() {
        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.getOneByToken("token"))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldGetOneByToken() {
        Project project = new Project();
        project.setId(1L);

        ProjectResponseDto projectResponseDto = new ProjectResponseDto();
        projectResponseDto.setName("name");

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectMapper.toProjectDto(any()))
            .thenReturn(projectResponseDto);

        ResponseEntity<ProjectResponseDto> actual = projectController.getOneByToken("token");

        assertThat(actual.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody()).isEqualTo(projectResponseDto);
    }

    @Test
    void shouldUpdateProjectNotFound() {
        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setName("name");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.updateProject(localUser, "token", projectRequestDto))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldUpdateProjectNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setName("name");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(false);

        assertThatThrownBy(() -> projectController.updateProject(localUser, "token", projectRequestDto))
            .isInstanceOf(ApiException.class)
            .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldUpdateProject() {
        Project project = new Project();
        project.setId(1L);

        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setName("name");
        projectRequestDto.setWidgetHeight(1);
        projectRequestDto.setMaxColumn(1);
        projectRequestDto.setCssStyle("css");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);

        ResponseEntity<Void> actual = projectController.updateProject(localUser, "token", projectRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldUpdateProjectScreenshotNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        MockMultipartFile file = new MockMultipartFile("name", new byte[10]);

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.updateProjectScreenshot(localUser, "token", file))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldUpdateProjectScreenshotNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        MockMultipartFile file = new MockMultipartFile("name", new byte[10]);

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(false);

        assertThatThrownBy(() -> projectController.updateProjectScreenshot(localUser, "token", file))
            .isInstanceOf(ApiException.class)
            .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProjectScreenshot() throws IOException {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        MockMultipartFile file = mock(MockMultipartFile.class);

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);
        doThrow(new IOException("error")).when(file)
            .getBytes();
        when(file.getOriginalFilename())
            .thenReturn("originalName");

        assertThatThrownBy(() -> projectController.updateProjectScreenshot(localUser, "token", file))
            .isInstanceOf(InvalidFileException.class)
            .hasMessage("The file originalName cannot be read for entity Project '1'");
    }

    @Test
    void shouldUpdateProjectScreenshot() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        MockMultipartFile file = new MockMultipartFile("name", "originalName", "image/png", new byte[10]);

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);

        ResponseEntity<Void> actual = projectController.updateProjectScreenshot(localUser, "token", file);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldDeleteProjectByIdNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.deleteProjectById(localUser, "token"))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldDeleteProjectByIdNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(false);

        assertThatThrownBy(() -> projectController.deleteProjectById(localUser, "token"))
            .isInstanceOf(ApiException.class)
            .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldDeleteProjectById() {
        Project project = new Project();
        project.setId(1L);

        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setName("name");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);

        ResponseEntity<Void> actual = projectController.deleteProjectById(localUser, "token");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldUpdateProjectWidgetsPositionForProjectNotFound() {
        ProjectWidgetPositionRequestDto projectWidgetPositionRequestDto = new ProjectWidgetPositionRequestDto();
        projectWidgetPositionRequestDto.setProjectWidgetId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        List<ProjectWidgetPositionRequestDto> projectWidgetPositionRequestDtos =
            Collections.singletonList(projectWidgetPositionRequestDto);

        assertThatThrownBy(() -> projectController.updateProjectWidgetsPositionForProject(localUser, "token",
            projectWidgetPositionRequestDtos))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldUpdateProjectWidgetsPositionForProjectNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        ProjectWidgetPositionRequestDto projectWidgetPositionRequestDto = new ProjectWidgetPositionRequestDto();
        projectWidgetPositionRequestDto.setProjectWidgetId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(false);

        List<ProjectWidgetPositionRequestDto> projectWidgetPositionRequestDtos =
            Collections.singletonList(projectWidgetPositionRequestDto);

        assertThatThrownBy(() -> projectController.updateProjectWidgetsPositionForProject(localUser, "token",
            projectWidgetPositionRequestDtos))
            .isInstanceOf(ApiException.class)
            .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldUpdateProjectWidgetsPositionForProject() {
        Project project = new Project();
        project.setId(1L);

        ProjectWidgetPositionRequestDto projectWidgetPositionRequestDto = new ProjectWidgetPositionRequestDto();
        projectWidgetPositionRequestDto.setProjectWidgetId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);

        List<ProjectWidgetPositionRequestDto> projectWidgetPositionRequestDtos =
            Collections.singletonList(projectWidgetPositionRequestDto);

        ResponseEntity<Void> actual = projectController.updateProjectWidgetsPositionForProject(localUser, "token",
            projectWidgetPositionRequestDtos);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldGetProjectUsersNotFound() {
        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.getProjectUsers("token"))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldGetProjectUser() {
        Project project = new Project();
        project.setId(1L);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("username");

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(userMapper.toUsersDtos(any()))
            .thenReturn(Collections.singletonList(userResponseDto));

        ResponseEntity<List<UserResponseDto>> actual = projectController.getProjectUsers("token");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).contains(userResponseDto);
    }

    @Test
    void shouldAddUserToProjectNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Map<String, String> usernameMap = Collections.singletonMap("username", "username");

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.addUserToProject(localUser, "token", usernameMap))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldAddUserToProjectNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Map<String, String> usernameMap = Collections.singletonMap("username", "username");

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(false);

        assertThatThrownBy(() -> projectController.addUserToProject(localUser, "token", usernameMap))
            .isInstanceOf(ApiException.class)
            .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldAddUserToProjectUserNotFound() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Map<String, String> usernameMap = Collections.singletonMap("username", "username");

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);
        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.addUserToProject(localUser, "token", usernameMap))
            .isInstanceOf(ApiException.class)
            .hasMessage("User 'username' not found");
    }

    @Test
    void shouldAddUserToProject() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Map<String, String> usernameMap = Collections.singletonMap("username", "username");

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);
        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.of(user));
        when(projectService.createProjectForUser(any(), any()))
            .thenReturn(project);

        ResponseEntity<Void> actual = projectController.addUserToProject(localUser, "token", usernameMap);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldDeleteUserFromProjectNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.deleteUserFromProject(localUser, "token", 1L))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldDeleteUserFromProjectNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(false);

        assertThatThrownBy(() -> projectController.deleteUserFromProject(localUser, "token", 1L))
            .isInstanceOf(ApiException.class)
            .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldDeleteUserFromProjectUserNotFound() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);
        when(userService.getOne(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.deleteUserFromProject(localUser, "token", 1L))
            .isInstanceOf(ApiException.class)
            .hasMessage("User '1' not found");
    }

    @Test
    void shouldDeleteUserFromProject() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);
        when(userService.getOne(any()))
            .thenReturn(Optional.of(user));

        ResponseEntity<Void> actual = projectController.deleteUserFromProject(localUser, "token", 1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldGetProjectWebsocketClientsNotFound() {
        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectController.getProjectWebsocketClients("token"))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldGetProjectWebsocketClients() {
        Project project = new Project();
        project.setId(1L);

        WebsocketClient websocketClient = new WebsocketClient();
        websocketClient.setSessionId("1");

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(dashboardWebSocketService.getWebsocketClientsByProjectToken(any()))
            .thenReturn(Collections.singletonList(websocketClient));

        ResponseEntity<List<WebsocketClient>> actual = projectController.getProjectWebsocketClients("token");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).contains(websocketClient);
    }

    @Test
    void shouldGetAllForCurrentUser() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        ProjectResponseDto projectResponseDto = new ProjectResponseDto();
        projectResponseDto.setName("name");

        when(projectService.getAllByUser(any()))
            .thenReturn(Collections.singletonList(project));
        when(projectMapper.toProjectsDtos(any()))
            .thenReturn(Collections.singletonList(projectResponseDto));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ResponseEntity<List<ProjectResponseDto>> actual = projectController.getAllForCurrentUser(localUser);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).contains(projectResponseDto);
    }
}
