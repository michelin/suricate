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
package com.michelin.suricate.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.enumeration.UpdateType;
import com.michelin.suricate.repository.ProjectRepository;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.specification.ProjectSearchSpecification;
import com.michelin.suricate.service.websocket.DashboardWebSocketService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private DashboardWebSocketService dashboardWebsocketService;

    @Mock
    private AssetService assetService;

    @Mock
    private ProjectGridService projectGridService;

    @Mock
    private ProjectWidgetService projectWidgetService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void shouldGetAll() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findAll(any(ProjectSearchSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(project)));

        Page<Project> actual = projectService.getAll("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(project, actual.get().toList().getFirst());

        verify(projectRepository)
                .findAll(
                        Mockito.<ProjectSearchSpecification>argThat(
                                specification -> specification.getSearch().equals("search")
                                        && specification.getAttributes().isEmpty()),
                        Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldGetAllByUser() {
        Project project = new Project();
        project.setId(1L);

        User user = new User();
        user.setId(1L);

        when(projectRepository.findByUsersIdOrderByName(any())).thenReturn(Collections.singletonList(project));

        List<Project> actual = projectService.getAllByUser(user);

        assertFalse(actual.isEmpty());
        assertTrue(actual.contains(project));

        verify(projectRepository).findByUsersIdOrderByName(1L);
    }

    @Test
    void shouldGetOneById() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findById(any())).thenReturn(Optional.of(project));

        Optional<Project> actual = projectService.getOneById(1L);

        assertTrue(actual.isPresent());
        assertEquals(project, actual.get());

        verify(projectRepository).findById(1L);
    }

    @Test
    void shouldGetOneByToken() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findProjectByToken(any())).thenReturn(Optional.of(project));

        Optional<Project> actual = projectService.getOneByToken("token");

        assertTrue(actual.isPresent());
        assertEquals(project, actual.get());

        verify(projectRepository).findProjectByToken("token");
    }

    @Test
    void shouldGetTokenByProjectId() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.getToken(any())).thenReturn("token");

        String actual = projectService.getTokenByProjectId(1L);

        assertEquals("token", actual);

        verify(projectRepository).getToken(1L);
    }

    @Test
    void shouldCreateProjectNoToken() {
        Project project = new Project();
        project.setId(1L);

        when(stringEncryptor.encrypt(any())).thenReturn("encrypted");
        when(projectRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        Project actual = projectService.createProject(project);

        assertNotNull(actual);
        assertEquals("encrypted", actual.getToken());

        verify(stringEncryptor).encrypt(any(String.class));
        verify(projectRepository).save(project);
    }

    @Test
    void shouldCreateProject() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(projectRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        Project actual = projectService.createProject(project);

        assertNotNull(actual);
        assertEquals("token", actual.getToken());

        verify(stringEncryptor, never()).encrypt(any(String.class));
        verify(projectRepository).save(project);
    }

    @Test
    void shouldCreateProjectForUser() {
        User user = new User();
        user.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(projectRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        Project actual = projectService.createProjectForUser(user, project);

        assertNotNull(actual);
        assertEquals(1, actual.getUsers().size());
        assertTrue(actual.getUsers().contains(user));

        verify(stringEncryptor, never()).encrypt(any(String.class));
        verify(projectRepository).save(project);
    }

    @Test
    void shouldCreateProjectsWithNoScreenshot() {
        User user = new User();
        user.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");
        project.setGrids(Collections.singleton(projectGrid));

        when(projectRepository.findProjectByToken(any())).thenReturn(Optional.empty());
        when(projectGridService.findByIdAndProjectToken(any(), any())).thenReturn(Optional.empty());
        when(projectGridService.create(any())).thenAnswer(answer -> answer.getArgument(0));
        when(projectWidgetService.findByIdAndProjectGridId(any(), any())).thenReturn(Optional.empty());
        when(projectWidgetService.create(any())).thenAnswer(answer -> answer.getArgument(0));
        when(projectRepository.findAll(any())).thenReturn(Collections.singletonList(project));

        List<Project> actual = projectService.createUpdateProjects(Collections.singletonList(project), user);

        assertEquals(1, actual.size());
        assertTrue(actual.contains(project));

        verify(projectRepository).findProjectByToken("token");
        verify(projectGridService).findByIdAndProjectToken(1L, "token");
        verify(projectGridService).create(projectGrid);
        verify(projectWidgetService).findByIdAndProjectGridId(1L, null);
        verify(projectRepository)
                .findAll(Mockito.<ProjectSearchSpecification>argThat(
                        specification -> specification.getSearch().equals(StringUtils.EMPTY)
                                && specification.getAttributes().isEmpty()));
    }

    @Test
    void shouldUpdateProjects() {
        User user = new User();
        user.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setToken("token");
        project.setScreenshot(new Asset());
        project.setGrids(Collections.singleton(projectGrid));

        User existingUser = new User();
        existingUser.setId(1L);

        Asset existingAsset = new Asset();
        existingAsset.setId(1L);

        ProjectWidget existingProjectWidget = new ProjectWidget();
        existingProjectWidget.setId(1L);

        ProjectGrid existingProjectGrid = new ProjectGrid();
        existingProjectGrid.setId(1L);
        existingProjectGrid.setWidgets(Collections.singleton(existingProjectWidget));

        Project existingProject = new Project();
        existingProject.setId(1L);
        existingProject.setToken("token");
        existingProject.setUsers(Collections.singleton(existingUser));
        existingProject.setScreenshot(existingAsset);
        existingProject.setGrids(Collections.singleton(existingProjectGrid));

        when(projectRepository.findProjectByToken(any())).thenReturn(Optional.of(existingProject));
        when(projectGridService.findByIdAndProjectToken(any(), any())).thenReturn(Optional.of(existingProjectGrid));
        when(projectGridService.create(any())).thenAnswer(answer -> answer.getArgument(0));
        when(projectWidgetService.findByIdAndProjectGridId(any(), any()))
                .thenReturn(Optional.of(existingProjectWidget));
        when(projectWidgetService.create(any())).thenAnswer(answer -> answer.getArgument(0));
        when(projectRepository.findAll(any())).thenReturn(Collections.singletonList(project));

        List<Project> actual = projectService.createUpdateProjects(Collections.singletonList(project), user);

        assertEquals(1, actual.size());
        assertTrue(actual.contains(project));
        assertEquals(1L, actual.getFirst().getId());
        assertEquals(1L, actual.getFirst().getScreenshot().getId());
        assertIterableEquals(
                Collections.singleton(existingUser), actual.getFirst().getUsers());
        assertEquals(
                1L, new ArrayList<>(actual.getFirst().getGrids()).getFirst().getId());
        assertEquals(
                1L,
                new ArrayList<>(new ArrayList<>(actual.getFirst().getGrids())
                                .getFirst()
                                .getWidgets())
                        .getFirst()
                        .getId());

        verify(projectRepository).findProjectByToken("token");
        verify(projectGridService).findByIdAndProjectToken(1L, "token");
        verify(projectGridService).create(projectGrid);
        verify(projectWidgetService).findByIdAndProjectGridId(1L, 1L);
        verify(projectRepository)
                .findAll(Mockito.<ProjectSearchSpecification>argThat(
                        specification -> specification.getSearch().equals(StringUtils.EMPTY)
                                && specification.getAttributes().isEmpty()));
    }

    @Test
    void shouldUpdateProject() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(projectRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        projectService.updateProject(project, "newName", 1, 1, "css");

        assertEquals("newName", project.getName());
        assertEquals(1, project.getWidgetHeight());
        assertEquals(1, project.getMaxColumn());
        assertEquals("css", project.getCssStyle());

        verify(projectRepository).save(project);
        verify(dashboardWebsocketService)
                .sendEventToProjectSubscribers(
                        eq("token"),
                        argThat(event ->
                                event.getType().equals(UpdateType.REFRESH_DASHBOARD) && event.getDate() != null));
    }

    @Test
    void shouldUpdateProjectNullInputs() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(projectRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        projectService.updateProject(project, null, 0, 0, null);

        assertNull(project.getName());
        assertNull(project.getWidgetHeight());
        assertNull(project.getMaxColumn());
        assertNull(project.getCssStyle());

        verify(projectRepository).save(project);
        verify(dashboardWebsocketService)
                .sendEventToProjectSubscribers(
                        eq("token"),
                        argThat(event ->
                                event.getType().equals(UpdateType.REFRESH_DASHBOARD) && event.getDate() != null));
    }

    @Test
    void shouldDeleteUserFromProject() {
        User user = new User();
        user.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setUsers(new HashSet<>(Collections.singletonList(user)));

        when(projectRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        projectService.deleteUserFromProject(user, project);
        assertTrue(project.getUsers().isEmpty());

        verify(projectRepository).save(project);
    }

    @Test
    void shouldConnectedUserAccessToProjectBecauseAdmin() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Project project = new Project();
        project.setId(1L);

        boolean actual = projectService.isConnectedUserCanAccessToProject(project, localUser);

        assertTrue(actual);
    }

    @Test
    void shouldConnectedUserAccessToProject() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Project project = new Project();
        project.setId(1L);
        project.setUsers(Collections.singleton(user));

        boolean actual = projectService.isConnectedUserCanAccessToProject(project, localUser);

        assertTrue(actual);
    }

    @Test
    void shouldConnectedUserNotAccessToProject() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Project project = new Project();
        project.setId(1L);

        boolean actual = projectService.isConnectedUserCanAccessToProject(project, localUser);

        assertFalse(actual);
    }

    @Test
    void shouldDeleteProject() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        projectService.deleteProject(project);

        verify(dashboardWebsocketService)
                .sendEventToProjectSubscribers(
                        eq("token"),
                        argThat(event -> event.getType().equals(UpdateType.DISCONNECT) && event.getDate() != null));
        verify(projectRepository).delete(project);
    }

    @Test
    void shouldAddScreenshot() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(assetService.save(any())).thenAnswer(answer -> answer.getArgument(0));
        when(projectRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        projectService.addOrUpdateScreenshot(project, new byte[10], "contentType", 10L);

        verify(assetService)
                .save(argThat(asset -> asset.getSize() == 10L
                        && asset.getContentType().equals("contentType")
                        && Arrays.equals(asset.getContent(), new byte[10])));
        verify(projectRepository)
                .save(argThat(createdProject -> createdProject.getId().equals(1L)
                        && createdProject.getToken().equals("token")
                        && Arrays.equals(createdProject.getScreenshot().getContent(), new byte[10])
                        && createdProject.getScreenshot().getSize() == 10L
                        && createdProject.getScreenshot().getContentType().equals("contentType")));
    }

    @Test
    void shouldUpdateScreenshot() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");
        project.setScreenshot(new Asset());

        when(assetService.save(any())).thenAnswer(answer -> answer.getArgument(0));

        projectService.addOrUpdateScreenshot(project, new byte[10], "contentType", 10L);

        verify(assetService)
                .save(argThat(asset -> asset.getSize() == 10L
                        && asset.getContentType().equals("contentType")
                        && Arrays.equals(asset.getContent(), new byte[10])));
        verify(projectRepository, never()).save(any());
    }
}
