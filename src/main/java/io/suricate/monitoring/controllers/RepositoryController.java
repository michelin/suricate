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
import io.suricate.monitoring.model.dto.api.repository.RepositoryRequestDto;
import io.suricate.monitoring.model.dto.api.repository.RepositoryResponseDto;
import io.suricate.monitoring.model.dto.api.widget.WidgetResponseDto;
import io.suricate.monitoring.model.entities.Repository;
import io.suricate.monitoring.services.api.RepositoryService;
import io.suricate.monitoring.services.git.GitService;
import io.suricate.monitoring.services.mapper.RepositoryMapper;
import io.suricate.monitoring.services.mapper.WidgetMapper;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api")
@Tag(name = "Repository", description = "Repository Controller")
public class RepositoryController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private GitService gitService;

    @Autowired
    private RepositoryMapper repositoryMapper;

    @Autowired
    private WidgetMapper widgetMapper;

    /**
     * Get the full list of repositories
     * @return The list of repositories
     */
    @Operation(summary = "Get the full list of repositories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @ApiPageable
    @GetMapping(value = "/v1/repositories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Page<RepositoryResponseDto> getAll(@Parameter(name = "search", description = "Search keyword")
                                              @RequestParam(value = "search", required = false) String search,
                                              @ParameterObject Pageable pageable) {
        return repositoryService
                .getAll(search, pageable)
                .map(repositoryMapper::toRepositoryDTONoWidgets);
    }

    /**
     * Create a new repository
     * @param repositoryRequestDto The repository to create
     * @return The repository created
     */
    @Operation(summary = "Create a new repository, and load it automatically if enable is selected")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PostMapping(value = "/v1/repositories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RepositoryResponseDto> createOne(@Parameter(name = "repositoryResponseDto", description = "The repository to create", required = true)
                                                           @RequestBody RepositoryRequestDto repositoryRequestDto) throws GitAPIException, IOException {
        Repository repository = repositoryMapper.toRepositoryEntity(null, repositoryRequestDto);
        repositoryService.addOrUpdateRepository(repository);

        if (repository.isEnabled()) {
            gitService.updateWidgetFromEnabledGitRepositories();
        }

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/repositories/" + repository.getId())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .body(repositoryMapper.toRepositoryDTONoWidgets(repository));
    }

    /**
     * Retrieve an existing repository by id
     * @param repositoryId The repository id
     * @return The repository
     */
    @Operation(summary = "Retrieve an existing repository by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Repository not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/repositories/{repositoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<RepositoryResponseDto> getOneById(@Parameter(name = "repositoryId", description = "The repository id", required = true, example = "1")
                                                            @PathVariable Long repositoryId) {
        Optional<Repository> optionalRepository = repositoryService.getOneById(repositoryId);
        if (!optionalRepository.isPresent()) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(repositoryMapper.toRepositoryDTONoWidgets(optionalRepository.get()));
    }

    /**
     * Update a repository by id
     */
    @Operation(summary = "Update an existing repository by id, and load it automatically if enable is selected")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Repository updated"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Repository not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PutMapping(value = "/v1/repositories/{repositoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateOneById(@Parameter(name = "repositoryId", description = "The repository id", required = true, example = "1")
                                              @PathVariable Long repositoryId,
                                              @Parameter(name = "repositoryResponseDto", description = "The repository with the new info's to update", required = true)
                                              @RequestBody RepositoryRequestDto repositoryRequestDto,
                                              @Parameter(name = "disableSync", description = "Disable the synchronization of the repository", example = "true")
                                              @RequestParam boolean disableSync) throws GitAPIException, IOException {
        if (!repositoryService.existsById(repositoryId)) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        Repository repository = repositoryMapper.toRepositoryEntity(repositoryId, repositoryRequestDto);
        repositoryService.addOrUpdateRepository(repository);

        if (!disableSync && repository.isEnabled()) {
            gitService.updateWidgetFromEnabledGitRepositories();
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Synchronize all repositories
     */
    @Operation(summary = "Synchronize all repositories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Repositories synchronized"),
            @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PutMapping(value = "/v1/repositories/synchronize")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> synchronize() throws GitAPIException, IOException {
        gitService.updateWidgetFromEnabledGitRepositories();
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve a list of widget by repository
     * @param repositoryId The repository id
     * @return The repository
     */
    @Operation(summary = "Retrieve a list of widget by repository")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Repository not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/repositories/{repositoryId}/widgets")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<List<WidgetResponseDto>> getRepositoryWidget(@Parameter(name = "repositoryId", description = "The repository id", required = true, example = "1")
                                                                       @PathVariable Long repositoryId) {
        Optional<Repository> optionalRepository = repositoryService.getOneById(repositoryId);
        if (!optionalRepository.isPresent()) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(widgetMapper.toWidgetsDTOs(optionalRepository.get().getWidgets()));
    }
}
