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
import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridRequestDto;
import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridResponseDto;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectGrid;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.api.ProjectGridService;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.mapper.ProjectGridMapper;
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
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

import static io.suricate.monitoring.utils.exceptions.constants.ErrorMessage.USER_NOT_ALLOWED_PROJECT;

/**
 * Project Grid controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Project Grid Controller", tags = {"Project Grids"})
public class ProjectGridController {
    /**
     * Project service
     */
    @Autowired
    private ProjectService projectService;

    /**
     * Project grid service
     */
    @Autowired
    private ProjectGridService projectGridService;

    /**
     * The mapper that transform domain/dto objects
     */
    @Autowired
    private ProjectGridMapper projectGridMapper;

    /**
     * Add a new grid to a project
     * @param connectedUser  The authentication entity
     * @param gridRequestDto The grid to add
     * @return The saved grid
     */
    @ApiOperation(value = "Create a new grid", response = ProjectResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ProjectResponseDto.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Current user not found", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/projectGrids/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectGridResponseDto> create(@ApiIgnore @AuthenticationPrincipal LocalUser connectedUser,
                                                         @ApiParam(name = "projectToken", value = "The project token", required = true)
                                                         @PathVariable("projectToken") String projectToken,
                                                         @ApiParam(name = "projectGridRequestDto", value = "The project grid information", required = true)
                                                         @RequestBody ProjectGridRequestDto.GridRequestDto gridRequestDto) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!this.projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        ProjectGrid projectGrid = projectGridService.create(projectGridMapper.toProjectGridEntity(gridRequestDto, project));

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(projectGridMapper.toProjectGridDTO(projectGrid));
    }

    /**
     * Update an existing project
     * @param connectedUser      The connected user
     * @param projectToken       The project token to update
     * @param projectRequestDto  The project grids information to update
     * @return The project updated
     */
    @ApiOperation(value = "Update an existing project by the project token")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Project updated"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Grid not found for the project", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/projectGrids/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateProjectGrids(@ApiIgnore @AuthenticationPrincipal LocalUser connectedUser,
                                                   @ApiParam(name = "projectToken", value = "The project token", required = true)
                                                   @PathVariable("projectToken") String projectToken,
                                                   @ApiParam(name = "projectResponseDto", value = "The project information", required = true)
                                                   @RequestBody ProjectGridRequestDto projectRequestDto) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!this.projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        // Check given grids belonging to given project
        projectRequestDto.getGrids().forEach(givenGrid -> {
            if (project.getGrids().stream().noneMatch(grid -> grid.getId().equals(givenGrid.getId()))) {
                throw new GridNotFoundException(givenGrid.getId(), projectToken);
            }
        });

        projectGridService.updateAll(project, projectRequestDto);

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a given grid of a project
     * @param connectedUser  The connected user
     * @param projectToken   The project token to delete
     * @param gridId         The grid id
     * @return A void response entity
     */
    @ApiOperation(value = "Delete a grid by the grid id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Project deleted"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Grid not found for the project", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/projectGrids/{projectToken}/{gridId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteGridById(@ApiIgnore @AuthenticationPrincipal LocalUser connectedUser,
                                               @ApiParam(name = "projectToken", value = "The project token", required = true)
                                               @PathVariable("projectToken") String projectToken,
                                               @ApiParam(name = "gridId", value = "The grid id", required = true, example = "1")
                                               @PathVariable("gridId") Long gridId) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        if (project.getGrids().stream().noneMatch(grid -> grid.getId().equals(gridId))) {
            throw new GridNotFoundException(gridId, projectToken);
        }

        projectGridService.deleteByProjectIdAndId(project, gridId);

        return ResponseEntity.noContent().build();
    }
}
