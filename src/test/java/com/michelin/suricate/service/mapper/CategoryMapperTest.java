/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import com.michelin.suricate.model.dto.api.category.CategoryParameterResponseDto;
import com.michelin.suricate.model.dto.api.category.CategoryResponseDto;
import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.CategoryParameter;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import com.michelin.suricate.util.IdUtils;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryMapperTest {
    @InjectMocks
    private CategoryMapperImpl categoryMapper;

    @Test
    void shouldToCategoryWithoutParametersDto() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Category category = getCategory();

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");

            CategoryResponseDto actual = categoryMapper.toCategoryWithoutParametersDto(category);

            assertEquals(1L, actual.getId());
            assertEquals("name", actual.getName());
            assertEquals("technicalName", actual.getTechnicalName());
            assertEquals("encrypted", actual.getAssetToken());
            assertNull(actual.getImage());
            assertNull(actual.getCategoryParameters());
            assertTrue(actual.getWidgets().isEmpty());
        }
    }

    @Test
    void shouldToCategoryWithHiddenValueParametersDto() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            CategoryParameter categoryParameter = new CategoryParameter();
            categoryParameter.setKey("key");
            categoryParameter.setValue("value");
            categoryParameter.setDataType(DataTypeEnum.TEXT);
            categoryParameter.setExport(true);
            categoryParameter.setDescription("description");

            Asset asset = new Asset();
            asset.setId(1L);

            Widget widget = new Widget();
            widget.setId(1L);

            Category category = new Category();
            category.setId(1L);
            category.setName("name");
            category.setTechnicalName("technicalName");
            category.setImage(asset);
            category.setWidgets(Collections.singleton(widget));
            category.setConfigurations(Collections.singleton(categoryParameter));

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");

            CategoryResponseDto actual = categoryMapper.toCategoryWithHiddenValueParametersDto(category);

            assertEquals(1L, actual.getId());
            assertEquals("name", actual.getName());
            assertEquals("technicalName", actual.getTechnicalName());
            assertEquals("encrypted", actual.getAssetToken());
            assertNull(actual.getImage());

            CategoryParameterResponseDto categoryParameterResponseDto = new CategoryParameterResponseDto();
            categoryParameterResponseDto.setKey("key");
            categoryParameterResponseDto.setValue("value");
            categoryParameterResponseDto.setDataType(DataTypeEnum.TEXT);
            categoryParameterResponseDto.setExport(true);
            categoryParameterResponseDto.setDescription("description");

            assertTrue(actual.getCategoryParameters().contains(categoryParameterResponseDto));
            assertTrue(actual.getWidgets().isEmpty());
        }
    }

    @Test
    void shouldToCategoryParameterDto() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            CategoryParameter categoryParameter = getCategoryParameter();

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");

            CategoryParameterResponseDto actual = categoryMapper.toCategoryParameterDto(categoryParameter);

            assertEquals("key", actual.getKey());
            assertEquals("value", actual.getValue());
            assertEquals(DataTypeEnum.TEXT, actual.getDataType());
            assertTrue(actual.isExport());
            assertEquals("description", actual.getDescription());

            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            categoryResponseDto.setId(1L);
            categoryResponseDto.setName("name");
            categoryResponseDto.setTechnicalName("technicalName");
            categoryResponseDto.setAssetToken("encrypted");

            assertEquals(categoryResponseDto, actual.getCategory());
        }
    }

    @Test
    void shouldToCategoryParameterWithHiddenValuesDto() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            CategoryParameter categoryParameter = getCategoryParameter();

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");

            CategoryParameterResponseDto actual =
                categoryMapper.toCategoryParameterWithHiddenValuesDto(categoryParameter);

            assertEquals("key", actual.getKey());
            assertEquals("value", actual.getValue());
            assertEquals(DataTypeEnum.TEXT, actual.getDataType());
            assertTrue(actual.isExport());
            assertEquals("description", actual.getDescription());
            assertNull(actual.getCategory());
        }
    }

    @NotNull
    private static Category getCategory() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        Asset asset = new Asset();
        asset.setId(1L);

        Widget widget = new Widget();
        widget.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setTechnicalName("technicalName");
        category.setImage(asset);
        category.setWidgets(Collections.singleton(widget));
        category.setConfigurations(Collections.singleton(categoryParameter));
        return category;
    }

    @NotNull
    private static CategoryParameter getCategoryParameter() {
        Asset asset = new Asset();
        asset.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setTechnicalName("technicalName");
        category.setImage(asset);

        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);
        categoryParameter.setExport(true);
        categoryParameter.setDescription("description");
        categoryParameter.setCategory(category);
        return categoryParameter;
    }
}
