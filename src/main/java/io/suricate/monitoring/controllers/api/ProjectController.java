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

import io.suricate.monitoring.model.dto.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.project.ProjectDto;
import io.suricate.monitoring.model.dto.project.ProjectWidgetDto;
import io.suricate.monitoring.model.dto.project.ProjectWidgetPositionDto;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.model.mapper.project.ProjectMapper;
import io.suricate.monitoring.model.mapper.project.ProjectWidgetMapper;
import io.suricate.monitoring.service.api.ProjectService;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.service.api.UserService;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
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
import springfox.documentation.annotations.ApiIgnore;

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
@Api(value = "Project Controller", tags = {"Project"})
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
    @ApiOperation(value = "Get the full list of projects", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
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
            .orElseGet(() -> {
                throw new NoContentException(Project.class);
            });
    }

    /**
     * Get projects for a user
     *
     * @param principal The connected user
     * @return The whole list of projects
     */
    @ApiOperation(value = "Get the list of projects related to the current user", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Current user not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/currentUser", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<ProjectDto>> getAllForCurrentUser(@ApiIgnore Principal principal) {
        Optional<User> user = userService.getOneByUsername(principal.getName());

        if (!user.isPresent()) {
            throw new ObjectNotFoundException(User.class, principal.getName());
        }

        List<Project> projects = projectService.getAllByUser(user.get());
        if (projects == null || projects.isEmpty()) {
            throw new NoContentException(Project.class);
        }

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
    @ApiOperation(value = "Create a new project for the current user", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Current user not found", response = ApiErrorDto.class)
    })
    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> createProject(@ApiIgnore Principal principal,
                                                    @ApiParam(name = "projectDto", value = "The project information", required = true)
                                                    @RequestBody ProjectDto projectDto) {
        Optional<User> user = userService.getOneByUsername(principal.getName());

        if (!user.isPresent()) {
            throw new ObjectNotFoundException(User.class, principal.getName());
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
    @ApiOperation(value = "Update an existing project by the project id", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{projectId}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> updateProject(@ApiParam(name = "projectId", value = "The project id", required = true)
                                                    @PathVariable("projectId") Long projectId,
                                                    @ApiParam(name = "projectDto", value = "The project information", required = true)
                                                    @RequestBody ProjectDto projectDto) {
        Optional<Project> projectOptional = projectService.getOneById(projectId);

        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectId);
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
     * @param projectId The id of the project
     * @return The project
     */
    @ApiOperation(value = "Retrieve the project information by id", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> getOneById(@ApiParam(name = "projectId", value = "The project id", required = true)
                                                 @PathVariable("projectId") Long projectId) {
        Optional<Project> project = projectService.getOneById(projectId);

        if (!project.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectId);
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
    @ApiOperation(value = "Retrieve the project information by the project token", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/project/{token}", method = RequestMethod.GET)
    public ResponseEntity<ProjectDto> getOneByToken(@ApiParam(name = "token", value = "The project token", required = true)
                                                    @PathVariable("token") String token) {
        Optional<Project> project = projectService.getOneByToken(token);

        if (!project.isPresent()) {
            throw new ObjectNotFoundException(Project.class, token);
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
    @ApiOperation(value = "Delete a project by the project id", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{projectId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<ProjectDto> deleteOneById(@ApiParam(name = "projectId", value = "The project id", required = true)
                                                    @PathVariable("projectId") Long projectId) {
        Optional<Project> projectOptional = projectService.getOneById(projectId);

        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectId);
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
     * @param projectId   Id of the project
     * @param usernameMap Username of the user to add
     * @return The project
     */
    @ApiOperation(value = "Add a user to a project", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{projectId}/users", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> addUserToProject(@ApiParam(name = "projectId", value = "The project id", required = true)
                                                       @PathVariable("projectId") Long projectId,
                                                       @ApiParam(name = "usernameMap", value = "A map with the username", required = true)
                                                       @RequestBody Map<String, String> usernameMap) {
        Optional<User> user = userService.getOneByUsername(usernameMap.get("username"));
        Optional<Project> project = projectService.getOneById(projectId);

        if (!user.isPresent()) {
            throw new ObjectNotFoundException(User.class, usernameMap.get("username"));
        }
        if (!project.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectId);
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
     * @param projectId The project/dashboard id
     * @param userId    The user id to delete
     * @return The project
     */
    @ApiOperation(value = "Delete a user for a project", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{projectId}/users/{userId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> deleteUserToProject(@ApiParam(name = "projectId", value = "The project id", required = true)
                                                          @PathVariable("projectId") Long projectId,
                                                          @ApiParam(name = "projectId", value = "The user id", required = true)
                                                          @PathVariable("userId") Long userId) {
        Optional<User> user = userService.getOne(userId);
        Optional<Project> project = projectService.getOneById(projectId);

        if (!user.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }
        if (!project.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectId);
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
     * @param projectId        The project id
     * @param projectWidgetDto The projectWidget to add
     * @return The project
     */
    @ApiOperation(value = "Add a new widget to a project", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{projectId}/widgets", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> addWidgetToProject(@ApiParam(name = "projectId", value = "The project id", required = true)
                                                         @PathVariable("projectId") Long projectId,
                                                         @ApiParam(name = "projectWidgetDto", value = "The project widget info's", required = true)
                                                         @RequestBody ProjectWidgetDto projectWidgetDto) {
        if (!this.projectService.isProjectExists(projectId)) {
            throw new ObjectNotFoundException(Project.class, projectId);
        }

        ProjectWidget projectWidget = projectWidgetMapper.toNewProjectWidget(projectWidgetDto, projectId);
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
    @ApiOperation(value = "Update the project widget positions for a project", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{projectId}/projectWidgetPositions", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> updateProjectWidgetsPositionForProject(@ApiParam(name = "projectId", value = "The project id", required = true)
                                                                             @PathVariable("projectId") Long projectId,
                                                                             @ApiParam(name = "projectWidgetPositionDtos", value = "The list of the new positions", required = true)
                                                                             @RequestBody List<ProjectWidgetPositionDto> projectWidgetPositionDtos) {
        Optional<Project> projectOptional = projectService.getOneById(projectId);

        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectId);
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
    @ApiOperation(value = "Delete a project widget for a project", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project widget not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "{projectId}/projectWidgets/{projectWidgetId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> deleteProjectWidgetFromProject(@ApiParam(name = "projectId", value = "The project id", required = true)
                                                                     @PathVariable("projectId") Long projectId,
                                                                     @ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                                     @PathVariable("projectWidgetId") Long projectWidgetId) {
        Optional<Project> projectOptional = projectService.getOneById(projectId);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectId);
        }

        if (!this.projectWidgetService.isProjectWidgetExists(projectWidgetId)) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
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
    @ApiOperation(value = "Edit a project widget for a project", response = ProjectDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project widget not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "{projectId}/projectWidgets/{projectWidgetId}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectDto> editProjectWidgetFromProject(@ApiParam(name = "projectId", value = "The project id", required = true)
                                                                   @PathVariable("projectId") Long projectId,
                                                                   @ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                                   @PathVariable("projectWidgetId") Long projectWidgetId,
                                                                   @ApiParam(name = "projectWidgetDto", value = "The project widget informations to update", required = true)
                                                                   @RequestBody ProjectWidgetDto projectWidgetDto) {

        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.findByProjectIdAndProjectWidgetId(projectId, projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        Optional<Project> projectOptional = projectService.getOneById(projectId);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectId);
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
