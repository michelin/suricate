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

import io.suricate.monitoring.model.dto.ApplicationPropertiesDto;
import io.suricate.monitoring.model.dto.ConfigurationDto;
import io.suricate.monitoring.model.dto.error.ApiErrorDto;
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.model.mapper.ConfigurationMapper;
import io.suricate.monitoring.service.api.ConfigurationService;
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

import java.util.List;
import java.util.Optional;

/**
 * Configuration controller
 */
@RestController
@RequestMapping("/api/configurations")
@Api(value = "Configuration Controller", tags = {"Configuration"})
public class ConfigurationController {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

    /**
     * The configuration Service
     */
    private final ConfigurationService configurationService;

    /**
     * The configuration mapper for Domain/Dto tranformation
     */
    private final ConfigurationMapper configurationMapper;

    /**
     * Constructor
     *
     * @param configurationService Inject the configuration service
     * @param configurationMapper  The configuration mapper
     */
    @Autowired
    public ConfigurationController(final ConfigurationService configurationService,
                                   final ConfigurationMapper configurationMapper) {
        this.configurationService = configurationService;
        this.configurationMapper = configurationMapper;
    }

    /**
     * Get the full list of configurations
     *
     * @return The list of configurations
     */
    @ApiOperation(value = "Get the full list of configurations", response = ConfigurationDto.class, nickname = "getAllConfigs")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ConfigurationDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ConfigurationDto>> getAll() {
        Optional<List<Configuration>> configurations = configurationService.getAll();

        if (!configurations.isPresent()) {
            throw new NoContentException(Configuration.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(configurationMapper.toConfigurationDtosDefault(configurations.get()));
    }

    /**
     * Get a configuration by the key (Id)
     *
     * @param key The key to find
     * @return The related configuration
     */
    @ApiOperation(value = "Get a configuration by the key", response = ConfigurationDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ConfigurationDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Configuration not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ConfigurationDto> getOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                                        @PathVariable("key") final String key) {
        Optional<Configuration> configurationOptional = configurationService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(Configuration.class, key);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(configurationMapper.toConfigurationDtoDefault(configurationOptional.get()));
    }

    /**
     * Update the configuration by the key
     *
     * @param key              The key of the config
     * @param configurationDto The new configuration values
     * @return The config updated
     */
    @ApiOperation(value = "Update a configuration by the key", response = ConfigurationDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ConfigurationDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Configuration not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ConfigurationDto> updateOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                                           @PathVariable("key") final String key,
                                                           @ApiParam(name = "configurationDto", value = "The configuration upated", required = true)
                                                           @RequestBody final ConfigurationDto configurationDto) {
        Optional<Configuration> configurationOptional = configurationService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(Configuration.class, key);
        }

        Configuration configuration = configurationOptional.get();
        configuration = configurationService.updateConfiguration(configuration, configurationDto.getValue());

        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noCache())
            .contentType(MediaType.APPLICATION_JSON)
            .body(configurationMapper.toConfigurationDtoDefault(configuration));
    }

    /**
     * Delete a configuration by key
     *
     * @param key The configuration key
     * @return The config deleted
     */
    @ApiOperation(value = "Delete a configuration by the key", response = ConfigurationDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ConfigurationDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Configuration not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ConfigurationDto> deleteOneByKey(@ApiParam(name = "key", value = "The configuration key", required = true)
                                                           @PathVariable("key") final String key) {
        Optional<Configuration> configurationOptional = configurationService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(Configuration.class, key);
        }

        configurationService.deleteOneByKey(key);
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(configurationMapper.toConfigurationDtoDefault(configurationOptional.get()));
    }

    /**
     * Return the value needed for the frontend on the server configuration
     */
    @ApiOperation(value = "Get the server configuration for authentication provider (DB, LDAP...)", response = ApplicationPropertiesDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = ApplicationPropertiesDto.class)
    })
    @RequestMapping(value = "/authentication-provider", method = RequestMethod.GET)
    public ResponseEntity<ApplicationPropertiesDto> getAuthenticationProvider() {
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(configurationService.getAuthenticationProvider());
    }

    /**
     * Return the value needed for the frontend on the server configuration
     */
    @ApiOperation(value = "Get the server full configuration", response = ApplicationPropertiesDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ApplicationPropertiesDto.class, responseContainer = "List")
    })
    @RequestMapping(value = "/server", method = RequestMethod.GET)
    public ResponseEntity<List<ApplicationPropertiesDto>> getServerConfiguration() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(configurationService.getServerConfiguration());
    }
}
