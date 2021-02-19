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
import io.suricate.monitoring.model.dto.api.ApplicationPropertiesDto;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.widgetconfiguration.WidgetConfigurationRequestDto;
import io.suricate.monitoring.model.dto.api.widgetconfiguration.WidgetConfigurationResponseDto;
import io.suricate.monitoring.model.entities.CategoryParameter;
import io.suricate.monitoring.services.CacheService;
import io.suricate.monitoring.services.api.CategoryParametersService;
import io.suricate.monitoring.services.mapper.WidgetConfigurationMapper;
import io.suricate.monitoring.services.properties.ApplicationPropertiesService;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
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
@Api(value = "Widget Configuration Controller", tags = {"Widget Configurations"})
public class CategoryParametersController {

    /**
     * The configuration Service
     */
    private final ApplicationPropertiesService applicationPropertiesService;

    /**
     * The category parameters service
     */
    private final CategoryParametersService categoryParametersService;

    /**
     * The configuration mapper for Domain/Dto tranformation
     */
    private final WidgetConfigurationMapper widgetConfigurationMapper;

    /**
     * The cache service
     */
    private final CacheService cacheService;

    /**
     * Constructor
     *
     * @param widgetParametersService Inject the configuration service
     * @param widgetConfigurationMapper  The configuration mapper
     */
    @Autowired
    public CategoryParametersController(final ApplicationPropertiesService applicationPropertiesService,
                                        final CategoryParametersService categoryParametersService,
                                        final WidgetConfigurationMapper widgetConfigurationMapper,
                                        final CacheService cacheService) {
        this.applicationPropertiesService = applicationPropertiesService;
        this.categoryParametersService = categoryParametersService;
        this.widgetConfigurationMapper = widgetConfigurationMapper;
        this.cacheService = cacheService;
    }

    /**
     * Get the full list of configurations
     *
     * @return The list of configurations
     */
    @ApiOperation(value = "Get the full list of configurations", response = WidgetConfigurationResponseDto.class, nickname = "getAllConfigs")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = WidgetConfigurationResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @ApiPageable
    @GetMapping(value = "/v1/configurations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<WidgetConfigurationResponseDto> getAll(@ApiParam(name = "search", value = "Search keyword")
                                                       @RequestParam(value = "search", required = false) String search,
                                                       Pageable pageable) {
        Page<CategoryParameter> widgetConfigurationsPaged = categoryParametersService.getAll(search, pageable);
        return widgetConfigurationsPaged.map(widgetConfigurationMapper::toConfigurationDtoDefault);
    }

    /**
     * Get a configuration by the key (Id)
     *
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
    @GetMapping(value = "/v1/configurations/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<WidgetConfigurationResponseDto> getOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                                                      @PathVariable("key") final String key) {
        Optional<CategoryParameter> configurationOptional = categoryParametersService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(CategoryParameter.class, key);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(widgetConfigurationMapper.toConfigurationDtoDefault(configurationOptional.get()));
    }

    /**
     * Update the configuration by the key
     *
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
    @PutMapping(value = "/v1/configurations/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                               @PathVariable("key") final String key,
                                               @ApiParam(name = "configurationResponseDto", value = "The configuration updated", required = true)
                                               @RequestBody final WidgetConfigurationRequestDto widgetConfigurationRequestDto) {
        Optional<CategoryParameter> configurationOptional = categoryParametersService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(CategoryParameter.class, key);
        }

        categoryParametersService.updateConfiguration(configurationOptional.get(), widgetConfigurationRequestDto.getValue());
        cacheService.clearCache("configuration");

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a configuration by key
     *
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
    @DeleteMapping(value = "/v1/configurations/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                               @PathVariable("key") final String key) {
        Optional<CategoryParameter> configurationOptional = categoryParametersService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(CategoryParameter.class, key);
        }

        categoryParametersService.deleteOneByKey(key);

        return ResponseEntity.noContent().build();
    }

    /**
     * Return the value needed for the frontend on the server configuration
     */
    @ApiOperation(value = "Get the server configuration for authentication provider (DB, LDAP...)", response = ApplicationPropertiesDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ApplicationPropertiesDto.class)
    })
    @GetMapping(value = "/v1/configurations/authentication-provider")
    public ResponseEntity<ApplicationPropertiesDto> getAuthenticationProvider() {
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(applicationPropertiesService.getAuthenticationProvider());
    }
}
