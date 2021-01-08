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

import io.suricate.monitoring.configuration.swagger.ApiPageable;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.repository.RepositoryRequestDto;
import io.suricate.monitoring.model.dto.api.repository.RepositoryResponseDto;
import io.suricate.monitoring.model.dto.api.widget.WidgetResponseDto;
import io.suricate.monitoring.model.entity.widget.Repository;
import io.suricate.monitoring.services.GitService;
import io.suricate.monitoring.services.api.RepositoryService;
import io.suricate.monitoring.services.mapper.RepositoryMapper;
import io.suricate.monitoring.services.mapper.WidgetMapper;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
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
     * The repository service
     */
    private final RepositoryService repositoryService;

    /**
     * The git service
     */
    private final GitService gitService;

    /**
     * The repository mapper tranform Domain object into DTO
     */
    private final RepositoryMapper repositoryMapper;

    /**
     * The widget mapper
     */
    private final WidgetMapper widgetMapper;

    /**
     * Constructor
     *
     * @param repositoryService The repository service to inject
     * @param gitService        The git service to inject
     * @param repositoryMapper  The repository mapper to inject
     * @param widgetMapper      The widget mapper
     */
    @Autowired
    public RepositoryController(final RepositoryService repositoryService,
                                final GitService gitService,
                                final RepositoryMapper repositoryMapper,
                                final WidgetMapper widgetMapper) {
        this.repositoryService = repositoryService;
        this.gitService = gitService;
        this.repositoryMapper = repositoryMapper;
        this.widgetMapper = widgetMapper;
    }

    /**
     * Get the full list of repositories
     *
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
        Page<Repository> repositoriesPaged = repositoryService.getAll(search, pageable);
        return repositoriesPaged.map(repositoryMapper::toRepositoryDtoDefault);
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
                                                           @RequestBody RepositoryRequestDto repositoryRequestDto) {
        Repository repository = repositoryMapper.toRepositoryDefaultModel(null, repositoryRequestDto);
        repositoryService.addOrUpdateRepository(repository);

        if (repository.isEnabled()) {
            this.gitService.updateWidgetFromGitRepository(repository);
        }

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/repositories/" + repository.getId())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .body(repositoryMapper.toRepositoryDtoDefault(repository));
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
        Optional<Repository> optionalRepository = repositoryService.getOneById(repositoryId);
        if (!optionalRepository.isPresent()) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(repositoryMapper.toRepositoryDtoDefault(optionalRepository.get()));
    }

    /**
     * Update a repository by id
     *
     * @return The repository updated
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
                                              @RequestBody RepositoryRequestDto repositoryRequestDto) {
        if (!repositoryService.existsById(repositoryId)) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        Repository repository = repositoryMapper.toRepositoryDefaultModel(repositoryId, repositoryRequestDto);
        this.repositoryService.addOrUpdateRepository(repository);

        if (repository.isEnabled()) {
            this.gitService.updateWidgetFromGitRepository(repository);
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
        Optional<Repository> optionalRepository = repositoryService.getOneById(repositoryId);
        if (!optionalRepository.isPresent()) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(widgetMapper.toWidgetDtosDefault(optionalRepository.get().getWidgets()));
    }
}
