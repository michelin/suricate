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
package com.michelin.suricate.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.CategoryParameter;
import com.michelin.suricate.model.entity.CategoryParameter_;
import com.michelin.suricate.model.entity.WidgetParam;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import com.michelin.suricate.repository.CategoryParametersRepository;
import com.michelin.suricate.service.specification.CategoryParametersSearchSpecification;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryParametersServiceTest {
    @Mock
    private CategoryParametersRepository categoryParametersRepository;

    @Mock
    private SingularAttribute<CategoryParameter, String> description;

    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private CategoryParametersService categoryParametersService;

    @Test
    void shouldGetParametersByCategoryId() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);

        when(categoryParametersRepository.findCategoryParametersByCategoryId(1L))
                .thenReturn(Optional.of(Collections.singletonList(categoryParameter)));

        Optional<List<CategoryParameter>> actual = categoryParametersService.getParametersByCategoryId(1L);

        assertTrue(actual.isPresent());
        assertEquals(1, actual.get().size());
        assertEquals(categoryParameter, actual.get().getFirst());

        verify(categoryParametersRepository).findCategoryParametersByCategoryId(1L);
    }

    @Test
    void shouldGetAll() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);

        CategoryParameter_.description = description;
        when(categoryParametersRepository.findAll(
                        any(CategoryParametersSearchSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(categoryParameter)));

        Page<CategoryParameter> actual = categoryParametersService.getAll("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertTrue(actual.getContent().contains(categoryParameter));

        verify(categoryParametersRepository)
                .findAll(
                        Mockito.<CategoryParametersSearchSpecification>argThat(
                                specification -> specification.getSearch().equals("search")
                                        && specification.getAttributes().contains(description.getName())),
                        Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldGetOneByKey() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);

        when(categoryParametersRepository.findById("key")).thenReturn(Optional.of(categoryParameter));

        Optional<CategoryParameter> actual = categoryParametersService.getOneByKey("key");

        assertTrue(actual.isPresent());
        assertEquals(categoryParameter, actual.get());

        verify(categoryParametersRepository).findById("key");
    }

    @Test
    void shouldUpdateConfigurationWhenDataTypeIsText() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);

        when(categoryParametersRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        categoryParametersService.updateConfiguration(categoryParameter, "newValue");

        assertEquals("newValue", categoryParameter.getValue());

        verify(categoryParametersRepository).save(categoryParameter);
    }

    @Test
    void shouldUpdateConfigurationWhenDataTypeIsPassword() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.PASSWORD);

        when(categoryParametersRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));
        when(stringEncryptor.encrypt("newValue")).thenReturn("encrypted");

        categoryParametersService.updateConfiguration(categoryParameter, "newValue");

        assertEquals("encrypted", categoryParameter.getValue());

        verify(categoryParametersRepository).save(categoryParameter);
    }

    @Test
    void shouldDeleteOneByKey() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);

        categoryParametersService.deleteOneByKey("key");

        verify(categoryParametersRepository).deleteById("key");
    }

    @Test
    void shouldAddCategoryConfiguration() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);

        Category category = new Category();
        category.setId(1L);
        category.setName("name");

        when(categoryParametersRepository.findById(any())).thenReturn(Optional.empty());
        when(categoryParametersRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        categoryParametersService.addOrUpdateCategoryConfiguration(Collections.singleton(categoryParameter), category);

        assertEquals(category, categoryParameter.getCategory());
        assertEquals("key", categoryParameter.getKey());
        assertEquals("value", categoryParameter.getValue());
        assertFalse(categoryParameter.isExport());

        verify(categoryParametersRepository).findById("key");
        verify(categoryParametersRepository).save(categoryParameter);
    }

    @Test
    void shouldUpdateCategoryConfiguration() {
        CategoryParameter currentCategoryParameter = new CategoryParameter();
        currentCategoryParameter.setKey("oldKey");
        currentCategoryParameter.setValue("oldValue");
        currentCategoryParameter.setExport(true);
        currentCategoryParameter.setDataType(DataTypeEnum.TEXT);

        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);

        Category category = new Category();
        category.setId(1L);
        category.setName("name");

        when(categoryParametersRepository.findById(any())).thenReturn(Optional.of(currentCategoryParameter));
        when(categoryParametersRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        categoryParametersService.addOrUpdateCategoryConfiguration(Collections.singleton(categoryParameter), category);

        assertEquals(category, categoryParameter.getCategory());
        assertEquals("key", categoryParameter.getKey());
        assertEquals("oldValue", categoryParameter.getValue());
        assertTrue(categoryParameter.isExport());

        verify(categoryParametersRepository).findById("key");
        verify(categoryParametersRepository).save(categoryParameter);
    }

    @Test
    void shouldConvertCategoryParametersToWidgetParameters() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);

        WidgetParam widgetParam =
                CategoryParametersService.convertCategoryParametersToWidgetParameters(categoryParameter);

        assertEquals("key", widgetParam.getName());
        assertEquals("value", widgetParam.getDefaultValue());
        assertEquals(DataTypeEnum.TEXT, widgetParam.getType());
        assertEquals("key", widgetParam.getDescription());
        assertTrue(widgetParam.isRequired());
    }
}
