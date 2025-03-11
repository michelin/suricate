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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridRequestDto;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridResponseDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.api.ProjectGridService;
import com.michelin.suricate.service.api.ProjectService;
import com.michelin.suricate.service.mapper.ProjectGridMapper;
import com.michelin.suricate.util.exception.ApiException;
import com.michelin.suricate.util.exception.GridNotFoundException;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProjectGridControllerTest {
    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectGridService projectGridService;

    @Mock
    private ProjectGridMapper projectGridMapper;

    @InjectMocks
    private ProjectGridController projectGridController;

    @Test
    void shouldCreateNotFound() {
        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class, () -> projectGridController.create(localUser, "token", gridRequestDto));

        assertEquals("Project 'token' not found", exception.getMessage());
    }

    @Test
    void shouldCreateNotAuthorized() {
        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(1L);

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

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(false);

        ApiException exception = assertThrows(
                ApiException.class, () -> projectGridController.create(localUser, "token", gridRequestDto));

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
    }

    @Test
    void shouldCreate() {
        ProjectGridResponseDto projectGridResponseDto = new ProjectGridResponseDto();
        projectGridResponseDto.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(1L);

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

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(true);
        when(projectGridMapper.toProjectGridEntity(any(), any())).thenReturn(projectGrid);
        when(projectGridService.create(any())).thenAnswer(answer -> answer.getArgument(0));
        when(projectGridMapper.toProjectGridDto(any())).thenReturn(projectGridResponseDto);

        ResponseEntity<ProjectGridResponseDto> actual =
                projectGridController.create(localUser, "token", gridRequestDto);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(projectGridResponseDto, actual.getBody());
    }

    @Test
    void shouldUpdateProjectGridsNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any())).thenReturn(Optional.empty());

        ProjectGridRequestDto projectGridRequestDto = new ProjectGridRequestDto();

        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> projectGridController.updateProjectGrids(localUser, "token", projectGridRequestDto));

        assertEquals("Project 'token' not found", exception.getMessage());
    }

    @Test
    void shouldUpdateProjectGridsNotAuthorized() {
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

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(false);

        ProjectGridRequestDto projectGridRequestDto = new ProjectGridRequestDto();

        ApiException exception = assertThrows(
                ApiException.class,
                () -> projectGridController.updateProjectGrids(localUser, "token", projectGridRequestDto));

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
    }

    @Test
    void shouldUpdateProjectGridsGridNotFound() {
        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(2L);

        ProjectGridRequestDto projectGridRequestDto = new ProjectGridRequestDto();
        projectGridRequestDto.setGrids(Collections.singletonList(gridRequestDto));

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

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(true);

        GridNotFoundException exception = assertThrows(
                GridNotFoundException.class,
                () -> projectGridController.updateProjectGrids(localUser, "token", projectGridRequestDto));

        assertEquals("Grid '2' not found for project token", exception.getMessage());
    }

    @Test
    void shouldUpdateProjectGrids() {
        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(1L);

        ProjectGridRequestDto projectGridRequestDto = new ProjectGridRequestDto();
        projectGridRequestDto.setGrids(Collections.singletonList(gridRequestDto));

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

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(true);

        ResponseEntity<Void> actual =
                projectGridController.updateProjectGrids(localUser, "token", projectGridRequestDto);

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldDeleteGridByIdNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class, () -> projectGridController.deleteGridById(localUser, "token", 1L));

        assertEquals("Project 'token' not found", exception.getMessage());
    }

    @Test
    void shouldDeleteGridByIdNotAuthorized() {
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

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(false);

        ApiException exception =
                assertThrows(ApiException.class, () -> projectGridController.deleteGridById(localUser, "token", 1L));

        assertEquals("The user is not allowed to modify this project", exception.getMessage());
    }

    @Test
    void shouldDeleteGridByIdGridNotFound() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(2L);

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

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(true);

        GridNotFoundException exception = assertThrows(
                GridNotFoundException.class, () -> projectGridController.deleteGridById(localUser, "token", 1L));

        assertEquals("Grid '1' not found for project token", exception.getMessage());
    }

    @Test
    void shouldDeleteGridById() {
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

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any())).thenReturn(true);

        ResponseEntity<Void> actual = projectGridController.deleteGridById(localUser, "token", 1L);

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }
}
