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

package com.michelin.suricate.controllers;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.services.mapper.ProjectGridMapper;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridRequestDto;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridResponseDto;
import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectGrid;
import com.michelin.suricate.model.enums.ApiErrorEnum;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.ProjectGridService;
import com.michelin.suricate.services.api.ProjectService;
import com.michelin.suricate.utils.exceptions.ApiException;
import com.michelin.suricate.utils.exceptions.GridNotFoundException;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
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

import java.util.Optional;

import static com.michelin.suricate.utils.exceptions.constants.ErrorMessage.USER_NOT_ALLOWED_PROJECT;

/**
 * Project Grid controller
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Project Grid", description = "Project Grid Controller")
public class ProjectGridController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectGridService projectGridService;

    @Autowired
    private ProjectGridMapper projectGridMapper;

    /**
     * Add a new grid to a project
     * @param connectedUser  The authentication entity
     * @param gridRequestDto The grid to add
     * @return The saved grid
     */
    @Operation(summary = "Create a new grid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Current user not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PostMapping(value = "/v1/projectGrids/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProjectGridResponseDto> create(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                                         @Parameter(name = "projectToken", description = "The project token", required = true)
                                                         @PathVariable("projectToken") String projectToken,
                                                         @Parameter(name = "projectGridRequestDto", description = "The project grid information", required = true)
                                                         @RequestBody ProjectGridRequestDto.GridRequestDto gridRequestDto) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
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
    @Operation(summary = "Update an existing project by the project token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project updated"),
            @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Grid not found for the project", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PutMapping(value = "/v1/projectGrids/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateProjectGrids(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                                   @Parameter(name = "projectToken", description = "The project token", required = true)
                                                   @PathVariable("projectToken") String projectToken,
                                                   @Parameter(name = "projectResponseDto", description = "The project information", required = true)
                                                   @RequestBody ProjectGridRequestDto projectRequestDto) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!projectService.isConnectedUserCanAccessToProject(project, connectedUser)) {
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
    @Operation(summary = "Delete a grid by the grid id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Grid not found for the project", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @DeleteMapping(value = "/v1/projectGrids/{projectToken}/{gridId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteGridById(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                               @Parameter(name = "projectToken", description = "The project token", required = true)
                                               @PathVariable("projectToken") String projectToken,
                                               @Parameter(name = "gridId", description = "The grid id", required = true, example = "1")
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
