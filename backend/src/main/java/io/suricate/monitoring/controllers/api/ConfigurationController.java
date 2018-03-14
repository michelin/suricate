package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.model.dto.ConfigurationDto;
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.service.api.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/configurations")
public class ConfigurationController {
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

    /**
     * The configuration Service
     */
    private final ConfigurationService configurationService;

    /**
     * Constructor
     *
     * @param configurationService Inject the configuration service
     */
    @Autowired
    public ConfigurationController(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ConfigurationDto> getAll() {
        Optional<List<Configuration>> configurations = configurationService.getAll();

        if(!configurations.isPresent()) {
            LOGGER.debug("No configurations found");
        }

        return configurationService.toDTO(configurations.get());
    }
}
