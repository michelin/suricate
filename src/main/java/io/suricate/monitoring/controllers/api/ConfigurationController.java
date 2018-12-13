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

import io.suricate.monitoring.model.dto.api.ApplicationPropertiesDto;
import io.suricate.monitoring.model.dto.api.configuration.ConfigurationRequestDto;
import io.suricate.monitoring.model.dto.api.configuration.ConfigurationResponseDto;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.service.CacheService;
import io.suricate.monitoring.service.api.ConfigurationService;
import io.suricate.monitoring.service.mapper.ConfigurationMapper;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Configuration controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Configuration Controller", tags = {"Configurations"})
public class ConfigurationController {

    /**
     * The configuration Service
     */
    private final ConfigurationService configurationService;

    /**
     * The configuration mapper for Domain/Dto tranformation
     */
    private final ConfigurationMapper configurationMapper;

    /**
     * The cache service
     */
    private final CacheService cacheService;

    /**
     * Constructor
     *
     * @param configurationService Inject the configuration service
     * @param configurationMapper  The configuration mapper
     */
    @Autowired
    public ConfigurationController(final ConfigurationService configurationService,
                                   final ConfigurationMapper configurationMapper,
                                   final CacheService cacheService) {
        this.configurationService = configurationService;
        this.configurationMapper = configurationMapper;
        this.cacheService = cacheService;
    }

    /**
     * Get the full list of configurations
     *
     * @return The list of configurations
     */
    @ApiOperation(value = "Get the full list of configurations", response = ConfigurationResponseDto.class, nickname = "getAllConfigs")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ConfigurationResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/configurations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ConfigurationResponseDto>> getAll() {
        Optional<List<Configuration>> configurationsOptional = configurationService.getAll();

        if (!configurationsOptional.isPresent()) {
            throw new NoContentException(Configuration.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(configurationMapper.toConfigurationDtosDefault(configurationsOptional.get()));
    }

    /**
     * Get a configuration by the key (Id)
     *
     * @param key The key to find
     * @return The related configuration
     */
    @ApiOperation(value = "Get a configuration by the key", response = ConfigurationResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ConfigurationResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Configuration not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/configurations/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ConfigurationResponseDto> getOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                                                @PathVariable("key") final String key) {
        Optional<Configuration> configurationOptional = configurationService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(Configuration.class, key);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(configurationMapper.toConfigurationDtoDefault(configurationOptional.get()));
    }

    /**
     * Update the configuration by the key
     *
     * @param key                     The key of the config
     * @param configurationRequestDto The new configuration values
     * @return The config updated
     */
    @ApiOperation(value = "Update a configuration by the key")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Configuration not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/configurations/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                               @PathVariable("key") final String key,
                                               @ApiParam(name = "configurationResponseDto", value = "The configuration updated", required = true)
                                               @RequestBody final ConfigurationRequestDto configurationRequestDto) {
        Optional<Configuration> configurationOptional = configurationService.getOneByKey(key);
        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(Configuration.class, key);
        }

        configurationService.updateConfiguration(configurationOptional.get(), configurationRequestDto.getValue());
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
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Configuration not found", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/configurations/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                               @PathVariable("key") final String key) {
        Optional<Configuration> configurationOptional = configurationService.getOneByKey(key);
        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(Configuration.class, key);
        }

        configurationService.deleteOneByKey(key);

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
            .body(configurationService.getAuthenticationProvider());
    }

    /**
     * Return the value needed for the frontend on the server configuration
     */
    @ApiOperation(value = "Get the server full configuration", response = ApplicationPropertiesDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ApplicationPropertiesDto.class, responseContainer = "List")
    })
    @GetMapping(value = "/v1/configurations/server")
    public ResponseEntity<List<ApplicationPropertiesDto>> getServerConfiguration() {
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(configurationService.getServerConfiguration());
    }
}
