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

import io.suricate.monitoring.configuration.ApplicationProperties;
import io.suricate.monitoring.model.dto.api.ApplicationPropertiesDto;
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.widget.WidgetParam;
import io.suricate.monitoring.model.enums.WidgetVariableType;
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
     * The application properties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Constructor
     *
     * @param configurationRepository Inject the configuration repository
     * @param applicationProperties   The application properties to inject
     */
    @Autowired
    public ConfigurationService(final ConfigurationRepository configurationRepository,
                                final ApplicationProperties applicationProperties) {
        this.configurationRepository = configurationRepository;
        this.applicationProperties = applicationProperties;
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
        return configurationRepository.findById(key);
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
     * Delete a configuration by the key
     *
     * @param key The key of the configuration
     */
    public void deleteOneByKey(String key) {
        configurationRepository.deleteById(key);
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
     * Get the server configuration properties
     *
     * @return The list of usefull server configuration properties
     */
    public ApplicationPropertiesDto getAuthenticationProvider() {
        return new ApplicationPropertiesDto("authentication.provider", applicationProperties.authentication.provider, "The user provider source (Database or LDAP)");
    }

    /**
     * FIXME : MVT
     * @return
     */
    public List<ApplicationPropertiesDto> getServerConfiguration() {
        // FIXME : MVT
        return null;
    }

    @Transactional
    public List<Configuration> getConfigurationForCategory(Long categoryId) {
        return configurationRepository.findConfigurationByCategoryId(categoryId);
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

        Optional<Configuration> currentConfiguration = configurationRepository.findById(configuration.getKey());
        configuration.setCategory(category);

        if (currentConfiguration.isPresent()) {
            configuration.setValue(currentConfiguration.get().getValue());
            configuration.setExport(currentConfiguration.get().isExport());
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


    /**
     * Convert configuration to widget param
     * @param configuration Configuration to convert
     * @return widget param newly created
     */
    public static WidgetParam initParamFromConfiguration(Configuration configuration) {
        WidgetParam param = new WidgetParam();
        param.setName(configuration.getKey());
        param.setDefaultValue(configuration.getValue());
        param.setType(WidgetVariableType.valueOf(configuration.getDataType().toString()));
        param.setDescription(configuration.getKey());
        return param;
    }
}
