/*
 *
 *  * Copyright 2012-2018 the original author or authors.
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
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.PermitAll;
import java.util.Optional;

/**
 * The widget controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Project Widget Controller", tags = {"Project Widgets"})
public class ProjectWidgetController {
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
    public ResponseEntity<ProjectWidgetResponseDto> getProjectWidgetFromProject(@ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                                                @PathVariable("projectWidgetId") Long projectWidgetId) {
        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.getOne(projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectWidgetMapper.toProjectWidgetDTO(projectWidgetOptional.get()));
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
    public ResponseEntity<Void> editProjectWidgetFromProject(@ApiIgnore OAuth2Authentication authentication,
                                                             @ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                             @PathVariable("projectWidgetId") Long projectWidgetId,
                                                             @ApiParam(name = "projectWidgetResponseDto", value = "The project widget informations to update", required = true)
                                                             @RequestBody ProjectWidgetRequestDto projectWidgetRequestDto) {
        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.getOne(projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        if (!projectService.isConnectedUserCanAccessToProject(projectWidgetOptional.get().getProject(), authentication.getUserAuthentication())) {
            throw new ApiException("The user is not allowed to modify this resource", ApiErrorEnum.NOT_AUTHORIZED);
        }

        projectWidgetService.updateProjectWidget(projectWidgetOptional.get(), projectWidgetRequestDto.getCustomStyle(),
                projectWidgetRequestDto.getBackendConfig());

        return ResponseEntity.noContent().build();
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
    public ResponseEntity<Void> deleteProjectWidget(@ApiIgnore OAuth2Authentication authentication,
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
