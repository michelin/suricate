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

package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.category.CategoryParameterResponseDto;
import com.michelin.suricate.model.dto.api.category.CategoryResponseDto;
import com.michelin.suricate.model.entities.Category;
import com.michelin.suricate.model.entities.CategoryParameter;
import org.jasypt.encryption.StringEncryptor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Category mapper.
 */
@Mapper(componentModel = "spring",
    uses = {
        AssetMapper.class,
        WidgetMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CategoryMapper {
    /**
     * String encryptor.
     */
    @Autowired
    @Qualifier("jasyptStringEncryptor")
    StringEncryptor stringEncryptor;

    /**
     * Map a category into a DTO. Ignore the category parameters.
     *
     * @param category The category to map
     * @return The category as DTO
     */
    @Named("toCategoryWithoutParametersDto")
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "widgets", ignore = true)
    @Mapping(target = "categoryParameters", ignore = true)
    @Mapping(target = "assetToken", expression = "java(category.getImage() != null "
        + "? com.michelin.suricate.utils.IdUtils.encrypt(category.getImage().getId()) : null )")
    public abstract CategoryResponseDto toCategoryWithoutParametersDto(Category category);

    /**
     * Map a category into a DTO. Hide the category parameter values.
     *
     * @param category The category to map
     * @return The category as DTO
     */
    @Named("toCategoryWithHiddenValueParametersDto")
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "widgets", ignore = true)
    @Mapping(target = "assetToken", expression = "java(category.getImage() != null "
        + "? com.michelin.suricate.utils.IdUtils.encrypt(category.getImage().getId()) : null )")
    @Mapping(target = "categoryParameters", source = "category.configurations",
        qualifiedByName = "toCategoryParameterWithHiddenValuesDTO")
    public abstract CategoryResponseDto toCategoryWithHiddenValueParametersDto(Category category);

    /**
     * Map a category parameter into a DTO.
     *
     * @param categoryParameter The category parameter to map
     * @return The category parameter as DTO
     */
    @Named("toCategoryParameterDto")
    @Mapping(target = "category", qualifiedByName = "toCategoryWithoutParametersDto")
    @Mapping(target = "value", expression = "java("
        + "categoryParameter.getDataType() == com.michelin.suricate.model.enums.DataTypeEnum.PASSWORD "
        + "? stringEncryptor.decrypt(categoryParameter.getValue()) : categoryParameter.getValue())")
    public abstract CategoryParameterResponseDto toCategoryParameterDto(CategoryParameter categoryParameter);

    /**
     * Map a category parameter into a DTO. Hide the value.
     *
     * @param categoryParameter The category parameter to map
     * @return The category parameter as DTO
     */
    @Named("toCategoryParameterWithHiddenValuesDTO")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "value", expression = "java("
        + "categoryParameter.getDataType() == com.michelin.suricate.model.enums.DataTypeEnum.PASSWORD"
        + " ? org.apache.commons.lang3.StringUtils.EMPTY : categoryParameter.getValue())")
    public abstract CategoryParameterResponseDto toCategoryParameterWithHiddenValuesDto(
        CategoryParameter categoryParameter);
}

