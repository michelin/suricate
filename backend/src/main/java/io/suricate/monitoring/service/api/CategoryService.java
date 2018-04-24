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

import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * The service that manage categories
 */
@Service
public class CategoryService {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);

    /**
     * The category repository
     */
    private final CategoryRepository categoryRepository;

    /**
     * The asset service
     */
    private final AssetService assetService;

    /**
     * The contructor
     *
     * @param categoryRepository The category repository to inject
     * @param assetService The asset service
     */
    @Autowired
    public CategoryService(final CategoryRepository categoryRepository, final AssetService assetService) {
        this.categoryRepository = categoryRepository;
        this.assetService = assetService;
    }

    /**
     * Get every categories order by name
     * @return The list of categories
     */
    @Transactional
    @Cacheable("widget-categories")
    public List<Category> getCategoriesOrderByName() {
        return categoryRepository.findAllByOrderByNameAsc();
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
     * Method used to add or update an category
     * @param category the category to add
     */
    @Transactional
    public void addOrUpdateCategory(Category category) {
        if (category == null){
            return;
        }
        // Find and existing category with the same id
        Category currentCateg = findByTechnicalName(category.getTechnicalName());
        if (category.getImage() != null) {
            if (currentCateg != null && currentCateg.getImage() != null){
                category.getImage().setId(currentCateg.getImage().getId());
            }
            assetService.save(category.getImage());
        }

        if (currentCateg != null){
            category.setId(currentCateg.getId());
        }

        // Create/Update category
        categoryRepository.save(category);
    }
}
