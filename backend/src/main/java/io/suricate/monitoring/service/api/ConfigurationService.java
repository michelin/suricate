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

package io.suricate.monitoring.service.api;

import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.repository.ConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Configuration service
 */
@Service
public class ConfigurationService {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

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
     * Get all the configurations
     *
     * @return The list of configurations
     */
    public Optional<List<Configuration>> getAll() {
        List<Configuration> configurations = this.configurationRepository.findAll();

        if (configurations == null || configurations.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(configurations);
    }

    /**
     * Get a configuration by key
     *
     * @param key The key to find
     * @return The configuration as optional
     */
    public Optional<Configuration> getOneByKey(final String key) {
        Configuration configuration = configurationRepository.findOne(key);

        if (configuration == null) {
            return Optional.empty();
        }

        return Optional.of(configuration);
    }

    /**
     * Update a configuration
     *
     * @param configuration The config to update
     * @param newValue      The new value
     * @return The config updated
     */
    public Configuration updateConfiguration(Configuration configuration, final String newValue) {
        configuration.setValue(newValue);
        return configurationRepository.save(configuration);
    }

    /**
     * Get the configurations for widgets
     *
     * @return The list of config for the widgets
     */
    public List<Configuration> getConfigurationForWidgets() {
        return this.configurationRepository.findConfigurationForWidgets();
    }

    /**
     * Add or update a single configuration
     *
     * @param configuration The configuration
     * @param category      The related category
     */
    @Transactional
    public void addOrUpdateConfiguration(Configuration configuration, Category category) {
        if (configuration == null) {
            return;
        }

        Configuration currentConfiguration = configurationRepository.findOne(configuration.getKey());
        configuration.setCategory(category);

        if (currentConfiguration != null) {
            configuration.setValue(currentConfiguration.getValue());
            configuration.setExport(currentConfiguration.isExport());
        }

        configurationRepository.save(configuration);
    }

    /**
     * Add or update a list of configurations
     *
     * @param configurations The configurations
     * @param category       The category
     */
    @Transactional
    public void addOrUpdateConfigurations(List<Configuration> configurations, Category category) {
        configurations.forEach(configuration -> this.addOrUpdateConfiguration(configuration, category));
    }
}
