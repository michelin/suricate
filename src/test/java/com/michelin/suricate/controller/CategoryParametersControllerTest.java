package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.category.CategoryParameterResponseDto;
import com.michelin.suricate.model.dto.api.widget.WidgetConfigurationRequestDto;
import com.michelin.suricate.model.entity.CategoryParameter;
import com.michelin.suricate.service.api.CategoryParametersService;
import com.michelin.suricate.service.cache.CacheService;
import com.michelin.suricate.service.mapper.CategoryMapper;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import java.util.Collections;
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
class CategoryParametersControllerTest {
    @Mock
    private CategoryParametersService categoryParametersService;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CategoryParametersController categoryParametersController;

    @Test
    void shouldGetAll() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        CategoryParameterResponseDto categoryParameterResponseDto = new CategoryParameterResponseDto();
        categoryParameterResponseDto.setKey("key");

        when(categoryParametersService.getAll(any(), any()))
            .thenReturn(new PageImpl<>(Collections.singletonList(categoryParameter)));
        when(categoryMapper.toCategoryParameterDto(any()))
            .thenReturn(categoryParameterResponseDto);

        Page<CategoryParameterResponseDto> actual = categoryParametersController.getAll("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.get().count());
        assertEquals(categoryParameterResponseDto, actual.get().toList().getFirst());
    }

    @Test
    void shouldGetOneByKeyNotFound() {
        when(categoryParametersService.getOneByKey(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> categoryParametersController.getOneByKey("key")
        );

        assertEquals("CategoryParameter 'key' not found", exception.getMessage());
    }

    @Test
    void shouldGetOneByKey() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        CategoryParameterResponseDto categoryParameterResponseDto = new CategoryParameterResponseDto();
        categoryParameterResponseDto.setKey("key");

        when(categoryParametersService.getOneByKey(any()))
            .thenReturn(Optional.of(categoryParameter));
        when(categoryMapper.toCategoryParameterDto(any()))
            .thenReturn(categoryParameterResponseDto);

        ResponseEntity<CategoryParameterResponseDto> actual = categoryParametersController.getOneByKey("key");

        assertEquals(MediaType.APPLICATION_JSON, actual.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(categoryParameterResponseDto, actual.getBody());
    }

    @Test
    void shouldUpdateOneByKeyNotFound() {
        WidgetConfigurationRequestDto widgetConfigurationRequestDto = new WidgetConfigurationRequestDto();
        widgetConfigurationRequestDto.setValue("value");

        when(categoryParametersService.getOneByKey(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> categoryParametersController.updateOneByKey("key", widgetConfigurationRequestDto)
        );

        assertEquals("CategoryParameter 'key' not found", exception.getMessage());
    }

    @Test
    void shouldUpdateOneByKey() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        WidgetConfigurationRequestDto widgetConfigurationRequestDto = new WidgetConfigurationRequestDto();
        widgetConfigurationRequestDto.setValue("value");

        when(categoryParametersService.getOneByKey(any()))
            .thenReturn(Optional.of(categoryParameter));

        ResponseEntity<Void> actual = categoryParametersController.updateOneByKey("key", widgetConfigurationRequestDto);

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }

    @Test
    void shouldDeleteOneByKeyNotFound() {
        when(categoryParametersService.getOneByKey(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> categoryParametersController.deleteOneByKey("key")
        );

        assertEquals("CategoryParameter 'key' not found", exception.getMessage());
    }

    @Test
    void shouldDeleteOneByKey() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        when(categoryParametersService.getOneByKey(any()))
            .thenReturn(Optional.of(categoryParameter));

        ResponseEntity<Void> actual = categoryParametersController.deleteOneByKey("key");

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }
}
