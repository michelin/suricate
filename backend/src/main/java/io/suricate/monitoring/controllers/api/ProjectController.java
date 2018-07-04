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

package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.controllers.api.error.exception.ApiException;
import io.suricate.monitoring.model.dto.project.ProjectDto;
import io.suricate.monitoring.model.dto.project.ProjectWidgetDto;
import io.suricate.monitoring.model.dto.project.ProjectWidgetPositionDto;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.model.mapper.project.ProjectMapper;
import io.suricate.monitoring.model.mapper.project.ProjectWidgetMapper;
import io.suricate.monitoring.service.api.ProjectService;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.service.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Project controller
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    /**
     * Project service
     */
    private final ProjectService projectService;

    /**
     * The project widget service
     */
    private final ProjectWidgetService projectWidgetService;

    /**
     * User service
     */
    private final UserService userService;

    /**
     * The mapper that transform domain/dto objects
     */
    private final ProjectMapper projectMapper;

    /**
     * Project widget mapper
     */
    private final ProjectWidgetMapper projectWidgetMapper;

    /**
     * Constructor for dependency injection
     *
     * @param projectService       The project service to inject
     * @param projectWidgetService The project widget service
     * @param userService          The user service to inject
     * @param projectMapper        The project mapper
     * @param projectWidgetMapper  The project widget mapper
     */
    @Autowired
    public ProjectController(final ProjectService projectService,
                             @Lazy final ProjectWidgetService projectWidgetService,
                             final UserService userService,
                             final ProjectMapper projectMapper,
                             final ProjectWidgetMapper projectWidgetMapper) {
        this.projectService = projectService;
        this.projectWidgetService = projectWidgetService;
        this.userService = userService;
        this.projectMapper = projectMapper;
        this.projectWidgetMapper = projectWidgetMapper;
    }

    /**
     * Get every project in database
     *
     * @return The whole list of projects
     */
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ProjectDto>> getAll() {

        return Optional
            .ofNullable(projectService.getAll())
            .map(projects ->
                ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .cacheControl(CacheControl.noCache())
                    .body(projectMapper.toProjectDtosDefault(projects))
            )
            .orElseGet(() ->
                ResponseEntity
                    .noContent()
                    .cacheControl(CacheControl.noCache())
                    .build()
            );
    }

    /**
     * Get projects for a user
     *
     * @param principal The connected user
     * @return The whole list of projects
     */
    @RequestMapping(value = "/currentUser", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<ProjectDto>> getAllForCurrentUser(Principal principal) {
        Optional<User> user = userService.getOneByUsername(principal.getName());

        if (!user.isPresent()) {
            LOGGER.debug("No user with username : {}", principal.getName());

            return ResponseEntity
                .notFound()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        List<Project> projects = projectService.getAllByUser(user.get());

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(projectMapper.toProjectDtosDefault(projects));
    }


    /**
     * Add a new project/dashboard for a user
     *
     * @param principal  The connected user
     * @param projectDto The project to add
     * @return The saved project
     */
    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> createProject(Principal principal, @RequestBody ProjectDto projectDto) {
        Optional<User> user = userService.getOneByUsername(principal.getName());

        if (!user.isPresent()) {
            return ResponseEntity
                .notFound()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        Project project = projectService.createProject(user.get(), projectMapper.toNewProject(projectDto));

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/projects/" + project.getId())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(projectMapper.toProjectDtoDefault(project));
    }

    /**
     * Update an existing project
     *
     * @param projectId  The project id to update
     * @param projectDto The informations to update
     * @return The project updated
     */
    @RequestMapping(value = "/{projectId}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable("projectId") Long projectId, @RequestBody ProjectDto projectDto) {
        Optional<Project> projectOptional = projectService.getOneById(projectId);

        if (!projectOptional.isPresent()) {
            return ResponseEntity
                .notFound()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        projectService.updateProject(
            projectOptional.get(),
            projectDto.getName(),
            projectDto.getWidgetHeight(),
            projectDto.getMaxColumn(),
            projectDto.getCssStyle()
        );
        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noCache())
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectMapper.toProjectDtoDefault(projectOptional.get()));
    }

    /**
     * Get a project by id
     *
     * @param id The id of the project
     * @return The project
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> getOneById(@PathVariable("id") Long id) {
        Optional<Project> project = projectService.getOneById(id);

        if (!project.isPresent()) {
            return ResponseEntity
                .notFound()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(projectMapper.toProjectDtoDefault(project.get()));
    }

    /**
     * Get a project by token
     *
     * @param token The token of the project
     * @return The project
     */
    @RequestMapping(value = "/project/{token}", method = RequestMethod.GET)
    public ResponseEntity<ProjectDto> getOneByToken(@PathVariable("token") String token) {
        Optional<Project> project = projectService.getOneByToken(token);

        if (!project.isPresent()) {
            return ResponseEntity
                .notFound()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(projectMapper.toProjectDtoDefault(project.get()));
    }

    /**
     * Method that delete a project
     *
     * @param projectId The project id to delete
     * @return The project deleted
     */
    @RequestMapping(value = "/{projectId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<ProjectDto> deleteOneById(@PathVariable("projectId") Long projectId) {
        Optional<Project> projectOptional = projectService.getOneById(projectId);

        if (!projectOptional.isPresent()) {
            return ResponseEntity
                .notFound()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        projectService.deleteProject(projectOptional.get());
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(projectMapper.toProjectDtoDefault(projectOptional.get()));
    }

    /**
     * Add a user to a project
     *
     * @param id          Id of the project
     * @param usernameMap Username of the user to add
     * @return The project
     */
    @RequestMapping(value = "/{id}/users", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> addUserToProject(@PathVariable("id") Long id,
                                                       @RequestBody Map<String, String> usernameMap) {
        Optional<User> user = userService.getOneByUsername(usernameMap.get("username"));
        Optional<Project> project = projectService.getOneById(id);

        if (!user.isPresent()) {
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }
        if (!project.isPresent()) {
            throw new ApiException(ApiErrorEnum.PROJECT_NOT_FOUND);
        }

        projectService.addUserToProject(user.get(), project.get());
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(projectMapper.toProjectDtoDefault(project.get()));
    }

    /**
     * Delete a user from a dashboard
     *
     * @param id     The project/dashboard id
     * @param userId The user id to delete
     * @return The project
     */
    @RequestMapping(value = "/{id}/users/{userId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> deleteUserToProject(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        Optional<User> user = userService.getOne(userId);
        Optional<Project> project = projectService.getOneById(id);

        if (!user.isPresent()) {
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }
        if (!project.isPresent()) {
            throw new ApiException(ApiErrorEnum.PROJECT_NOT_FOUND);
        }

        projectService.deleteUserFromProject(user.get(), project.get());

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(projectMapper.toProjectDtoDefault(project.get()));
    }

    /**
     * Add widget into the dashboard
     *
     * @param id               The project id
     * @param projectWidgetDto The projectWidget to add
     * @return The project
     */
    @RequestMapping(value = "/{id}/widgets", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> addWidgetToProject(@PathVariable("id") Long id,
                                                         @RequestBody ProjectWidgetDto projectWidgetDto) {
        ProjectWidget projectWidget = projectWidgetMapper.toNewProjectWidget(projectWidgetDto, id);
        projectWidgetService.addProjectWidget(projectWidget);

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/projects/" + projectWidget.getProject().getId())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(projectMapper.toProjectDtoDefault(projectWidget.getProject()));
    }

    /**
     * Update the list of widget positions for a project
     *
     * @param projectId                 The project id to update
     * @param projectWidgetPositionDtos The list of project widget positions
     * @return The project updated
     */
    @RequestMapping(value = "/{projectId}/projectWidgetPositions", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> updateProjectWidgetsPositionForProject(@PathVariable("projectId") Long projectId,
                                                                             @RequestBody List<ProjectWidgetPositionDto> projectWidgetPositionDtos) {
        Optional<Project> projectOptional = projectService.getOneById(projectId);

        if (!projectOptional.isPresent()) {
            return ResponseEntity
                .notFound()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        projectWidgetService.updateWidgetPositionByProject(projectOptional.get(), projectWidgetPositionDtos);
        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noCache())
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectMapper.toProjectDtoDefault(projectOptional.get()));
    }

    /**
     * Delete a project widget from a dashboard
     *
     * @param projectId       The project ID
     * @param projectWidgetId The project widget to delete
     * @return The dashboard updated
     */
    @RequestMapping(value = "{projectId}/projectWidgets/{projectWidgetId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> deleteProjectWidgetFromProject(@PathVariable("projectId") Long projectId,
                                                                     @PathVariable("projectWidgetId") Long projectWidgetId) {
        Optional<Project> projectOptional = projectService.getOneById(projectId);
        if (!projectOptional.isPresent()) {
            return ResponseEntity.notFound().cacheControl(CacheControl.noCache()).build();
        }

        projectWidgetService.removeWidgetFromDashboard(projectOptional.get(), projectWidgetId);

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(projectMapper.toProjectDtoDefault(projectOptional.get()));
    }

    /**
     * Edit a project widget for a project
     *
     * @param projectId        The project id
     * @param projectWidgetId  The project widget id
     * @param projectWidgetDto The project widget updated
     * @return The project updated
     */
    @RequestMapping(value = "{projectId}/projectWidgets/{projectWidgetId}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> editProjectWidgetFromProject(@PathVariable("projectId") Long projectId,
                                                                   @PathVariable("projectWidgetId") Long projectWidgetId,
                                                                   @RequestBody ProjectWidgetDto projectWidgetDto) {

        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.findByProjectIdAndProjectWidgetId(projectId, projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            return ResponseEntity.notFound().cacheControl(CacheControl.noCache()).build();
        }

        Optional<Project> projectOptional = projectService.getOneById(projectId);
        if (!projectOptional.isPresent()) {
            return ResponseEntity.notFound().cacheControl(CacheControl.noCache()).build();
        }
        ProjectWidget projectWidget = projectWidgetOptional.get();
        projectWidgetService.updateProjectWidget(projectWidget, projectWidgetDto.getCustomStyle(), projectWidgetDto.getBackendConfig());

        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noCache())
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectMapper.toProjectDtoDefault(projectOptional.get()));
    }
}
