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
import io.suricate.monitoring.model.dto.api.export.ImportExportCategoryDto;
import io.suricate.monitoring.model.dto.api.export.ImportExportCategoryParameterDto;
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
     * Map a category into an import export category DTO.
     *
     * @param category The category to map
     * @return The import export category as DTO
     */
    @Named("toImportExportCategoryDTO")
    @Mapping(target = "image", source = "category.image", qualifiedByName = "toImportExportAssetDTO")
    @Mapping(target = "categoryParameters", source = "category.configurations", qualifiedByName = "toImportExportCategoryParameterDTO")
    @Mapping(target = "widgets", qualifiedByName = "toImportExportWidgetDTO")
    public abstract ImportExportCategoryDto toImportExportCategoryDTO(Category category);

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
     * Map a category parameter into an import export DTO
     * @param categoryParameter The category parameter to map
     * @return The import export category parameter as DTO
     */
    @Named("toImportExportCategoryParameterDTO")
    public abstract ImportExportCategoryParameterDto toImportExportCategoryParameterDTO(CategoryParameter categoryParameter);

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

    /**
     * Map an import export category DTO as entity
     * @param importExportCategoryDto The category DTO to map
     * @return The category as entity
     */
    @Named("toCategoryEntity")
    @Mapping(target = "image", qualifiedByName = "toAssetEntity")
    @Mapping(target = "widgets", qualifiedByName = "toWidgetEntity")
    @Mapping(target = "configurations", source = "categoryParameters", qualifiedByName = "toCategoryParameterEntity")
    public abstract Category toCategoryEntity(ImportExportCategoryDto importExportCategoryDto);

    /**
     * Map an import export category parameter DTO as entity
     * @param importExportCategoryParameterDto The category parameter DTO to map
     * @return The category parameter as entity
     */
    @Named("toCategoryParameterEntity")
    public abstract CategoryParameter toCategoryParameterEntity(ImportExportCategoryParameterDto importExportCategoryParameterDto);
}

