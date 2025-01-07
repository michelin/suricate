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

package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.project.ProjectRequestDto;
import com.michelin.suricate.model.dto.api.project.ProjectResponseDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetPositionRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.dto.websocket.WebsocketClient;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.api.ProjectGridService;
import com.michelin.suricate.service.api.ProjectService;
import com.michelin.suricate.service.api.ProjectWidgetService;
import com.michelin.suricate.service.api.UserService;
import com.michelin.suricate.service.mapper.ProjectGridMapper;
import com.michelin.suricate.service.mapper.ProjectMapper;
import com.michelin.suricate.service.mapper.UserMapper;
import com.michelin.suricate.service.websocket.DashboardWebSocketService;
import com.michelin.suricate.util.exception.ApiException;
import com.michelin.suricate.util.exception.InvalidFileException;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.get().count());
        assertEquals(projectResponseDto, actual.get().toList().getFirst());
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

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> projectController.createProject(localUser, projectRequestDto)
        );

        assertEquals("User 'username' not found", exception.getMessage());
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

        assertEquals(MediaType.APPLICATION_JSON, actual.getHeaders().getContentType());
        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(projectResponseDto, actual.getBody());
    }

    @Test
    void shouldGetOneByTokenNotFound() {
        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> projectController.getOneByToken("token")
        );

        assertEquals("Project 'token' not found", exception.getMessage());
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

        assertEquals(MediaType.APPLICATION_JSON, actual.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(projectResponseDto, actual.getBody());
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

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> projectController.updateProject(localUser, "token", projectRequestDto)
        );

        assertEquals("Project 'token' not found", exception.getMessage());
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

        ApiException exception = assertThrows(
            ApiException.class,
            () -> projectController.updateProject(localUser, "token", projectRequestDto)
        );

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
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

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
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

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> projectController.updateProjectScreenshot(localUser, "token", file)
        );

        assertEquals("Project 'token' not found", exception.getMessage());
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

        ApiException exception = assertThrows(
            ApiException.class,
            () -> projectController.updateProjectScreenshot(localUser, "token", file)
        );

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
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

        InvalidFileException exception = assertThrows(
            InvalidFileException.class,
            () -> projectController.updateProjectScreenshot(localUser, "token", file)
        );

        assertEquals("The file originalName cannot be read for entity Project '1'", exception.getMessage());
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

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
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

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> projectController.deleteProjectById(localUser, "token")
        );

        assertEquals("Project 'token' not found", exception.getMessage());
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

        ApiException exception = assertThrows(
            ApiException.class,
            () -> projectController.deleteProjectById(localUser, "token")
        );

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
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

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
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

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> projectController.updateProjectWidgetsPositionForProject(
                localUser,
                "token",
                projectWidgetPositionRequestDtos
            )
        );

        assertEquals("Project 'token' not found", exception.getMessage());
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

        ApiException exception = assertThrows(
            ApiException.class,
            () -> projectController.updateProjectWidgetsPositionForProject(
                localUser,
                "token",
                projectWidgetPositionRequestDtos
            )
        );

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
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

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldGetProjectUsersNotFound() {
        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> projectController.getProjectUsers("token")
        );

        assertEquals("Project 'token' not found", exception.getMessage());
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

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().contains(userResponseDto));
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

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> projectController.addUserToProject(localUser, "token", usernameMap)
        );

        assertEquals("Project 'token' not found", exception.getMessage());
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

        ApiException exception = assertThrows(
            ApiException.class,
            () -> projectController.addUserToProject(localUser, "token", usernameMap)
        );

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
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

        ApiException exception = assertThrows(
            ApiException.class,
            () -> projectController.addUserToProject(localUser, "token", usernameMap)
        );

        assertEquals("User 'username' not found", exception.getMessage());
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

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNull(actual.getBody());
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

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> projectController.deleteUserFromProject(localUser, "token", 1L)
        );

        assertEquals("Project 'token' not found", exception.getMessage());
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

        ApiException exception = assertThrows(
            ApiException.class,
            () -> projectController.deleteUserFromProject(localUser, "token", 1L)
        );

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
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

        ApiException exception = assertThrows(
            ApiException.class,
            () -> projectController.deleteUserFromProject(localUser, "token", 1L)
        );

        assertEquals("User '1' not found", exception.getMessage());
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

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldGetProjectWebsocketClientsNotFound() {
        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> projectController.getProjectWebsocketClients("token")
        );

        assertEquals("Project 'token' not found", exception.getMessage());
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

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().contains(websocketClient));
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

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().contains(projectResponseDto));
    }
}
