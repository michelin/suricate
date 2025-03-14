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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetRequestDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.api.ProjectService;
import com.michelin.suricate.service.api.ProjectWidgetService;
import com.michelin.suricate.service.mapper.ProjectWidgetMapper;
import com.michelin.suricate.util.exception.ApiException;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class ProjectWidgetControllerTest {
    @Mock
    private ProjectWidgetService projectWidgetService;

    @Mock
    private ProjectWidgetMapper projectWidgetMapper;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectWidgetController projectWidgetController;

    @Test
    void shouldGetByIdNotFound() {
        when(projectWidgetService.getOne(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception =
                assertThrows(ObjectNotFoundException.class, () -> projectWidgetController.getById(1L));

        assertEquals("ProjectWidget '1' not found", exception.getMessage());
    }

    @Test
    void shouldGetById() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDto(any())).thenReturn(projectWidgetResponseDto);

        ResponseEntity<ProjectWidgetResponseDto> actual = projectWidgetController.getById(1L);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(projectWidgetResponseDto, actual.getBody());
    }

    @Test
    void shouldGetByProjectNotFound() {
        when(projectService.getOneByToken(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception =
                assertThrows(ObjectNotFoundException.class, () -> projectWidgetController.getByProject("token"));

        assertEquals("Project 'token' not found", exception.getMessage());
    }

    @Test
    void shouldGetByProjectNoGrid() {
        Project project = new Project();
        project.setId(1L);

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));

        ResponseEntity<List<ProjectWidgetResponseDto>> actual = projectWidgetController.getByProject("token");

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldGetByProject() {
        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setId(1L);
        project.setGrids(Collections.singleton(projectGrid));

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectWidgetMapper.toProjectWidgetsDtos(any()))
                .thenReturn(Collections.singletonList(projectWidgetResponseDto));

        ResponseEntity<List<ProjectWidgetResponseDto>> actual = projectWidgetController.getByProject("token");

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().contains(projectWidgetResponseDto));
    }

    @Test
    void shouldEditByProjectNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectWidgetService.getOne(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> projectWidgetController.editByProject(localUser, 1L, projectWidgetRequestDto));

        assertEquals("ProjectWidget '1' not found", exception.getMessage());
    }

    @Test
    void shouldEditByProjectNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setProjectGrid(projectGrid);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(false);

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> projectWidgetController.editByProject(localUser, 1L, projectWidgetRequestDto));

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
    }

    @Test
    void shouldEditByProject() {
        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setProjectGrid(projectGrid);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(true);
        when(projectWidgetMapper.toProjectWidgetDto(any())).thenReturn(projectWidgetResponseDto);

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ResponseEntity<ProjectWidgetResponseDto> actual =
                projectWidgetController.editByProject(localUser, 1L, projectWidgetRequestDto);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(projectWidgetResponseDto, actual.getBody());
    }

    @Test
    void shouldAddProjectWidgetToProjectNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectService.getOneByToken(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> projectWidgetController.addProjectWidgetToProject(
                        localUser, "token", 1L, projectWidgetRequestDto));

        assertEquals("Project 'token' not found", exception.getMessage());
    }

    @Test
    void shouldAddProjectWidgetToProjectNotAuthorized() {
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

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(false);

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> projectWidgetController.addProjectWidgetToProject(
                        localUser, "token", 1L, projectWidgetRequestDto));

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
    }

    @Test
    void shouldAddProjectWidgetToProjectGridNotFound() {
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

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(true);

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> projectWidgetController.addProjectWidgetToProject(
                        localUser, "token", 1L, projectWidgetRequestDto));

        assertEquals("Grid '1' not found for project token", exception.getMessage());
    }

    @Test
    void shouldAddProjectWidgetToProject() {
        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setGrids(Collections.singleton(projectGrid));

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(true);
        when(projectWidgetMapper.toProjectWidgetEntity(any(), any())).thenReturn(projectWidget);
        when(projectWidgetMapper.toProjectWidgetDto(any())).thenReturn(projectWidgetResponseDto);

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ResponseEntity<ProjectWidgetResponseDto> actual =
                projectWidgetController.addProjectWidgetToProject(localUser, "token", 1L, projectWidgetRequestDto);

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(projectWidgetResponseDto, actual.getBody());
    }

    @Test
    void shouldDeleteByIdNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectWidgetService.getOne(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception =
                assertThrows(ObjectNotFoundException.class, () -> projectWidgetController.deleteById(localUser, 1L));

        assertEquals("ProjectWidget '1' not found", exception.getMessage());
    }

    @Test
    void shouldDeleteByIdNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setProjectGrid(projectGrid);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(false);

        ApiException exception =
                assertThrows(ApiException.class, () -> projectWidgetController.deleteById(localUser, 1L));

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
    }

    @Test
    void shouldDeleteById() {
        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setProjectGrid(projectGrid);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(true);

        ResponseEntity<Void> actual = projectWidgetController.deleteById(localUser, 1L);

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }
}
