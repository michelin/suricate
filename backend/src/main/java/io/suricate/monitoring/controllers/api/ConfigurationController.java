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
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.model.mapper.ConfigurationMapper;
import io.suricate.monitoring.service.api.ConfigurationService;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
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
    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ConfigurationDto> getOneByKey(@PathVariable("key") final String key) {
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
    @RequestMapping(value = "/{key}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ConfigurationDto> updateOneByKey(@PathVariable("key") final String key,
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
    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ConfigurationDto> deleteOneByKey(@PathVariable("key") final String key) {
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
    @RequestMapping(value = "/application", method = RequestMethod.GET)
    public ResponseEntity<List<ApplicationPropertiesDto>> getServerConfigurations() {
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(configurationService.getServerConfigurations());
    }
}
