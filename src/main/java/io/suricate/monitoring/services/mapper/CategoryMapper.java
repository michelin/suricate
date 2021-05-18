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

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.category.CategoryResponseDto;
import io.suricate.monitoring.model.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Manage the generation DTO/Model objects for Category class
 */
@Mapper(componentModel = "spring",
        uses = {
            CategoryParamMapper.class
        }
)
public abstract class CategoryMapper {

    /**
     * Map a category into a DTO
     *
     * @param category The category to map
     * @return The category as DTO
     */
    @Named("toCategoryDTO")
    @Mapping(target = "assetToken", expression = "java(category.getImage() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(category.getImage().getId()) : null )")
    @Mapping(target = "categoryParameters", source = "category.configurations", qualifiedByName = "toCategoryParameterWithoutCategoryDTO")
    public abstract CategoryResponseDto toCategoryDTO(Category category);

    /**
     * Map a category into a DTO
     *
     * @param category The category to map
     * @return The category as DTO
     */
    @Named("toCategoryWithHiddenValueParametersDTO")
    @Mapping(target = "assetToken", expression = "java(category.getImage() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(category.getImage().getId()) : null )")
    @Mapping(target = "categoryParameters", source = "category.configurations", qualifiedByName = "toCategoryParameterWithHiddenValuesDTO")
    public abstract CategoryResponseDto toCategoryWithHiddenValueParametersDTO(Category category);

    /**
     * Map a category into a DTO
     *
     * @param category The category to map
     * @return The category as DTO
     */
    @Named("toCategoryWithoutCategoryParametersDTO")
    @Mapping(target = "assetToken", expression = "java(category.getImage() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(category.getImage().getId()) : null )")
    @Mapping(target = "categoryParameters", ignore = true)
    public abstract CategoryResponseDto toCategoryWithoutCategoryParametersDTO(Category category);
}
