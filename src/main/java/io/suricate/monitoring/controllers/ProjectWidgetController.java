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

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetRequestDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectGrid;
import io.suricate.monitoring.model.entities.ProjectWidget;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.api.ProjectWidgetService;
import io.suricate.monitoring.services.mapper.ProjectWidgetMapper;
import io.suricate.monitoring.utils.exceptions.ApiException;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.PermitAll;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.suricate.monitoring.utils.exceptions.constants.ErrorMessage.USER_NOT_ALLOWED_PROJECT;

/**
 * The widget controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Project Widget Controller", tags = {"Project Widgets"})
public class  ProjectWidgetController {
    /**
     * The project widget service
     */
    private final ProjectWidgetService projectWidgetService;

    /**
     * The model/DTO for project widget
     */
    private final ProjectWidgetMapper projectWidgetMapper;

    /**
     * The project service
     */
    private final ProjectService projectService;

    /**
     * Constructor
     *
     * @param projectWidgetService The project widget service
     * @param projectWidgetMapper  The mapper to inject
     * @param projectService       The project service to inject
     */
    public ProjectWidgetController(final ProjectWidgetService projectWidgetService,
                                   final ProjectWidgetMapper projectWidgetMapper,
                                   final ProjectService projectService) {
        this.projectWidgetService = projectWidgetService;
        this.projectWidgetMapper = projectWidgetMapper;
        this.projectService = projectService;
    }

    /**
     * Get a project widget
     *
     * @param projectWidgetId The project widget id
     * @return The project updated
     */
    @ApiOperation(value = "Retrieve a project widget", response = ProjectWidgetResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectWidgetResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project widget not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/projectWidgets/{projectWidgetId}")
    @PermitAll
    public ResponseEntity<ProjectWidgetResponseDto> getById(@ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                            @PathVariable("projectWidgetId") Long projectWidgetId) {
        Optional<ProjectWidget> projectWidgetOptional = this.projectWidgetService.getOne(projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(this.projectWidgetMapper.toProjectWidgetDTO(projectWidgetOptional.get()));
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
    @GetMapping(value = "/v1/projectWidgets/{projectToken}/projectWidgets")
    @PermitAll
    public ResponseEntity<List<ProjectWidgetResponseDto>> getByProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                                                      @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = this.projectService.getOneByToken(projectToken);
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
                .body(this.projectWidgetMapper.toProjectWidgetsDTOs(project.getWidgets()));
    }

    /**
     * Get the list of project widgets for a specific project grid
     */
    @ApiOperation(value = "Get the full list of project widgets for a specific project grid", response = ProjectWidgetResponseDto.class, nickname = "getProjectWidgetsForProjectAndGrid")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ProjectWidgetResponseDto.class, responseContainer = "List"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/projectWidgets/{projectToken}/{gridId}/projectWidgets")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<ProjectWidgetResponseDto>> getByProjectAndGrid(@ApiIgnore OAuth2Authentication authentication,
                                                                                             @ApiParam(name = "projectToken", value = "The project token", required = true)
                                                                                             @PathVariable("projectToken") String projectToken,
                                                                                             @ApiParam(name = "gridId", value = "The grid id", required = true)
                                                                                             @PathVariable("gridId") Long gridId) {
        Optional<Project> projectOptional = this.projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (project.getGrids().stream().noneMatch(grid -> grid.getId().equals(gridId))) {
            throw new ObjectNotFoundException(ProjectGrid.class, gridId);
        }

        if (!this.projectService.isConnectedUserCanAccessToProject(project, authentication)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.projectWidgetMapper.toProjectWidgetsDTOs(project.getWidgets()
                        .stream()
                        .filter(widget -> widget.getProjectGrid().getId().equals(gridId))
                        .collect(Collectors.toList())));
    }

    /**
     * Edit a project widget for a project
     *
     * @param authentication          The connected user
     * @param projectWidgetId         The project widget id
     * @param projectWidgetRequestDto The project widget updated
     */
    @ApiOperation(value = "Edit a project widget")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Project widget updated"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project widget not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/projectWidgets/{projectWidgetId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectWidgetResponseDto> editByProject(@ApiIgnore OAuth2Authentication authentication,
                                                             @ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                             @PathVariable("projectWidgetId") Long projectWidgetId,
                                                             @ApiParam(name = "projectWidgetResponseDto", value = "The project widget informations to update", required = true)
                                                             @RequestBody ProjectWidgetRequestDto projectWidgetRequestDto) {
        Optional<ProjectWidget> projectWidgetOptional = this.projectWidgetService.getOne(projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        if (!this.projectService.isConnectedUserCanAccessToProject(projectWidgetOptional.get().getProject(), authentication.getUserAuthentication())) {
            throw new ApiException("The user is not allowed to modify this resource", ApiErrorEnum.NOT_AUTHORIZED);
        }

        this.projectWidgetService.updateProjectWidget(projectWidgetOptional.get(), projectWidgetRequestDto.getCustomStyle(),
                projectWidgetRequestDto.getBackendConfig());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.projectWidgetMapper.toProjectWidgetDTO(projectWidgetOptional.get()));
    }

    /**
     * Add widget into the dashboard
     *
     * @param authentication          The connected user
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
    @PostMapping(value = "/v1/projectWidgets/{projectToken}/{gridId}/projectWidgets")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectWidgetResponseDto> addProjectWidgetToProject(@ApiIgnore OAuth2Authentication authentication,
                                                                              @ApiParam(name = "projectToken", value = "The project token", required = true)
                                                                              @PathVariable("projectToken") String projectToken,
                                                                              @ApiParam(name = "gridId", value = "The grid id", required = true)
                                                                              @PathVariable("gridId") Long gridId,
                                                                              @ApiParam(name = "projectWidgetDto", value = "The project widget info's", required = true)
                                                                              @RequestBody ProjectWidgetRequestDto projectWidgetRequestDto) {
        Optional<Project> projectOptional = this.projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (project.getGrids().stream().noneMatch(grid -> grid.getId().equals(gridId))) {
            throw new ObjectNotFoundException(ProjectGrid.class, gridId);
        }

        if (!this.projectService.isConnectedUserCanAccessToProject(project, authentication)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        ProjectWidget projectWidget = this.projectWidgetMapper.toProjectWidgetEntity(projectWidgetRequestDto, projectToken, gridId);
        this.projectWidgetService.addWidgetInstanceToProject(projectWidget);

        URI resourceLocation = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/projectWidgets/" + projectWidget.getId())
                .build()
                .toUri();

        return ResponseEntity
                .created(resourceLocation)
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.projectWidgetMapper.toProjectWidgetDTO(projectWidget));
    }

    /**
     * Delete a project widget from a dashboard
     *
     * @param projectWidgetId The project widget to delete
     * @return The dashboard updated
     */
    @ApiOperation(value = "Delete a project widget")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Project widget deleted"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project widget not found", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/projectWidgets/{projectWidgetId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteById(@ApiIgnore OAuth2Authentication authentication,
                                           @ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                           @PathVariable("projectWidgetId") Long projectWidgetId) {
        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.getOne(projectWidgetId);

        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        if (!projectService.isConnectedUserCanAccessToProject(projectWidgetOptional.get().getProject(), authentication.getUserAuthentication())) {
            throw new ApiException("The user is not allowed to modify this resource", ApiErrorEnum.NOT_AUTHORIZED);
        }

        projectWidgetService.removeWidgetFromDashboard(projectWidgetId);
        return ResponseEntity.noContent().build();
    }
}
