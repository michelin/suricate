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

import com.michelin.suricate.model.dto.websocket.UpdateEvent;
import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.enumeration.UpdateType;
import com.michelin.suricate.repository.ProjectRepository;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.specification.ProjectSearchSpecification;
import com.michelin.suricate.service.websocket.DashboardWebSocketService;
import com.michelin.suricate.util.SecurityUtils;
import com.michelin.suricate.util.logging.LogExecutionTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Project service.
 */
@Service
public class ProjectService {
    @Autowired
    @Qualifier("jasyptStringEncryptor")
    private StringEncryptor stringEncryptor;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DashboardWebSocketService dashboardWebsocketService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private ProjectGridService projectGridService;

    @Autowired
    private ProjectWidgetService projectWidgetService;

    /**
     * Get the list of projects.
     *
     * @param search   The search string
     * @param pageable The page configurations
     * @return The list paginated
     */
    @Transactional(readOnly = true)
    public Page<Project> getAll(String search, Pageable pageable) {
        return projectRepository.findAll(new ProjectSearchSpecification(search), pageable);
    }

    /**
     * Get all projects by user.
     *
     * @param user The user
     * @return A list of projects
     */
    @Transactional(readOnly = true)
    public List<Project> getAllByUser(User user) {
        return projectRepository.findByUsersIdOrderByName(user.getId());
    }

    /**
     * Get a project by the project id.
     *
     * @param id The id of the project
     * @return The project associated
     */
    @LogExecutionTime
    @Transactional(readOnly = true)
    public Optional<Project> getOneById(Long id) {
        return projectRepository.findById(id);
    }

    /**
     * Get a project by its token.
     *
     * @param token The token to find
     * @return The project
     */
    @Transactional(readOnly = true)
    public Optional<Project> getOneByToken(final String token) {
        return projectRepository.findProjectByToken(token);
    }

    /**
     * Create a new project.
     *
     * @param project The project to instantiate
     * @return The project instantiate
     */
    @Transactional
    public Project createProject(Project project) {
        if (StringUtils.isBlank(project.getToken())) {
            project.setToken(stringEncryptor.encrypt(UUID.randomUUID().toString()));
        }

        return projectRepository.save(project);
    }

    /**
     * Create a new project for a user.
     *
     * @param user    The user how create the project
     * @param project The project to instantiate
     * @return The project instantiate
     */
    @Transactional
    public Project createProjectForUser(User user, Project project) {
        project.getUsers().add(user);
        return createProject(project);
    }

    /**
     * Create or update a list of projects.
     *
     * @param projects      All the projects to create/update
     * @param connectedUser The connected user
     * @return The created/updated projects
     */
    @Transactional
    public List<Project> createUpdateProjects(List<Project> projects, User connectedUser) {
        for (Project project : projects) {
            Optional<Project> projectOptional = projectRepository.findProjectByToken(project.getToken());

            if (project.getScreenshot() != null) {
                if (projectOptional.isPresent() && projectOptional.get().getScreenshot() != null) {
                    project.getScreenshot().setId(projectOptional.get().getScreenshot().getId());
                }

                assetService.save(project.getScreenshot());
            }

            if (projectOptional.isPresent()) {
                project.setId(projectOptional.get().getId());
                project.setUsers(projectOptional.get().getUsers());
                createProject(project);
            } else {
                createProjectForUser(connectedUser, project);
            }

            project.getGrids().forEach(projectGrid -> {
                Optional<ProjectGrid> projectGridOptional =
                    projectGridService.findByIdAndProjectToken(projectGrid.getId(), project.getToken());
                if (projectGridOptional.isPresent()) {
                    projectGrid.setId(projectGridOptional.get().getId());
                } else {
                    // Reset id to not steal a grid from a project to another
                    projectGrid.setId(null);
                }

                projectGrid.setProject(project);
                projectGridService.create(projectGrid);

                projectGrid.getWidgets().forEach(projectWidget -> {
                    Optional<ProjectWidget> projectWidgetOptional =
                        projectWidgetService.findByIdAndProjectGridId(projectWidget.getId(), projectGrid.getId());
                    if (projectWidgetOptional.isPresent()) {
                        projectWidget.setId(projectWidgetOptional.get().getId());
                    } else {
                        // Reset id to not steal a grid from a project to another
                        projectWidget.setId(null);
                    }

                    projectWidget.setProjectGrid(projectGrid);
                    projectWidgetService.create(projectWidget);
                });
            });
        }

        return projectRepository.findAll(new ProjectSearchSpecification(StringUtils.EMPTY));
    }

    /**
     * Method used to update a project.
     *
     * @param project      the project to update
     * @param newName      the new name
     * @param widgetHeight The new widget height
     * @param maxColumn    The new max column
     * @param customCss    The custom CSS style
     */
    @Transactional
    public void updateProject(Project project, final String newName, final int widgetHeight, final int maxColumn,
                              final String customCss) {
        if (StringUtils.isNotBlank(newName)) {
            project.setName(newName);
        }

        if (widgetHeight > 0) {
            project.setWidgetHeight(widgetHeight);
        }

        if (maxColumn > 0) {
            project.setMaxColumn(maxColumn);
        }

        if (StringUtils.isNotBlank(customCss)) {
            project.setCssStyle(customCss);
        }

        projectRepository.save(project);

        // Update grid
        dashboardWebsocketService.sendEventToProjectSubscribers(project.getToken(),
            UpdateEvent.builder().type(UpdateType.REFRESH_DASHBOARD).build());
    }

    /**
     * Delete a user from a project.
     *
     * @param user    The user to delete
     * @param project The project related
     */
    public void deleteUserFromProject(User user, Project project) {
        project.getUsers().remove(user);
        projectRepository.save(project);
    }

    /**
     * Method used for retrieve a project token from a project id.
     *
     * @param projectId The project id
     * @return The related token
     */
    public String getTokenByProjectId(final Long projectId) {
        return projectRepository.getToken(projectId);
    }

    /**
     * Check if the connected user can access to this project.
     *
     * @param project       The project
     * @param connectedUser The connected user
     * @return True if he can, false otherwise
     */
    public boolean isConnectedUserCanAccessToProject(final Project project, final LocalUser connectedUser) {
        return SecurityUtils.isAdmin(connectedUser)
            || project.getUsers().stream()
            .anyMatch(currentUser -> currentUser.getUsername().equalsIgnoreCase(connectedUser.getUsername()));
    }

    /**
     * Method used to delete a project with its ID.
     *
     * @param project The project to delete
     */
    @Transactional
    public void deleteProject(Project project) {
        dashboardWebsocketService.sendEventToProjectSubscribers(
            project.getToken(),
            UpdateEvent.builder()
                .type(UpdateType.DISCONNECT)
                .build()
        );

        projectRepository.delete(project);
    }

    /**
     * Add or update a screenshot for a project.
     *
     * @param project     The project
     * @param content     The image content
     * @param contentType The image content type
     * @param size        The image size
     */
    public void addOrUpdateScreenshot(Project project, byte[] content, String contentType, long size) {
        Asset screenshotAsset = new Asset();
        screenshotAsset.setContent(content);
        screenshotAsset.setContentType(contentType);
        screenshotAsset.setSize(size);

        if (project.getScreenshot() != null) {
            screenshotAsset.setId(project.getScreenshot().getId());
            assetService.save(screenshotAsset);
        } else {
            assetService.save(screenshotAsset);
            project.setScreenshot(screenshotAsset);
            projectRepository.save(project);
        }
    }
}
