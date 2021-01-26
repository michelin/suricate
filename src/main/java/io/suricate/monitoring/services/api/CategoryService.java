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

import io.suricate.monitoring.model.entities.WidgetConfiguration;
import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.repositories.CategoryRepository;
import io.suricate.monitoring.services.specifications.CategorySearchSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * The service that manage categories
 */
@Service
public class CategoryService {
    /**
     * The category repository
     */
    private final CategoryRepository categoryRepository;

    /**
     * The asset service
     */
    private final AssetService assetService;

    /**
     * The configuration service
     */
    private final WidgetConfigurationService widgetConfigurationService;

    /**
     * The contructor
     *
     * @param categoryRepository         The category repository to inject
     * @param assetService               The asset service
     * @param widgetConfigurationService The configuration service
     */
    @Autowired
    public CategoryService(final CategoryRepository categoryRepository,
                           final AssetService assetService,
                           final WidgetConfigurationService widgetConfigurationService) {
        this.categoryRepository = categoryRepository;
        this.assetService = assetService;
        this.widgetConfigurationService = widgetConfigurationService;
    }

    /**
     * Get every categories order by name
     *
     * @return The list of categories
     */
    @Transactional
    @Cacheable("widget-categories")
    public Page<Category> getAll(String search, Pageable pageable) {
        return categoryRepository.findAll(new CategorySearchSpecification(search), pageable);
    }

    /**
     * Find a category by technical name
     *
     * @param technicalName The technical name of the category
     * @return The related category
     */
    public Category findByTechnicalName(final String technicalName) {
        return categoryRepository.findByTechnicalName(technicalName);
    }

    /**
     * Add or update a category
     *
     * @param category The category to add or update
     */
    @Transactional
    public void addOrUpdateCategory(Category category) {
        if (category == null) {
            return;
        }

        Category existingCategory = findByTechnicalName(category.getTechnicalName());
        if (category.getImage() != null) {
            if (existingCategory != null && existingCategory.getImage() != null) {
                category.getImage().setId(existingCategory.getImage().getId());
            }

            assetService.save(category.getImage());
        }

        if (existingCategory != null) {
            category.setId(existingCategory.getId());
        }

        // Save the configurations
        List<WidgetConfiguration> widgetConfigurations = category.getConfigurations();
        category.setConfigurations(new ArrayList<>());

        // Create/Update category
        categoryRepository.save(category);

        // Create/Update configurations
        if (widgetConfigurations != null && !widgetConfigurations.isEmpty()) {
            widgetConfigurationService.addOrUpdateConfigurations(widgetConfigurations, category);
        }
    }
}
