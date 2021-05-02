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

import io.suricate.monitoring.model.entities.CategoryParameter;
import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.Widget;
import io.suricate.monitoring.model.entities.WidgetParam;
import io.suricate.monitoring.repositories.CategoryRepository;
import io.suricate.monitoring.services.specifications.CategorySearchSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manage the categories
 */
@Service
public class CategoryService {

    /**
     * The asset service
     */
    private final AssetService assetService;

    /**
     * The category repository
     */
    private final CategoryRepository categoryRepository;

    /**
     * The category parameters service
     */
    private final CategoryParametersService categoryParametersService;

    /**
     * Constructor
     *
     * @param assetService               The asset service
     * @param categoryRepository         The category repository
     * @param categoryParametersService  The category parameters service
     */
    @Autowired
    public CategoryService(final AssetService assetService,
                           final CategoryRepository categoryRepository,
                           final CategoryParametersService categoryParametersService) {
        this.assetService = assetService;
        this.categoryRepository = categoryRepository;
        this.categoryParametersService = categoryParametersService;
    }

    /**
     * Get all the categories
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
        List<CategoryParameter> categoryOldConfigurations = existingCategory != null ?
                new ArrayList<>(existingCategory.getCategoryParameters()) : new ArrayList<>();

        List<CategoryParameter> categoryNewConfigurations = category.getCategoryParameters();
        category.setCategoryParameters(new ArrayList<>());

        // Create/Update category
        categoryRepository.save(category);

        // Create/Update configurations
        if (categoryNewConfigurations != null && !categoryNewConfigurations.isEmpty()) {
            List<String> categoryNewConfigurationsKeys = categoryNewConfigurations
                    .stream()
                    .map(CategoryParameter::getId)
                    .collect(Collectors.toList());

            for (CategoryParameter categoryConfiguration : categoryOldConfigurations) {
                if (!categoryNewConfigurationsKeys.contains(categoryConfiguration.getId())) {
                    categoryParametersService.deleteOneByKey(categoryConfiguration.getId());
                }
            }

            categoryParametersService.addOrUpdateCategoryConfiguration(categoryNewConfigurations, category);
        }
    }

    /**
     * Get the parameters of the category linked with the given widget
     *
     * @param widget The widget
     * @return The category parameters
     */
    public List<WidgetParam> getCategoryParametersByWidget(final Widget widget) {
        Optional<List<CategoryParameter>> configurationsOptional = categoryParametersService
                .getParametersByCategoryId(widget.getCategory().getId());

        return configurationsOptional
                .map(configurations -> configurations
                        .stream()
                        .map(CategoryParametersService::convertCategoryParametersToWidgetParameters)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }
}
