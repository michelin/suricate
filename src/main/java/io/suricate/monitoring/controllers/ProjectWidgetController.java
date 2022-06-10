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

import io.suricate.monitoring.security.LocalUser;
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
import io.suricate.monitoring.utils.exceptions.GridNotFoundException;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.PermitAll;
import java.net.URI;
import java.util.Collection;
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
    @Autowired
    private ProjectWidgetService projectWidgetService;

    /**
     * The model/DTO for project widget
     */
    @Autowired
    private ProjectWidgetMapper projectWidgetMapper;

    /**
     * The project service
     */
    @Autowired
    private ProjectService projectService;

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
        List<ProjectWidget> allWidgets = project.getGrids().stream().map(ProjectGrid::getWidgets).flatMap(Collection::stream).collect(Collectors.toList());
        if (project.getGrids().isEmpty() || allWidgets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.projectWidgetMapper.toProjectWidgetsDTOs(allWidgets));
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
    public ResponseEntity<ProjectWidgetResponseDto> editByProject(@ApiIgnore @AuthenticationPrincipal LocalUser connectedUser,
                                                                  @ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                                  @PathVariable("projectWidgetId") Long projectWidgetId,
                                                                  @ApiParam(name = "projectWidgetResponseDto", value = "The project widget informations to update", required = true)
                                                                  @RequestBody ProjectWidgetRequestDto projectWidgetRequestDto) {
        Optional<ProjectWidget> projectWidgetOptional = this.projectWidgetService.getOne(projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        if (!this.projectService.isConnectedUserCanAccessToProject(projectWidgetOptional.get().getProjectGrid().getProject(), connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
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
    public ResponseEntity<ProjectWidgetResponseDto> addProjectWidgetToProject(@ApiIgnore @AuthenticationPrincipal LocalUser connectedUser,
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
        if (!this.projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        if (project.getGrids().stream().noneMatch(grid -> grid.getId().equals(gridId))) {
            throw new GridNotFoundException(gridId, projectToken);
        }

        ProjectWidget projectWidget = this.projectWidgetMapper.toProjectWidgetEntity(projectWidgetRequestDto, gridId);
        this.projectWidgetService.createAndRefreshDashboards(projectWidget);

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
    public ResponseEntity<Void> deleteById(@ApiIgnore @AuthenticationPrincipal LocalUser connectedUser,
                                           @ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                           @PathVariable("projectWidgetId") Long projectWidgetId) {
        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.getOne(projectWidgetId);

        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        if (!projectService.isConnectedUserCanAccessToProject(projectWidgetOptional.get().getProjectGrid().getProject(), connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        projectWidgetService.removeWidgetFromDashboard(projectWidgetId);
        return ResponseEntity.noContent().build();
    }
}
