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
import io.suricate.monitoring.services.git.GitService;
import io.suricate.monitoring.services.api.RepositoryService;
import io.suricate.monitoring.services.mapper.RepositoryMapper;
import io.suricate.monitoring.services.mapper.WidgetMapper;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.suricate.monitoring.utils.exceptions.RepositorySyncException;
import io.swagger.annotations.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Repository Controller
 */
@RestController
@RequestMapping(value = "/api")
@Api(value = "Repository Controller", tags = {"Repositories"})
public class RepositoryController {
    /**
     * Repository service
     */
    @Autowired
    private RepositoryService repositoryService;

    /**
     * Git service
     */
    @Autowired
    private GitService gitService;

    /**
     * Repository mapper
     */
    @Autowired
    private RepositoryMapper repositoryMapper;

    /**
     * Widget mapper
     */
    @Autowired
    private WidgetMapper widgetMapper;

    /**
     * Get the full list of repositories
     * @return The list of repositories
     */
    @ApiOperation(value = "Get the full list of repositories", response = RepositoryResponseDto.class, nickname = "getAllRepos")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = RepositoryResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @ApiPageable
    @GetMapping(value = "/v1/repositories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Page<RepositoryResponseDto> getAll(@ApiParam(name = "search", value = "Search keyword")
                                              @RequestParam(value = "search", required = false) String search,
                                              Pageable pageable) {
        return repositoryService
                .getAll(search, pageable)
                .map(this.repositoryMapper::toRepositoryDTONoWidgets);
    }

    /**
     * Create a new repository
     *
     * @param repositoryRequestDto The repository to create
     * @return The repository created
     */
    @ApiOperation(value = "Create a new repository, and load it automatically if enable is selected", response = RepositoryResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = RepositoryResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/repositories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RepositoryResponseDto> createOne(@ApiParam(name = "repositoryResponseDto", value = "The repository to create", required = true)
                                                           @RequestBody RepositoryRequestDto repositoryRequestDto) throws GitAPIException, IOException {
        Repository repository = this.repositoryMapper.toRepositoryEntity(null, repositoryRequestDto);
        repositoryService.addOrUpdateRepository(repository);

        if (repository.isEnabled()) {
            gitService.updateWidgetsFromRepository(repository);
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
     *
     * @param repositoryId The repository Id
     * @return The repository
     */
    @ApiOperation(value = "Retrieve an existing repository by id", response = RepositoryResponseDto.class, nickname = "getRepoById")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = RepositoryResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Repository not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/repositories/{repositoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<RepositoryResponseDto> getOneById(@ApiParam(name = "repositoryId", value = "The repository id", required = true)
                                                            @PathVariable Long repositoryId) {
        Optional<Repository> optionalRepository = this.repositoryService.getOneById(repositoryId);
        if (!optionalRepository.isPresent()) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(this.repositoryMapper.toRepositoryDTONoWidgets(optionalRepository.get()));
    }

    /**
     * Update a repository by id
     */
    @ApiOperation(value = "Update an existing repository by id, and load it automatically if enable is selected")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Repository updated"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Repository not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/repositories/{repositoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateOneById(@ApiParam(name = "repositoryId", value = "The repository id", required = true)
                                              @PathVariable Long repositoryId,
                                              @ApiParam(name = "repositoryResponseDto", value = "The repository with the new info's to update", required = true)
                                              @RequestBody RepositoryRequestDto repositoryRequestDto) throws GitAPIException, IOException {
        if (!this.repositoryService.existsById(repositoryId)) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        Repository repository = this.repositoryMapper.toRepositoryEntity(repositoryId, repositoryRequestDto);
        this.repositoryService.addOrUpdateRepository(repository);

        if (repository.isEnabled()) {
            gitService.updateWidgetsFromRepository(repository);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Reload a repository by id
     */
    @ApiOperation(value = "Reload an existing repository by id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Repository reloaded"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Repository not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/repositories/{repositoryId}/reload")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> reload(@ApiParam(name = "repositoryId", value = "The repository id", required = true)
                                       @PathVariable Long repositoryId) throws GitAPIException, IOException {
        Optional<Repository> optionalRepository = this.repositoryService.getOneById(repositoryId);
        if (!optionalRepository.isPresent()) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        Repository repository = optionalRepository.get();
        if (repository.isEnabled()) {
            gitService.updateWidgetsFromRepository(repository);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve a list of widget by repository
     *
     * @param repositoryId The repository Id
     * @return The repository
     */
    @ApiOperation(value = "Retrieve a list of widget by repository", response = WidgetResponseDto.class, nickname = "getRepositoryWidget")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = WidgetResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Repository not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/repositories/{repositoryId}/widgets")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<List<WidgetResponseDto>> getRepositoryWidget(@ApiParam(name = "repositoryId", value = "The repository id", required = true)
                                                                       @PathVariable Long repositoryId) {
        Optional<Repository> optionalRepository = this.repositoryService.getOneById(repositoryId);
        if (!optionalRepository.isPresent()) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(this.widgetMapper.toWidgetsDTOs(optionalRepository.get().getWidgets()));
    }
}
