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

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.project.ProjectRequestDto;
import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetPositionRequestDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetRequestDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import io.suricate.monitoring.model.dto.api.user.UserResponseDto;
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.widget.WidgetParam;
import io.suricate.monitoring.service.api.ConfigurationService;
import io.suricate.monitoring.service.api.ProjectService;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.service.api.UserService;
import io.suricate.monitoring.service.mapper.ProjectMapper;
import io.suricate.monitoring.service.mapper.ProjectWidgetMapper;
import io.suricate.monitoring.service.mapper.UserMapper;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import java.util.stream.Collectors;

/**
 * Project controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Project Controller", tags = {"Projects"})
public class ProjectController {

    /**
     * Configuration service
     */
    private final ConfigurationService configurationService;

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
     * The user mapper
     */
    private final UserMapper userMapper;

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
                             final ProjectWidgetMapper projectWidgetMapper,
                             final ConfigurationService configurationService,
                             final UserMapper userMapper) {
        this.projectService = projectService;
        this.projectWidgetService = projectWidgetService;
        this.userService = userService;
        this.projectMapper = projectMapper;
        this.projectWidgetMapper = projectWidgetMapper;
        this.configurationService = configurationService;
        this.userMapper = userMapper;
    }

    /**
     * Get every project in database
     *
     * @return The whole list of projects
     */
    @ApiOperation(value = "Get the full list of projects", response = ProjectResponseDto.class, nickname = "getAllProjects")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping("/v1/projects")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<List<ProjectResponseDto>> getAll() {

        return Optional
            .ofNullable(projectService.getAll())
            .map(projects ->
                ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(projectMapper.toProjectDtosDefault(projects))
            )
            .orElseGet(() -> {
                throw new NoContentException(Project.class);
            });
    }

    /**
     * Add a new project/dashboard for a user
     *
     * @param principal         The connected user
     * @param projectRequestDto The project to add
     * @return The saved project
     */
    @ApiOperation(value = "Create a new project for the current user", response = ProjectResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Current user not found", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/projects")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectResponseDto> createProject(@ApiIgnore Principal principal,
                                                            @ApiParam(name = "projectRequestDto", value = "The project information", required = true)
                                                            @RequestBody ProjectRequestDto projectRequestDto) {
        Optional<User> userOptional = userService.getOneByUsername(principal.getName());
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, principal.getName());
        }

        Project project = projectService.createProject(userOptional.get(), projectMapper.toNewProject(projectRequestDto));

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/projects/" + project.getToken())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectMapper.toProjectDtoDefault(project));
    }

    /**
     * Get a project by id
     *
     * @param projectToken The id of the project
     * @return The project
     */
    @ApiOperation(value = "Retrieve the project information by token", response = ProjectResponseDto.class, nickname = "getProjectById")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/projects/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public ResponseEntity<ProjectResponseDto> getOneByToken(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                            @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

//        // Add configuration for widgets
//        projectOptional.get().getWidgets().forEach(this::addConfiguration);

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectMapper.toProjectDtoDefault(projectOptional.get()));
    }

    private void addConfiguration(ProjectWidget widget) {
        Category widgetCat = widget.getWidget().getCategory();
        List<Configuration> confs = configurationService.getConfigurationForCategory(widgetCat.getId());

        List<WidgetParam> params = confs.stream().map(ConfigurationService::initParamFromConfiguration).collect(Collectors.toList());

        widget.getWidget().getWidgetParams().addAll(params);
    }

    /**
     * Update an existing project
     *
     * @param projectToken      The project token to update
     * @param projectRequestDto The informations to update
     * @return The project updated
     */
    @ApiOperation(value = "Update an existing project by the project token")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/projects/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                              @PathVariable("projectToken") String projectToken,
                                              @ApiParam(name = "projectResponseDto", value = "The project information", required = true)
                                              @RequestBody ProjectRequestDto projectRequestDto) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        projectService.updateProject(
            projectOptional.get(),
            projectRequestDto.getName(),
            projectRequestDto.getWidgetHeight(),
            projectRequestDto.getMaxColumn(),
            projectRequestDto.getCssStyle()
        );

        return ResponseEntity.noContent().build();
    }

    /**
     * Method that delete a project
     *
     * @param projectToken The project token to delete
     * @return The project deleted
     */
    @ApiOperation(value = "Delete a project by the project token")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/projects/{projectToken}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Void> deleteOneById(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                              @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        projectService.deleteProject(projectOptional.get());
        return ResponseEntity.noContent().build();
    }

    /**
     * Update the list of widget positions for a project
     *
     * @param projectToken                     The project token to update
     * @param projectWidgetPositionRequestDtos The list of project widget positions
     * @return The project updated
     */
    @ApiOperation(value = "Update the project widget positions for a project")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/projects/{projectToken}/projectWidgetPositions")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateProjectWidgetsPositionForProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                                       @PathVariable("projectToken") String projectToken,
                                                                       @ApiParam(name = "projectWidgetPositionRequestDtos", value = "The list of the new positions", required = true)
                                                                       @RequestBody List<ProjectWidgetPositionRequestDto> projectWidgetPositionRequestDtos) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);

        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        projectWidgetService.updateWidgetPositionByProject(projectOptional.get(), projectWidgetPositionRequestDtos);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get the list of project widgets for a project
     */
    @ApiOperation(value = "Get the full list of projectWidgets for a project", response = ProjectWidgetResponseDto.class, nickname = "getProjectWidgetsForProject")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectWidgetResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/projects/{projectToken}/projectWidgets")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public ResponseEntity<List<ProjectWidgetResponseDto>> getProjectWidgetsForProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                                                      @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Project project = projectOptional.get();
        if (project.getWidgets().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectWidgetMapper.toProjectWidgetDtosDefault(project.getWidgets()));
    }

    /**
     * Add widget into the dashboard
     *
     * @param projectToken            The project token
     * @param projectWidgetRequestDto The projectWidget to add
     * @return The project
     */
    @ApiOperation(value = "Add a new widget to a project", response = ProjectWidgetResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectWidgetResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/projects/{projectToken}/projectWidgets")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public ResponseEntity<ProjectWidgetResponseDto> addProjectWidgetToProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                                              @PathVariable("projectToken") String projectToken,
                                                                              @ApiParam(name = "projectWidgetDto", value = "The project widget info's", required = true)
                                                                              @RequestBody ProjectWidgetRequestDto projectWidgetRequestDto) {
        if (!this.projectService.isProjectExists(projectToken)) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        ProjectWidget projectWidget = projectWidgetMapper.toNewProjectWidget(projectWidgetRequestDto, projectToken);
        projectWidgetService.addProjectWidget(projectWidget);

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/projectWidgets/" + projectWidget.getId())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectWidgetMapper.toProjectWidgetDtoDefault(projectWidget));
    }

    /**
     * Get the list of users associated to a project
     *
     * @param projectToken Token of the project
     */
    @ApiOperation(value = "Retrieve project users", response = UserResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/projects/{projectToken}/users")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public ResponseEntity<List<UserResponseDto>> getProjectUsers(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                                 @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUserDtosDefault(projectOptional.get().getUsers()));
    }

    /**
     * Add a user to a project
     *
     * @param projectToken Token of the project
     * @param usernameMap  Username of the user to add
     * @return The project
     */
    @ApiOperation(value = "Add a user to a project")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/projects/{projectToken}/users")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> addUserToProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                 @PathVariable("projectToken") String projectToken,
                                                 @ApiParam(name = "usernameMap", value = "A map with the username", required = true)
                                                 @RequestBody Map<String, String> usernameMap) {
        Optional<User> userOptional = userService.getOneByUsername(usernameMap.get("username"));
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, usernameMap.get("username"));
        }

        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        projectService.addUserToProject(userOptional.get(), projectOptional.get());
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a user from a dashboard
     *
     * @param projectToken The project/dashboard token
     * @param userId       The user id to delete
     * @return The project
     */
    @ApiOperation(value = "Delete a user from a project")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/projects/{projectToken}/users/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteUserToProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                    @PathVariable("projectToken") String projectToken,
                                                    @ApiParam(name = "userId", value = "The user id", required = true)
                                                    @PathVariable("userId") Long userId) {
        Optional<User> userOptional = userService.getOne(userId);
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        projectService.deleteUserFromProject(userOptional.get(), projectOptional.get());
        return ResponseEntity.noContent().build();
    }

    /**
     * Get projects for a user
     *
     * @param principal The connected user
     * @return The whole list of projects
     */
    @ApiOperation(value = "Get the list of projects related to the current user", response = ProjectResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Current user not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/projects/currentUser")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public ResponseEntity<List<ProjectResponseDto>> getAllForCurrentUser(@ApiIgnore Principal principal) {
        Optional<User> userOptional = userService.getOneByUsername(principal.getName());
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, principal.getName());
        }

        List<Project> projects = projectService.getAllByUser(userOptional.get());
        if (projects == null || projects.isEmpty()) {
            throw new NoContentException(Project.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectMapper.toProjectDtosDefault(projects));
    }
}
