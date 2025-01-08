/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.controller;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.dto.api.export.ImportExportDto;
import com.michelin.suricate.model.dto.api.export.ImportExportProjectDto;
import com.michelin.suricate.model.dto.api.export.ImportExportRepositoryDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.Repository;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.api.ProjectService;
import com.michelin.suricate.service.api.RepositoryService;
import com.michelin.suricate.service.git.GitService;
import com.michelin.suricate.service.mapper.ProjectMapper;
import com.michelin.suricate.service.mapper.RepositoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Import/Export controller.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Import/Export", description = "Import/Export Controller")
public class ImportExportController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private GitService gitService;

    @Autowired
    private RepositoryMapper repositoryMapper;

    @Autowired
    private ProjectMapper projectMapper;

    /**
     * Export the application data.
     *
     * @return The application data to export
     */
    @Operation(summary = "Export the application data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/export")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ImportExportDto> exports() {
        List<ImportExportRepositoryDto> importExportRepositories = repositoryService
            .getAll(StringUtils.EMPTY, Pageable.unpaged())
            .map(repositoryMapper::toImportExportRepositoryDto)
            .getContent();

        List<ImportExportProjectDto> projects = projectService
            .getAll(StringUtils.EMPTY, Pageable.unpaged())
            .map(projectMapper::toImportExportProjectDto)
            .getContent();

        ImportExportDto exportDto = new ImportExportDto();
        exportDto.setRepositories(importExportRepositories);
        exportDto.setProjects(projects);

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(exportDto);
    }

    /**
     * Import the application data.
     *
     * @param importDto The application data to import
     */
    @Operation(summary = "Import the application data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PostMapping(value = "/v1/import")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> imports(@Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
                                        @Parameter(name = "importDto", description = "The data to import",
                                            required = true) @RequestBody ImportExportDto importDto)
        throws GitAPIException, IOException {
        // Map repositories into entity
        List<Repository> repositories = importDto.getRepositories()
            .stream()
            .map(repositoryMapper::toRepositoryEntity)
            .toList();

        // For existing repos, restore the ID
        repositories.forEach(repository -> {
            Optional<Repository> repositoryOptional = repositoryService.findByName(repository.getName());
            repositoryOptional.ifPresent(value -> repository.setId(value.getId()));
        });

        repositoryService.addOrUpdateRepositories(repositories);
        gitService.updateWidgetFromEnabledGitRepositories();

        List<Project> projects = importDto.getProjects()
            .stream()
            .map(projectMapper::toProjectEntity)
            .toList();

        projectService.createUpdateProjects(projects, connectedUser.getUser());

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .build();
    }
}
