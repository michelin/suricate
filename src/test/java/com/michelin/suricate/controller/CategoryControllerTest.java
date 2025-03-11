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
package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.category.CategoryResponseDto;
import com.michelin.suricate.model.dto.api.widget.WidgetResponseDto;
import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.service.api.CategoryService;
import com.michelin.suricate.service.api.WidgetService;
import com.michelin.suricate.service.mapper.CategoryMapper;
import com.michelin.suricate.service.mapper.WidgetMapper;
import com.michelin.suricate.util.exception.NoContentException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private WidgetService widgetService;

    @Mock
    private WidgetMapper widgetMapper;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void shouldGetCategories() {
        Category category = new Category();
        category.setId(1L);
        category.setName("name");

        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(1L);

        when(categoryService.getAll(any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(category)));
        when(categoryMapper.toCategoryWithoutParametersDto(any())).thenReturn(categoryResponseDto);

        Page<CategoryResponseDto> actual = categoryController.getCategories("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.get().count());
        assertEquals(categoryResponseDto, actual.get().toList().getFirst());
    }

    @Test
    void shouldGetWidgetByCategoryNoContent() {
        when(widgetService.getWidgetsByCategory(any())).thenReturn(Optional.empty());

        NoContentException exception =
                assertThrows(NoContentException.class, () -> categoryController.getWidgetByCategory(1L));

        assertEquals("No resource for the class 'Widget'", exception.getMessage());
    }

    @Test
    void shouldGetWidgetByCategory() {
        Widget widget = new Widget();
        widget.setId(1L);

        WidgetResponseDto widgetResponseDto = new WidgetResponseDto();
        widgetResponseDto.setId(1L);

        when(widgetService.getWidgetsByCategory(any())).thenReturn(Optional.of(Collections.singletonList(widget)));
        when(widgetMapper.toWidgetsDtos(any())).thenReturn(Collections.singletonList(widgetResponseDto));

        ResponseEntity<List<WidgetResponseDto>> actual = categoryController.getWidgetByCategory(1L);

        assertEquals(MediaType.APPLICATION_JSON, actual.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(1, actual.getBody().size());
        assertEquals(widgetResponseDto, actual.getBody().getFirst());
    }
}
