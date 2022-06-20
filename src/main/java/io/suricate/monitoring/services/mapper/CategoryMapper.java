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

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.category.CategoryParameterResponseDto;
import io.suricate.monitoring.model.dto.api.category.CategoryResponseDto;
import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.CategoryParameter;
import org.jasypt.encryption.StringEncryptor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Manage the generation DTO/Model objects for Category class
 */
@Mapper(componentModel = "spring",
        uses = {
                AssetMapper.class,
                WidgetMapper.class
        })
public abstract class CategoryMapper {
    /**
     * String encryptor
     */
    @Autowired
    @Qualifier("jasyptStringEncryptor")
    StringEncryptor stringEncryptor;

    /**
     * Map a category into a DTO. Ignore the category parameters
     *
     * @param category The category to map
     * @return The category as DTO
     */
    @Named("toCategoryWithoutParametersDTO")
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "widgets", ignore = true)
    @Mapping(target = "categoryParameters", ignore = true)
    @Mapping(target = "assetToken", expression = "java(category.getImage() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(category.getImage().getId()) : null )")
    public abstract CategoryResponseDto toCategoryWithoutParametersDTO(Category category);

    /**
     * Map a category into a DTO. Hide the category parameter values
     *
     * @param category The category to map
     * @return The category as DTO
     */
    @Named("toCategoryWithHiddenValueParametersDTO")
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "widgets", ignore = true)
    @Mapping(target = "assetToken", expression = "java(category.getImage() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(category.getImage().getId()) : null )")
    @Mapping(target = "categoryParameters", source = "category.configurations", qualifiedByName = "toCategoryParameterWithHiddenValuesDTO")
    public abstract CategoryResponseDto toCategoryWithHiddenValueParametersDTO(Category category);

    /**
     * Map a category parameter into a DTO
     *
     * @param categoryParameter The category parameter to map
     * @return The category parameter as DTO
     */
    @Named("toCategoryParameterDTO")
    @Mapping(target = "category", qualifiedByName = "toCategoryWithoutParametersDTO")
    @Mapping(target = "value", expression = "java(" +
            "categoryParameter.getDataType() == io.suricate.monitoring.model.enums.DataTypeEnum.PASSWORD ? stringEncryptor.decrypt(categoryParameter.getValue()) : categoryParameter.getValue())")
    public abstract CategoryParameterResponseDto toCategoryParameterDTO(CategoryParameter categoryParameter);

    /**
     * Map a category parameter into a DTO. Hide the value
     *
     * @param categoryParameter The category parameter to map
     * @return The category parameter as DTO
     */
    @Named("toCategoryParameterWithHiddenValuesDTO")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "value", expression = "java(" +
            "categoryParameter.getDataType() == io.suricate.monitoring.model.enums.DataTypeEnum.PASSWORD ? org.apache.commons.lang3.StringUtils.EMPTY : categoryParameter.getValue())")
    public abstract CategoryParameterResponseDto toCategoryParameterWithHiddenValuesDTO(CategoryParameter categoryParameter);
}

