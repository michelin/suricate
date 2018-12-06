package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.dto.api.project.ProjectWidgetDto;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * The widget controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Project Widget Controller", tags = {"Project Widget"})
public class ProjectWidgetController {

    /**
     * The project widget service
     */
    private final ProjectWidgetService projectWidgetService;

    /**
     * Constructor
     *
     * @param projectWidgetService The project widget serbice
     */
    public ProjectWidgetController(final ProjectWidgetService projectWidgetService) {
        this.projectWidgetService = projectWidgetService;
    }

    /**
     * Edit a project widget for a project
     *
     * @param projectWidgetId  The project widget id
     * @param projectWidgetDto The project widget updated
     * @return The project updated
     */
    @ApiOperation(value = "Edit a project widget", response = ProjectResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ProjectResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project widget not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/projectWidgets/{projectWidgetId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectResponseDto> editProjectWidgetFromProject(@ApiParam(name = "projectWidgetId", value = "The project widget id", required = true)
                                                                           @PathVariable("projectWidgetId") Long projectWidgetId,
                                                                           @ApiParam(name = "projectWidgetDto", value = "The project widget informations to update", required = true)
                                                                           @RequestBody ProjectWidgetDto projectWidgetDto) {
        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.getOne(projectWidgetId);
        if (!projectWidgetOptional.isPresent()) {
            throw new ObjectNotFoundException(ProjectWidget.class, projectWidgetId);
        }

        ProjectWidget projectWidget = projectWidgetOptional.get();
        projectWidgetService.updateProjectWidget(projectWidget, projectWidgetDto.getCustomStyle(), projectWidgetDto.getBackendConfig());

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
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
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
