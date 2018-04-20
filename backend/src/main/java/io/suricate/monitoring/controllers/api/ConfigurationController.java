package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.model.dto.ConfigurationDto;
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.model.mapper.ConfigurationMapper;
import io.suricate.monitoring.service.api.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
     * @param configurationMapper The configuration mapper
     */
    @Autowired
    public ConfigurationController(final ConfigurationService configurationService,
                                   final ConfigurationMapper configurationMapper) {
        this.configurationService = configurationService;
        this.configurationMapper = configurationMapper;
    }

    /**
     * Get the full list of configurations
     * @return The list of configurations
     */
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ConfigurationDto>> getAll() {
        Optional<List<Configuration>> configurations = configurationService.getAll();

        if(!configurations.isPresent()) {
            LOGGER.debug("No configurations found");

            return ResponseEntity
                .noContent()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(configurationMapper.toConfigurationDtosDefault(configurations.get()));
    }
}
