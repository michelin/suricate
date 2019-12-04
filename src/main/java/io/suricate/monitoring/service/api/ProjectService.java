/*
 * Copyright 2012-2018 the original author or authors.
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

package io.suricate.monitoring.service.api;

import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.service.specification.ProjectSearchSpecification;
import io.suricate.monitoring.service.websocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.SecurityUtils;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service used to manage projects
 */
@Service
public class ProjectService {

    /**
     * String encryptor (mainly used for SECRET widget params)
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
     * Constructor
     *
     * @param stringEncryptor           The string encryptor to inject
     * @param projectRepository         The project repository to inject
     * @param dashboardWebSocketService The dashboard web socket service to inject
     * @param assetService              The asset service
     */
    @Autowired
    public ProjectService(@Qualifier("jasyptStringEncryptor") final StringEncryptor stringEncryptor,
                          final ProjectRepository projectRepository,
                          final DashboardWebSocketService dashboardWebSocketService,
                          final AssetService assetService) {

        this.stringEncryptor = stringEncryptor;
        this.projectRepository = projectRepository;
        this.dashboardWebsocketService = dashboardWebSocketService;
        this.assetService = assetService;
    }

    /**
     * Get the list of projects
     *
     * @param search   The search string
     * @param pageable The page configurations
     * @return The list paginated
     */
    public Page<Project> getAll(String search, Pageable pageable) {
        return projectRepository.findAll(new ProjectSearchSpecification(search), pageable);
    }

    /**
     * Retrieve all the project for a user
     *
     * @param user The user
     * @return The project list associated to the user
     */
    @Transactional
    public List<Project> getAllByUser(User user) {
        return projectRepository.findByUsers_IdOrderByName(user.getId());
    }

    /**
     * Get a project by the project id
     *
     * @param id The id of the project
     * @return The project associated
     */
    @LogExecutionTime
    @Transactional
    public Optional<Project> getOneById(Long id) {
        return projectRepository.findById(id);
    }

    /**
     * Get a project by it's token
     *
     * @param token The token to find
     * @return The project
     */
    @Transactional
    public Optional<Project> getOneByToken(final String token) {
        return projectRepository.findProjectByToken(token);
    }

    /**
     * Create a new project for a user
     *
     * @param user    The user how create the project
     * @param project The project to instantiate
     * @return The project instantiate
     */
    @Transactional
    public Project createProject(User user, Project project) {
        project.getUsers().add(user);

        if (StringUtils.isBlank(project.getToken())) {
            project.setToken(stringEncryptor.encrypt(UUID.randomUUID().toString()));
        }

        return projectRepository.save(project);
    }

    /**
     * Method used to update a project
     *
     * @param project      the project to update
     * @param newName      the new name
     * @param widgetHeight The new widget height
     * @param maxColumn    The new max column
     */
    @Transactional
    public void updateProject(Project project,
                              final String newName,
                              final int widgetHeight,
                              final int maxColumn,
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
        dashboardWebsocketService.updateGlobalScreensByProjectToken(project.getToken(), new UpdateEvent(UpdateType.GRID));
    }

    /**
     * Add a user to a project
     *
     * @param user    The user to add
     * @param project The project to edit
     * @return The project with the user
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
     * @return The project with user deleted
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
     *
     * @param project        The project
     * @param authentication The connected user
     * @return True if he can, false otherwise
     */
    public boolean isConnectedUserCanAccessToProject(final Project project, final Authentication authentication) {
        return SecurityUtils.isAdmin(authentication)
            || project.getUsers().stream().anyMatch(currentUser -> currentUser.getUsername().equalsIgnoreCase(authentication.getName()));
    }

    /**
     * Method used to delete a project with his ID
     *
     * @param project the project to delete
     */
    @Transactional
    public void deleteProject(Project project) {
        // notify clients
        dashboardWebsocketService.updateGlobalScreensByProjectToken(project.getToken(), new UpdateEvent(UpdateType.DISCONNECT));
        // delete project
        projectRepository.delete(project);
    }

    /**
     * Add or update a screenshot for a project
     *
     * @param project    The project
     * @param screenshot The screenshot to add
     */
    public void addOrUpdateScreenshot(Project project, MultipartFile screenshot) throws IOException {
        Asset screenshotAsset = new Asset();
        screenshotAsset.setContent(screenshot.getBytes());
        screenshotAsset.setContentType(screenshot.getContentType());
        screenshotAsset.setSize(screenshot.getSize());

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
