/*
 * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.service.api;

import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.CategoryParameter;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.entity.WidgetParam;
import com.michelin.suricate.repository.CategoryRepository;
import com.michelin.suricate.service.specification.CategorySearchSpecification;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Category service.
 */
@Service
public class CategoryService {
    @Autowired
    private AssetService assetService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryParametersService categoryParametersService;

    /**
     * Get all the categories.
     *
     * @return The list of categories
     */
    @Cacheable("widget-categories")
    @Transactional(readOnly = true)
    public Page<Category> getAll(String search, Pageable pageable) {
        return categoryRepository.findAll(new CategorySearchSpecification(search), pageable);
    }

    /**
     * Find a category by technical name.
     *
     * @param technicalName The technical name of the category
     * @return The related category
     */
    public Category findByTechnicalName(final String technicalName) {
        return categoryRepository.findByTechnicalName(technicalName);
    }

    /**
     * Add or update a category.
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
        List<CategoryParameter> categoryOldConfigurations = existingCategory != null
            ? new ArrayList<>(existingCategory.getConfigurations()) : new ArrayList<>();

        Set<CategoryParameter> categoryNewConfigurations = category.getConfigurations();
        category.setConfigurations(new HashSet<>());

        // Create/Update category
        categoryRepository.save(category);

        // Create/Update configurations
        if (categoryNewConfigurations != null && !categoryNewConfigurations.isEmpty()) {
            List<String> categoryNewConfigurationsKeys = categoryNewConfigurations
                .stream()
                .map(CategoryParameter::getId)
                .toList();

            for (CategoryParameter categoryConfiguration : categoryOldConfigurations) {
                if (!categoryNewConfigurationsKeys.contains(categoryConfiguration.getId())) {
                    categoryParametersService.deleteOneByKey(categoryConfiguration.getId());
                }
            }

            categoryParametersService.addOrUpdateCategoryConfiguration(categoryNewConfigurations, category);
        }
    }

    /**
     * Get the parameters of the category linked with the given widget.
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
                .toList())
            .orElseGet(ArrayList::new);
    }
}
