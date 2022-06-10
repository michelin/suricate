/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entities.*;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.repositories.ProjectRepository;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.specifications.ProjectSearchSpecification;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.SecurityUtils;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manage the projects
 */
@Service
public class ProjectService {
    /**
     * String encryptor
     */
    private final StringEncryptor stringEncryptor;

    /**
     * Project repository
     */
    private final ProjectRepository projectRepository;

    /**
     * dashboard Socket service
     */
    private final DashboardWebSocketService dashboardWebsocketService;

    /**
     * The asset service to inject
     */
    private final AssetService assetService;

    /**
     * The project grid service
     */
    private final ProjectGridService projectGridService;

    /**
     * The project widget service
     */
    private final ProjectWidgetService projectWidgetService;

    /**
     * Constructor
     *
     * @param stringEncryptor           The string encryptor to inject
     * @param projectRepository         The project repository to inject
     * @param dashboardWebSocketService The dashboard web socket service to inject
     * @param assetService              The asset service
     * @param projectGridService        The project grid service
     * @param projectWidgetService      The project widget service
     */
    @Autowired
    public ProjectService(@Qualifier("jasyptStringEncryptor") final StringEncryptor stringEncryptor,
                          final ProjectRepository projectRepository,
                          final DashboardWebSocketService dashboardWebSocketService,
                          final AssetService assetService,
                          final ProjectGridService projectGridService,
                          final ProjectWidgetService projectWidgetService) {
        this.stringEncryptor = stringEncryptor;
        this.projectRepository = projectRepository;
        this.dashboardWebsocketService = dashboardWebSocketService;
        this.assetService = assetService;
        this.projectGridService = projectGridService;
        this.projectWidgetService = projectWidgetService;
    }

    /**
     * Get the list of projects
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
     * Get all projects by user
     *
     * @param user The user
     * @return A list of projects
     */
    @Transactional(readOnly = true)
    public List<Project> getAllByUser(User user) {
        return projectRepository.findByUsersIdOrderByName(user.getId());
    }

    /**
     * Get a project by the project id
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
     * Get a project by its token
     *
     * @param token The token to find
     * @return The project
     */
    @Transactional(readOnly = true)
    public Optional<Project> getOneByToken(final String token) {
        return projectRepository.findProjectByToken(token);
    }

    /**
     * Create a new project
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
     * Create a new project for a user
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
     * Create or update a list of projects
     * @param projects All the projects to create/update
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
                Optional<ProjectGrid> projectGridOptional = projectGridService.findByIdAndProjectToken(projectGrid.getId(), project.getToken());
                if (projectGridOptional.isPresent()) {
                    projectGrid.setId(projectGridOptional.get().getId());
                } else {
                    // Reset id to not steal a grid from a project to another
                    projectGrid.setId(null);
                }

                projectGrid.setProject(project);
                projectGridService.create(projectGrid);

                projectGrid.getWidgets().forEach(projectWidget -> {
                    Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.findByIdAndProjectGridId(projectWidget.getId(), projectGrid.getId());
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
     * Method used to update a project
     *
     * @param project      the project to update
     * @param newName      the new name
     * @param widgetHeight The new widget height
     * @param maxColumn    The new max column
     * @param customCSS    The custom CSS style
     */
    @Transactional
    public void updateProject(Project project, final String newName, final int widgetHeight, final int maxColumn,
                              final String customCSS) {
        if (StringUtils.isNotBlank(newName)) {
            project.setName(newName);
        }

        if (widgetHeight > 0) {
            project.setWidgetHeight(widgetHeight);
        }

        if (maxColumn > 0) {
            project.setMaxColumn(maxColumn);
        }

        if (StringUtils.isNotBlank(customCSS)) {
            project.setCssStyle(customCSS);
        }

        projectRepository.save(project);

        // Update grid
        dashboardWebsocketService.sendEventToProjectSubscribers(project.getToken(), UpdateEvent.builder().type(UpdateType.REFRESH_DASHBOARD).build());
    }

    /**
     * Add a user to a project
     *
     * @param user    The user to add
     * @param project The project to edit
     */
    @Transactional
    public void addUserToProject(User user, Project project) {
        project.getUsers().add(user);
        projectRepository.save(project);
    }

    /**
     * Delete a user from a project
     *
     * @param user    The user to delete
     * @param project The project related
     */
    public void deleteUserFromProject(User user, Project project) {
        project.getUsers().remove(user);
        projectRepository.save(project);
    }

    /**
     * Method used for retrieve a project token from a project id
     *
     * @param projectId The project id
     * @return The related token
     */
    public String getTokenByProjectId(final Long projectId) {
        return projectRepository.getToken(projectId);
    }

    /**
     * Check if the connected user can access to this project
     * @param project        The project
     * @param connectedUser The connected user
     * @return True if he can, false otherwise
     */
    public boolean isConnectedUserCanAccessToProject(final Project project, final LocalUser connectedUser) {
        return SecurityUtils.isAdmin(connectedUser)
            || project.getUsers().stream().anyMatch(currentUser -> currentUser.getUsername().equalsIgnoreCase(connectedUser.getUsername()));
    }

    /**
     * Method used to delete a project with its ID
     *
     * @param project The project to delete
     */
    @Transactional
    public void deleteProject(Project project) {
        this.dashboardWebsocketService.sendEventToProjectSubscribers(project.getToken(), UpdateEvent.builder().type(UpdateType.DISCONNECT).build());

        this.projectRepository.delete(project);
    }

    /**
     * Add or update a screenshot for a project
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
            this.assetService.save(screenshotAsset);
        } else {
            this.assetService.save(screenshotAsset);
            project.setScreenshot(screenshotAsset);
            this.projectRepository.save(project);
        }
    }
}
