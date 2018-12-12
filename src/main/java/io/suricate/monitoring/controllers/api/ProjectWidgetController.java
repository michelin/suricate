package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetRequestDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.mapper.project.ProjectWidgetMapper;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
     * Constructor
     *
     * @param projectWidgetService The project widget service
     * @param projectWidgetMapper  The mapper to inject
     */
    public ProjectWidgetController(final ProjectWidgetService projectWidgetService,
                                   final ProjectWidgetMapper projectWidgetMapper) {
        this.projectWidgetService = projectWidgetService;
        this.projectWidgetMapper = projectWidgetMapper;
    }

    /**
     * Get a project widget
     *
     * @param projectWidgetId The project widget id
     * @return The project updated
     */
    @ApiOperation(value = "Retrieve a project widget", response = ProjectResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project widget not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/projectWidgets/{projectWidgetId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectWidgetResponseDto> getProjectWidgetFromProject(@ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                                                @PathVariable("projectWidgetId") Long projectWidgetId) {
        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.getOne(projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(projectWidgetMapper.toProjectWidgetDtoDefault(projectWidgetOptional.get()));
    }

    /**
     * Edit a project widget for a project
     *
     * @param projectWidgetId         The project widget id
     * @param projectWidgetRequestDto The project widget updated
     * @return The project updated
     */
    @ApiOperation(value = "Edit a project widget", response = ProjectResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project widget not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/projectWidgets/{projectWidgetId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> editProjectWidgetFromProject(@ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                             @PathVariable("projectWidgetId") Long projectWidgetId,
                                                             @ApiParam(name = "projectWidgetResponseDto", value = "The project widget informations to update", required = true)
                                                             @RequestBody ProjectWidgetRequestDto projectWidgetRequestDto) {
        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.getOne(projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        ProjectWidget projectWidget = projectWidgetOptional.get();
        projectWidgetService.updateProjectWidget(projectWidget, projectWidgetRequestDto.getCustomStyle(), projectWidgetRequestDto.getBackendConfig());

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a project widget from a dashboard
     *
     * @param projectWidgetId The project widget to delete
     * @return The dashboard updated
     */
    @ApiOperation(value = "Delete a project widget", response = ProjectResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project widget not found", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/projectWidgets/{projectWidgetId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectResponseDto> deleteProjectWidget(@ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                                  @PathVariable("projectWidgetId") Long projectWidgetId) {
        if (!this.projectWidgetService.isProjectWidgetExists(projectWidgetId)) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        projectWidgetService.removeWidgetFromDashboard(projectWidgetId);
        return ResponseEntity.noContent().build();
    }
}
