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
import io.suricate.monitoring.model.dto.api.category.CategoryParameterResponseDto;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.widgetconfiguration.WidgetConfigurationRequestDto;
import io.suricate.monitoring.model.dto.api.widgetconfiguration.WidgetConfigurationResponseDto;
import io.suricate.monitoring.model.entities.CategoryParameter;
import io.suricate.monitoring.services.CacheService;
import io.suricate.monitoring.services.api.CategoryParametersService;
import io.suricate.monitoring.services.mapper.CategoryMapper;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Configuration controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Category Parameters Controller", tags = {"Category Parameters"})
public class CategoryParametersController {
    /**
     * The category parameters service
     */
    private final CategoryParametersService categoryParametersService;

    /**
     * The category parameters mapper
     */
    private final CategoryMapper categoryMapper;

    /**
     * The cache service
     */
    private final CacheService cacheService;

    /**
     * Constructor
     * @param categoryParametersService The category parameters services
     * @param categoryMapper The category mapper
     * @param cacheService The cache service
     */
    @Autowired
    public CategoryParametersController(final CategoryParametersService categoryParametersService,
                                        final CategoryMapper categoryMapper,
                                        final CacheService cacheService) {
        this.categoryParametersService = categoryParametersService;
        this.categoryMapper = categoryMapper;
        this.cacheService = cacheService;
    }

    /**
     * Get all parameters of all categories
     * @return The list of parameters of all categories
     */
    @ApiOperation(value = "Get all parameters of all categories", response = WidgetConfigurationResponseDto.class, nickname = "getAllConfigs")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = WidgetConfigurationResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @ApiPageable
    @GetMapping(value = "/v1/category-parameters")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<CategoryParameterResponseDto> getAll(@ApiParam(name = "search", value = "Search keyword")
                                                     @RequestParam(value = "search", required = false) String search,
                                                     Pageable pageable) {
        return categoryParametersService.getAll(search, pageable).map(categoryMapper::toCategoryParameterDTO);
    }

    /**
     * Get a configuration by the key (Id)
     * @param key The key to find
     * @return The related configuration
     */
    @ApiOperation(value = "Get a configuration by the key", response = WidgetConfigurationResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = WidgetConfigurationResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Configuration not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/category-parameters/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryParameterResponseDto> getOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                                                      @PathVariable("key") final String key) {
        Optional<CategoryParameter> configurationOptional = categoryParametersService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(CategoryParameter.class, key);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(categoryMapper.toCategoryParameterDTO(configurationOptional.get()));
    }

    /**
     * Update the configuration by the key
     * @param key                           The key of the config
     * @param widgetConfigurationRequestDto The new configuration values
     * @return The config updated
     */
    @ApiOperation(value = "Update a configuration by the key")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Configuration updated"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Configuration not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/category-parameters/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                               @PathVariable("key") final String key,
                                               @ApiParam(name = "configurationResponseDto", value = "The configuration updated", required = true)
                                               @RequestBody final WidgetConfigurationRequestDto widgetConfigurationRequestDto) {
        Optional<CategoryParameter> configurationOptional = this.categoryParametersService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(CategoryParameter.class, key);
        }

        this.categoryParametersService.updateConfiguration(configurationOptional.get(), widgetConfigurationRequestDto.getValue());
        this.cacheService.clearCache("configuration");

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a configuration by key
     * @param key The configuration key
     * @return The config deleted
     */
    @ApiOperation(value = "Delete a configuration by the key")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Configuration deleted"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Configuration not found", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/category-parameters/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                               @PathVariable("key") final String key) {
        Optional<CategoryParameter> configurationOptional = this.categoryParametersService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(CategoryParameter.class, key);
        }

        this.categoryParametersService.deleteOneByKey(key);

        return ResponseEntity.noContent().build();
    }
}
