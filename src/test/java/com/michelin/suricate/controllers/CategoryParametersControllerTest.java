package com.michelin.suricate.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.category.CategoryParameterResponseDto;
import com.michelin.suricate.model.dto.api.widgetconfiguration.WidgetConfigurationRequestDto;
import com.michelin.suricate.model.entities.CategoryParameter;
import com.michelin.suricate.services.api.CategoryParametersService;
import com.michelin.suricate.services.cache.CacheService;
import com.michelin.suricate.services.mapper.CategoryMapper;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
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

        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).hasSize(1);
        assertThat(actual.get().collect(Collectors.toList()).get(0)).isEqualTo(categoryParameterResponseDto);
    }

    @Test
    void shouldGetOneByKeyNotFound() {
        when(categoryParametersService.getOneByKey(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryParametersController.getOneByKey("key"))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("CategoryParameter 'key' not found");
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

        assertThat(actual.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody()).isEqualTo(categoryParameterResponseDto);
    }

    @Test
    void shouldUpdateOneByKeyNotFound() {
        WidgetConfigurationRequestDto widgetConfigurationRequestDto = new WidgetConfigurationRequestDto();
        widgetConfigurationRequestDto.setValue("value");

        when(categoryParametersService.getOneByKey(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryParametersController.updateOneByKey("key", widgetConfigurationRequestDto))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("CategoryParameter 'key' not found");
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

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldDeleteOneByKeyNotFound() {
        when(categoryParametersService.getOneByKey(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryParametersController.deleteOneByKey("key"))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("CategoryParameter 'key' not found");
    }

    @Test
    void shouldDeleteOneByKey() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        when(categoryParametersService.getOneByKey(any()))
            .thenReturn(Optional.of(categoryParameter));

        ResponseEntity<Void> actual = categoryParametersController.deleteOneByKey("key");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
