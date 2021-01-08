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

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.configuration.ApplicationProperties;
import io.suricate.monitoring.model.dto.api.ApplicationPropertiesDto;
import io.suricate.monitoring.model.entity.WidgetConfiguration;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.widget.WidgetParam;
import io.suricate.monitoring.model.enums.DataType;
import io.suricate.monitoring.repositories.WidgetConfigurationRepository;
import io.suricate.monitoring.services.specifications.WidgetConfigurationSearchSpecification;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Configuration service
 */
@Service
public class WidgetConfigurationService {
    /**
     * The configuration repository
     */
    private final WidgetConfigurationRepository widgetConfigurationRepository;

    /**
     * The application properties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * The string encryptor
     */
    private StringEncryptor stringEncryptor;

    /**
     * Constructor
     *
     * @param widgetConfigurationRepository Inject the configuration repository
     * @param applicationProperties         The application properties to inject
     * @param stringEncryptor               The string encryptor
     */
    @Autowired
    public WidgetConfigurationService(final WidgetConfigurationRepository widgetConfigurationRepository,
                                      final ApplicationProperties applicationProperties,
                                      @Qualifier("jasyptStringEncryptor") final StringEncryptor stringEncryptor) {
        this.widgetConfigurationRepository = widgetConfigurationRepository;
        this.applicationProperties = applicationProperties;
        this.stringEncryptor = stringEncryptor;
    }

    /**
     * Convert configuration to widget param
     *
     * @param widgetConfiguration Configuration to convert
     * @return widget param newly created
     */
    public static WidgetParam initParamFromConfiguration(WidgetConfiguration widgetConfiguration) {
        WidgetParam param = new WidgetParam();
        param.setName(widgetConfiguration.getKey());
        param.setDefaultValue(widgetConfiguration.getValue());
        param.setType(widgetConfiguration.getDataType());
        param.setDescription(widgetConfiguration.getKey());
        param.setRequired(true);
        return param;
    }

    /**
     * Get all the configurations
     *
     * @return The list of configurations
     */
    public Page<WidgetConfiguration> getAll(String search, Pageable pageable) {
        return widgetConfigurationRepository.findAll(new WidgetConfigurationSearchSpecification(search), pageable);
    }

    /**
     * Get a configuration by key
     *
     * @param key The key to find
     * @return The configuration as optional
     */
    public Optional<WidgetConfiguration> getOneByKey(final String key) {
        return widgetConfigurationRepository.findById(key);
    }

    /**
     * Update a configuration
     *
     * @param widgetConfiguration The config to update
     * @param newValue            The new value
     */
    public void updateConfiguration(WidgetConfiguration widgetConfiguration, final String newValue) {
        widgetConfiguration.setValue(widgetConfiguration.getDataType() == DataType.PASSWORD ? stringEncryptor.encrypt(newValue) : newValue);
        widgetConfigurationRepository.save(widgetConfiguration);
    }

    /**
     * Delete a configuration by the key
     *
     * @param key The key of the configuration
     */
    public void deleteOneByKey(String key) {
        widgetConfigurationRepository.deleteById(key);
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
     * Get the list of configurations for a category
     *
     * @param categoryId The category
     * @return The list of configurations
     */
    @Transactional
    public Optional<List<WidgetConfiguration>> getConfigurationForCategory(Long categoryId) {
        return widgetConfigurationRepository.findConfigurationByCategoryId(categoryId);
    }

    /**
     * Add or update a single configuration
     *
     * @param widgetConfiguration The configuration
     * @param category            The related category
     */
    @Transactional
    public void addOrUpdateConfiguration(WidgetConfiguration widgetConfiguration, Category category) {
        if (widgetConfiguration == null) {
            return;
        }

        Optional<WidgetConfiguration> currentConfiguration = widgetConfigurationRepository.findById(widgetConfiguration.getKey());
        widgetConfiguration.setCategory(category);

        if (currentConfiguration.isPresent()) {
            widgetConfiguration.setValue(currentConfiguration.get().getValue());
            widgetConfiguration.setExport(currentConfiguration.get().isExport());
        }

        widgetConfigurationRepository.save(widgetConfiguration);
    }

    /**
     * Add or update a list of configurations
     *
     * @param widgetConfigurations The configurations
     * @param category             The category
     */
    @Transactional
    public void addOrUpdateConfigurations(List<WidgetConfiguration> widgetConfigurations, Category category) {
        widgetConfigurations.forEach(configuration -> this.addOrUpdateConfiguration(configuration, category));
    }
}
