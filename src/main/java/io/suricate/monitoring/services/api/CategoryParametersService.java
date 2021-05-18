/*
 *
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.CategoryParameter;
import io.suricate.monitoring.model.entities.WidgetParam;
import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.suricate.monitoring.repositories.CategoryParametersRepository;
import io.suricate.monitoring.services.specifications.CategoryParametersSearchSpecification;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Manager the parameters of categories
 */
@Service
public class CategoryParametersService {

    /**
     * The category repository
     */
    private final CategoryParametersRepository categoryParametersRepository;

    /**
     * The String encryptor
     */
    private final StringEncryptor stringEncryptor;

    /**
     * Constructor
     *
     * @param categoryParametersRepository The category parameters service
     * @param stringEncryptor              The String encryptor
     */
    public CategoryParametersService(CategoryParametersRepository categoryParametersRepository,
                                     @Qualifier("jasyptStringEncryptor") final StringEncryptor stringEncryptor) {
        this.categoryParametersRepository = categoryParametersRepository;
        this.stringEncryptor = stringEncryptor;
    }

    /**
     * Get the list of parameters by category ID
     *
     * @param categoryId The category ID
     * @return The list of parameters
     */
    @Transactional
    public Optional<List<CategoryParameter>> getParametersByCategoryId(Long categoryId) {
        return categoryParametersRepository.findCategoryParametersByCategoryId(categoryId);
    }

    /**
     * Get all the category parameters
     *
     * @return The list of category parameters
     */
    public Page<CategoryParameter> getAll(String search, Pageable pageable) {
        return categoryParametersRepository.findAll(new CategoryParametersSearchSpecification(search), pageable);
    }

    /**
     * Get a parameter by key
     *
     * @param key The key
     * @return The category parameter as optional
     */
    public Optional<CategoryParameter> getOneByKey(final String key) {
        return categoryParametersRepository.findById(key);
    }

    /**
     * Update a category parameter
     *
     * @param categoryParameter The category parameter to update
     * @param newValue          The new value to set
     */
    public void updateConfiguration(CategoryParameter categoryParameter, final String newValue) {
        categoryParameter.setValue(categoryParameter.getDataType() == DataTypeEnum.PASSWORD ?
                stringEncryptor.encrypt(newValue) : newValue);

        categoryParametersRepository.save(categoryParameter);
    }

    /**
     * Delete a category parameter by its key
     *
     * @param key The key of the configuration
     */
    public void deleteOneByKey(String key) {
        categoryParametersRepository.deleteById(key);
    }

    /**
     * Add or update a list of category parameters
     *
     * @param categoryParameters The category parameters
     * @param category           The related category
     */
    @Transactional
    public void addOrUpdateCategoryConfiguration(Set<CategoryParameter> categoryParameters, Category category) {
        for (CategoryParameter categoryParameter : categoryParameters) {
            Optional<CategoryParameter> currentConfiguration = categoryParametersRepository.findById(categoryParameter.getKey());
            categoryParameter.setCategory(category);

            if (currentConfiguration.isPresent()) {
                categoryParameter.setValue(currentConfiguration.get().getValue());
                categoryParameter.setExport(currentConfiguration.get().isExport());
            }

            categoryParametersRepository.save(categoryParameter);
        }
    }

    /**
     * Convert category parameters into widget parameters
     *
     * @param categoryParameter The category parameters to convert
     * @return The widget parameters
     */
    public static WidgetParam convertCategoryParametersToWidgetParameters(CategoryParameter categoryParameter) {
        WidgetParam param = new WidgetParam();
        param.setName(categoryParameter.getKey());
        param.setDefaultValue(categoryParameter.getValue());
        param.setType(categoryParameter.getDataType());
        param.setDescription(categoryParameter.getKey());
        param.setRequired(true);

        return param;
    }
}
