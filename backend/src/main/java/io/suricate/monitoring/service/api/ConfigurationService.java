package io.suricate.monitoring.service.api;

import io.suricate.monitoring.model.dto.ConfigurationDto;
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConfigurationService {
    /**
     * The configuration repository
     */
    private final ConfigurationRepository configurationRepository;

    /**
     * Constructor
     *
     * @param configurationRepository Inject the configuration repository
     */
    @Autowired
    public ConfigurationService(final ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    /**
     * Transform a configuration into a DTO
     *
     * @param configuration The ocnfiguration to transform
     * @return The related configuration dto
     */
    public ConfigurationDto toDTO(Configuration configuration) {
        ConfigurationDto configurationDto = new ConfigurationDto();

        configurationDto.setKey(configuration.getKey());
        configurationDto.setValue(configuration.getValue());
        configurationDto.setExport(configuration.isExport());

        return configurationDto;
    }

    public List<ConfigurationDto> toDTO(List<Configuration> configurations) {
        return configurations.stream().map(configuration -> this.toDTO(configuration)).collect(Collectors.toList());
    }

    /**
     * Get all the configurations
     *
     * @return The list of configurations
     */
    public Optional<List<Configuration>> getAll() {
        List<Configuration> configurations = this.configurationRepository.findAll();

        if(configurations == null || configurations.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(configurations);
    }
}
