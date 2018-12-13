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

package io.suricate.monitoring.service.mapper;

import io.suricate.monitoring.model.dto.api.widget.CategoryResponseDto;
import io.suricate.monitoring.model.entity.widget.Category;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Category class
 */
@Component
@Mapper(componentModel = "spring")
public abstract class CategoryMapper {

    /* ************************* TO DTO ********************************************** */

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a Category into a CategoryResponseDto
     *
     * @param category The category to transform
     * @return The related category DTO
     */
    @Named("toCategoryDtoDefault")
    @Mapping(target = "assetToken", expression = "java( category.getImage() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(category.getImage().getId()) : null )")
    public abstract CategoryResponseDto toCategoryDtoDefault(Category category);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of categories into a list of categoryDto
     *
     * @param categories The list of category to transform
     * @return The related DTO
     */
    @Named("toCategoryDtosDefault")
    @IterableMapping(qualifiedByName = "toCategoryDtoDefault")
    public abstract List<CategoryResponseDto> toCategoryDtosDefault(List<Category> categories);
}
