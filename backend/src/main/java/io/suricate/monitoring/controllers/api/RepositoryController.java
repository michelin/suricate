/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.model.dto.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.widget.RepositoryDto;
import io.suricate.monitoring.model.entity.widget.Repository;
import io.suricate.monitoring.model.mapper.widget.RepositoryMapper;
import io.suricate.monitoring.service.api.RepositoryService;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Repository Controller
 */
@RestController
@RequestMapping(value = "/api/repositories")
@Api(value = "Repository Controller", tags = {"Repository"})
public class RepositoryController {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(RepositoryController.class);

    /**
     * The repository service
     */
    private final RepositoryService repositoryService;

    /**
     * The repository mapper tranform Domain object into DTO
     */
    private final RepositoryMapper repositoryMapper;

    /**
     * Constructor
     *
     * @param repositoryService The repository service to inject
     * @param repositoryMapper  The repository mapper to inject
     */
    @Autowired
    public RepositoryController(final RepositoryService repositoryService,
                                final RepositoryMapper repositoryMapper) {
        this.repositoryService = repositoryService;
        this.repositoryMapper = repositoryMapper;
    }

    /**
     * Get the full list of repositories
     *
     * @return The list of repositories
     */
    @ApiOperation(value = "Get the full list of repositories", response = RepositoryDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = RepositoryDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<RepositoryDto>> getAll() {
        Optional<List<Repository>> optionalRepositories = repositoryService.getAllOrderByName();

        if (!optionalRepositories.isPresent()) {
            throw new NoContentException(Repository.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(repositoryMapper.toRepositoryDtosDefault(optionalRepositories.get()));
    }

    /**
     * Create a new repository
     *
     * @param repositoryDto The repository to create
     * @return The repository created
     */
    @ApiOperation(value = "Create a new repository", response = RepositoryDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = RepositoryDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RepositoryDto> createOne(@ApiParam(name = "repositoryDto", value = "The repository to create", required = true)
                                                   @RequestBody RepositoryDto repositoryDto) {
        Repository repository = repositoryMapper.toRepositoryWithoutWidgets(repositoryDto);
        repositoryService.addOrUpdateRepository(repository);

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/repositories/" + repository.getId())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(repositoryMapper.toRepositoryDtoDefault(repository));
    }

    /**
     * Retrieve an existing repository by id
     *
     * @param repositoryId The repository Id
     * @return The repository
     */
    @ApiOperation(value = "Retrieve an existing repository by id", response = RepositoryDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = RepositoryDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Repository not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{repositoryId}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RepositoryDto> getOneById(@ApiParam(name = "repositoryId", value = "The repository id", required = true)
                                                    @PathVariable Long repositoryId) {
        Optional<Repository> optionalRepository = repositoryService.getOneById(repositoryId);

        if (!optionalRepository.isPresent()) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(repositoryMapper.toRepositoryDtoDefault(optionalRepository.get()));
    }

    /**
     * Update a repository by id
     *
     * @return The repository updated
     */
    @ApiOperation(value = "Update an existing repository by id", response = RepositoryDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = RepositoryDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Repository not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{repositoryId}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RepositoryDto> updateOneById(@ApiParam(name = "repositoryId", value = "The repository id", required = true)
                                                       @PathVariable Long repositoryId,
                                                       @ApiParam(name = "repositoryDto", value = "The repository with the new info's to update", required = true)
                                                       @RequestBody RepositoryDto repositoryDto) {
        if (!repositoryService.existsById(repositoryId)) {
            throw new ObjectNotFoundException(Repository.class, repositoryId);
        }

        Repository repository = repositoryMapper.toRepositoryWithoutWidgets(repositoryDto);
        repository.setId(repositoryId);

        this.repositoryService.addOrUpdateRepository(repository);

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(this.repositoryMapper.toRepositoryDtoDefault(repository));
    }
}
