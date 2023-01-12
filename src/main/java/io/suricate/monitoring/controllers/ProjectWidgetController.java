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
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.api.ProjectWidgetService;
import io.suricate.monitoring.services.mapper.ProjectWidgetMapper;
import io.suricate.monitoring.utils.exceptions.ApiException;
import io.suricate.monitoring.utils.exceptions.GridNotFoundException;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.security.PermitAll;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.suricate.monitoring.utils.exceptions.constants.ErrorMessage.USER_NOT_ALLOWED_PROJECT;

@RestController
@RequestMapping("/api")
@Tag(name = "Project Widget", description = "Project Widget Controller")
public class ProjectWidgetController {
    @Autowired
    private ProjectWidgetService projectWidgetService;

    @Autowired
    private ProjectWidgetMapper projectWidgetMapper;

    @Autowired
    private ProjectService projectService;

    /**
     * Get a project widget
     * @param projectWidgetId The project widget id
     * @return The project updated
     */
    @Operation(summary = "Retrieve a project widget")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project widget not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/projectWidgets/{projectWidgetId}")
    @PermitAll
    public ResponseEntity<ProjectWidgetResponseDto> getById(@Parameter(name = "projectWidgetId", description = "The project widget id", required = true, example = "1")
                                                            @PathVariable("projectWidgetId") Long projectWidgetId) {
        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.getOne(projectWidgetId);
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
    @Operation(summary = "Get the full list of projectWidgets for a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/projectWidgets/{projectToken}/projectWidgets")
    @PermitAll
    public ResponseEntity<List<ProjectWidgetResponseDto>> getByProject(@Parameter(name = "projectToken", description = "The project token", required = true)
                                                                       @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
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
                .body(projectWidgetMapper.toProjectWidgetsDTOs(allWidgets));
    }

    /**
     * Edit a project widget for a project
     * @param connectedUser          The connected user
     * @param projectWidgetId         The project widget id
     * @param projectWidgetRequestDto The project widget updated
     */
    @Operation(summary = "Edit a project widget")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Project widget updated"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project widget not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PutMapping(value = "/v1/projectWidgets/{projectWidgetId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectWidgetResponseDto> editByProject(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                                                  @Parameter(name = "projectWidgetId", description = "The project widget id", required = true, example = "1")
                                                                  @PathVariable("projectWidgetId") Long projectWidgetId,
                                                                  @Parameter(name = "projectWidgetResponseDto", description = "The project widget information to update", required = true)
                                                                  @RequestBody ProjectWidgetRequestDto projectWidgetRequestDto) {
        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.getOne(projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        if (!this.projectService.isConnectedUserCanAccessToProject(projectWidgetOptional.get().getProjectGrid().getProject(), connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        projectWidgetService.updateProjectWidget(projectWidgetOptional.get(), projectWidgetRequestDto.getCustomStyle(),
                projectWidgetRequestDto.getBackendConfig());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(projectWidgetMapper.toProjectWidgetDTO(projectWidgetOptional.get()));
    }

    /**
     * Add widget into the dashboard
     * @param connectedUser          The connected user
     * @param projectToken            The project token
     * @param projectWidgetRequestDto The projectWidget to add
     * @return The project
     */
    @Operation(summary = "Add a new widget to a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PostMapping(value = "/v1/projectWidgets/{projectToken}/{gridId}/projectWidgets")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectWidgetResponseDto> addProjectWidgetToProject(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                                                              @Parameter(name = "projectToken", description = "The project token", required = true)
                                                                              @PathVariable("projectToken") String projectToken,
                                                                              @Parameter(name = "gridId", description = "The grid id", required = true, example = "1")
                                                                              @PathVariable("gridId") Long gridId,
                                                                              @Parameter(name = "projectWidgetDto", description = "The project widget info's", required = true)
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
     * @param projectWidgetId The project widget to delete
     * @return The dashboard updated
     */
    @Operation(summary = "Delete a project widget")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Project widget deleted"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project widget not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @DeleteMapping(value = "/v1/projectWidgets/{projectWidgetId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteById(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                           @Parameter(name = "projectWidgetId", description = "The project widget id", required = true, example = "1")
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
