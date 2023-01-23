/*
 *
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.controllers;

import io.suricate.monitoring.configuration.swagger.ApiPageable;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.project.ProjectRequestDto;
import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetPositionRequestDto;
import io.suricate.monitoring.model.dto.api.user.UserResponseDto;
import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.api.ProjectGridService;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.api.ProjectWidgetService;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.services.mapper.ProjectGridMapper;
import io.suricate.monitoring.services.mapper.ProjectMapper;
import io.suricate.monitoring.services.mapper.UserMapper;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.exceptions.ApiException;
import io.suricate.monitoring.utils.exceptions.InvalidFileException;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.security.PermitAll;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.suricate.monitoring.utils.exceptions.constants.ErrorMessage.USER_NOT_ALLOWED_PROJECT;

@RestController
@RequestMapping("/api")
@Tag(name = "Project", description = "Project Controller")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectGridService projectGridService;

    @Lazy
    @Autowired
    private ProjectWidgetService projectWidgetService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectGridMapper projectGridMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DashboardWebSocketService dashboardWebSocketService;

    /**
     * Get every project in database
     * @return The whole list of projects
     */
    @Operation(summary = "Get the full list of projects")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @ApiPageable
    @GetMapping("/v1/projects")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<ProjectResponseDto> getAll(@Parameter(name = "search", description = "Search keyword")
                                           @RequestParam(value = "search", required = false) String search,
                                           @ParameterObject Pageable pageable) {
        return projectService.getAll(search, pageable).map(projectMapper::toProjectDTONoAsset);
    }

    /**
     * Add a new project/dashboard for a user
     * @param connectedUser     The connected user
     * @param projectRequestDto The project to add
     * @return The saved project
     */
    @Operation(summary = "Create a new project for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Current user not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PostMapping(value = "/v1/projects")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectResponseDto> createProject(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                                            @Parameter(name = "projectRequestDto", description = "The project information", required = true)
                                                            @RequestBody ProjectRequestDto projectRequestDto) {
        Optional<User> userOptional = userService.getOneByUsername(connectedUser.getUsername());
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, connectedUser.getUsername());
        }

        Project project = projectService.createProjectForUser(userOptional.get(),
                projectMapper.toProjectEntity(projectRequestDto));

        project.getGrids().add(projectGridService.create(projectGridMapper.toProjectGridEntity(project)));

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/projects/" + project.getToken())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectMapper.toProjectDTO(project));
    }

    /**
     * Get a project by id
     * @param projectToken The id of the project
     * @return The project
     */
    @Operation(summary = "Retrieve the project information by token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/projects/{projectToken}")
    @PermitAll
    public ResponseEntity<ProjectResponseDto> getOneByToken(@Parameter(name = "projectToken", description = "The project token", required = true)
                                                            @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);

        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectMapper.toProjectDTO(projectOptional.get()));
    }

    /**
     * Update an existing project
     * @param connectedUser     The connected user
     * @param projectToken      The project token to update
     * @param projectRequestDto The information to update
     * @return The project updated
     */
    @Operation(summary = "Update an existing project by the project token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Project updated"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PutMapping(value = "/v1/projects/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateProject(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                              @Parameter(name = "projectToken", description = "The project token", required = true)
                                              @PathVariable("projectToken") String projectToken,
                                              @Parameter(name = "projectResponseDto", description = "The project information", required = true)
                                              @RequestBody ProjectRequestDto projectRequestDto) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        projectService.updateProject(project, projectRequestDto.getName(), projectRequestDto.getWidgetHeight(),
            projectRequestDto.getMaxColumn(), projectRequestDto.getCssStyle());

        return ResponseEntity.noContent().build();
    }

    /**
     * Add/Update a project screenshot
     * @param projectToken The project token to update
     */
    @Operation(summary = "Add/Update a project screenshot")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Screenshot updated"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PutMapping(value = "/v1/projects/{projectToken}/screenshot")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateProjectScreenshot(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                                        @Parameter(name = "projectToken", description = "The project token", required = true)
                                                        @PathVariable("projectToken") String projectToken,
                                                        @Parameter(name = "screenshot", description = "The screenshot to insert", required = true)
                                                        @RequestParam MultipartFile screenshot) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        try {
            projectService.addOrUpdateScreenshot(project, screenshot.getBytes(),
                screenshot.getContentType(), screenshot.getSize());
        } catch (IOException e) {
            throw new InvalidFileException(screenshot.getOriginalFilename(), Project.class, project.getId());
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a project
     * @param connectedUser  The connected user
     * @param projectToken   The project token to delete
     * @return A void response entity
     */
    @Operation(summary = "Delete a project by the project token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Project deleted"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @DeleteMapping(value = "/v1/projects/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteProjectById(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                                  @Parameter(name = "projectToken", description = "The project token", required = true)
                                                  @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);

        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        if (!projectService.isConnectedUserCanAccessToProject(projectOptional.get(), connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        projectService.deleteProject(projectOptional.get());

        return ResponseEntity.noContent().build();
    }

    /**
     * Update the list of widget positions for a project
     * @param connectedUser                    The connected user
     * @param projectToken                     The project token to update
     * @param projectWidgetPositionRequestDtos The list of project widget positions
     * @return The project updated
     */
    @Operation(summary = "Update the project widget positions for a project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Widget position updated"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PutMapping(value = "/v1/projects/{projectToken}/projectWidgetPositions")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateProjectWidgetsPositionForProject(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                                                       @Parameter(name = "projectToken", description = "The project token", required = true)
                                                                       @PathVariable("projectToken") String projectToken,
                                                                       @Parameter(name = "projectWidgetPositionRequestDtos", description = "The list of the new positions", required = true)
                                                                       @RequestBody List<ProjectWidgetPositionRequestDto> projectWidgetPositionRequestDtos) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        projectWidgetService.updateWidgetPositionByProject(projectOptional.get(), projectWidgetPositionRequestDtos);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get the list of users associated to a project
     * @param projectToken Token of the project
     */
    @Operation(summary = "Retrieve project users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/projects/{projectToken}/users")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserResponseDto>> getProjectUsers(@Parameter(name = "projectToken", description = "The project token", required = true)
                                                                 @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUsersDTOs(projectOptional.get().getUsers()));
    }

    /**
     * Add a user to a project
     * @param connectedUser  The connected user
     * @param projectToken   Token of the project
     * @param usernameMap    Username of the user to add
     * @return The project
     */
    @Operation(summary = "Add a user to a project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "User not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PostMapping(value = "/v1/projects/{projectToken}/users")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> addUserToProject(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                                 @Parameter(name = "projectToken", description = "The project token", required = true)
                                                 @PathVariable("projectToken") String projectToken,
                                                 @Parameter(name = "usernameMap", description = "A map with the username", required = true)
                                                 @RequestBody Map<String, String> usernameMap) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);

        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        Optional<User> userOptional = userService.getOneByUsername(usernameMap.get("username"));
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, usernameMap.get("username"));
        }

        projectService.createProjectForUser(userOptional.get(), projectOptional.get());
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a user from a dashboard
     * @param connectedUser  The connected user
     * @param projectToken   The project/dashboard token
     * @param userId         The user id to delete
     * @return The project
     */
    @Operation(summary = "Delete a user from a project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User removed from project"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "User not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @DeleteMapping(value = "/v1/projects/{projectToken}/users/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteUserFromProject(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                                      @Parameter(name = "projectToken", description = "The project token", required = true)
                                                      @PathVariable("projectToken") String projectToken,
                                                      @Parameter(name = "userId", description = "The user id", required = true, example = "1")
                                                      @PathVariable("userId") Long userId) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        Optional<User> userOptional = userService.getOne(userId);
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        projectService.deleteUserFromProject(userOptional.get(), projectOptional.get());
        return ResponseEntity.noContent().build();
    }

    /**
     * Get the list of websocket clients connected to the project
     * @param projectToken Token of the project
     */
    @Operation(summary = "Retrieve connected websocket clients for a project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/projects/{projectToken}/websocket/clients")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<WebsocketClient>> getProjectWebsocketClients(@Parameter(name = "projectToken", description = "The project token", required = true)
                                                                            @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(dashboardWebSocketService.getWebsocketClientsByProjectToken(projectToken));
    }

    /**
     * Get projects for a user
     * @param connectedUser The connected user
     * @return The whole list of projects
     */
    @Operation(summary = "Get the list of projects related to the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Current user not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/projects/currentUser")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<ProjectResponseDto>> getAllForCurrentUser(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser) {
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectMapper.toProjectsDTOs(projectService.getAllByUser(connectedUser.getUser())));
    }
}
