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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.CategoryParameter;
import com.michelin.suricate.model.entity.Category_;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.entity.WidgetParam;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import com.michelin.suricate.repository.CategoryRepository;
import com.michelin.suricate.service.specification.CategorySearchSpecification;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
class CategoryServiceTest {
    @Mock
    private AssetService assetService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryParametersService categoryParametersService;

    @Mock
    private SingularAttribute<Category, String> name;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldGetAll() {
        Category category = new Category();
        category.setId(1L);
        category.setName("name");

        Category_.name = name;
        when(categoryRepository.findAll(any(CategorySearchSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(category)));

        Page<Category> actual = categoryService.getAll("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.get().count());

        verify(categoryRepository)
                .findAll(
                        Mockito.<CategorySearchSpecification>argThat(
                                specification -> specification.getSearch().equals("search")
                                        && specification.getAttributes().contains(name.getName())),
                        Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldFindByTechnicalName() {
        Category category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setTechnicalName("technicalName");

        when(categoryRepository.findByTechnicalName("technicalName")).thenReturn(category);

        Category actual = categoryRepository.findByTechnicalName("technicalName");

        assertNotNull(actual);
        assertEquals(category, actual);

        verify(categoryRepository).findByTechnicalName("technicalName");
    }

    @Test
    void shouldAddCategoryWhenNull() {
        categoryService.addOrUpdateCategory(null);

        verify(assetService, never()).save(any());
        verify(categoryRepository, never()).save(any());
        verify(categoryParametersService, never()).deleteOneByKey(any());
        verify(categoryParametersService, never()).addOrUpdateCategoryConfiguration(any(), any());
    }

    @Test
    void shouldAddCategory() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        Asset asset = new Asset();
        asset.setId(1L);

        Category category = new Category();
        category.setName("name");
        category.setTechnicalName("technicalName");
        category.setImage(asset);
        category.setConfigurations(Collections.singleton(categoryParameter));

        when(categoryRepository.findByTechnicalName("technicalName")).thenReturn(null);

        categoryService.addOrUpdateCategory(category);

        verify(assetService).save(asset);
        verify(categoryRepository).save(category);
        verify(categoryParametersService, never()).deleteOneByKey("key");
        verify(categoryParametersService)
                .addOrUpdateCategoryConfiguration(Collections.singleton(categoryParameter), category);
    }

    @Test
    void shouldAddCategoryNoAssetNoConfig() {
        Category category = new Category();
        category.setName("name");
        category.setTechnicalName("technicalName");

        when(categoryRepository.findByTechnicalName("technicalName")).thenReturn(null);

        categoryService.addOrUpdateCategory(category);

        verify(assetService, never()).save(any());
        verify(categoryRepository).save(category);
        verify(categoryParametersService, never()).deleteOneByKey(any());
        verify(categoryParametersService, never()).addOrUpdateCategoryConfiguration(any(), any());
    }

    @Test
    void shouldUpdateCategory() {
        Asset oldAsset = new Asset();
        oldAsset.setId(1L);

        CategoryParameter oldCategoryParameter = new CategoryParameter();
        oldCategoryParameter.setKey("oldKey");

        Category oldCategory = new Category();
        oldCategory.setId(2L);
        oldCategory.setName("oldName");
        oldCategory.setTechnicalName("oldTechnicalName");
        oldCategory.setImage(oldAsset);
        oldCategory.setConfigurations(Collections.singleton(oldCategoryParameter));

        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        Asset asset = new Asset();
        asset.setId(1L);

        Category category = new Category();
        category.setName("name");
        category.setTechnicalName("technicalName");
        category.setImage(asset);
        category.setConfigurations(Collections.singleton(categoryParameter));

        when(categoryRepository.findByTechnicalName("technicalName")).thenReturn(oldCategory);

        categoryService.addOrUpdateCategory(category);

        assertEquals(2L, category.getId());
        assertEquals(1L, category.getImage().getId());

        verify(assetService).save(asset);
        verify(categoryRepository).save(category);
        verify(categoryParametersService).deleteOneByKey("oldKey");
        verify(categoryParametersService)
                .addOrUpdateCategoryConfiguration(Collections.singleton(categoryParameter), category);
    }

    @Test
    void shouldGetCategoryParametersByWidget() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);

        Category category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setTechnicalName("technicalName");

        Widget widget = new Widget();
        widget.setCategory(category);

        when(categoryParametersService.getParametersByCategoryId(any()))
                .thenReturn(Optional.of(Collections.singletonList(categoryParameter)));

        List<WidgetParam> actual = categoryService.getCategoryParametersByWidget(widget);

        assertEquals(1, actual.size());
        assertEquals("key", actual.getFirst().getName());
        assertEquals("value", actual.getFirst().getDefaultValue());
        assertEquals(DataTypeEnum.TEXT, actual.getFirst().getType());
        assertEquals("key", actual.getFirst().getDescription());
        assertTrue(actual.getFirst().isRequired());
    }
}
