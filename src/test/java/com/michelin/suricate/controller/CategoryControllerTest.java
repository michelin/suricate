package com.michelin.suricate.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        when(categoryService.getAll(any(), any()))
            .thenReturn(new PageImpl<>(Collections.singletonList(category)));
        when(categoryMapper.toCategoryWithoutParametersDto(any()))
            .thenReturn(categoryResponseDto);

        Page<CategoryResponseDto> actual = categoryController.getCategories("search", Pageable.unpaged());

        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).hasSize(1);
        assertThat(actual.get().toList().get(0)).isEqualTo(categoryResponseDto);
    }

    @Test
    void shouldGetWidgetByCategoryNoContent() {
        when(widgetService.getWidgetsByCategory(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryController.getWidgetByCategory(1L))
            .isInstanceOf(NoContentException.class)
            .hasMessage("No resource for the class 'Widget'");
    }

    @Test
    void shouldGetWidgetByCategory() {
        Widget widget = new Widget();
        widget.setId(1L);

        WidgetResponseDto widgetResponseDto = new WidgetResponseDto();
        widgetResponseDto.setId(1L);

        when(widgetService.getWidgetsByCategory(any()))
            .thenReturn(Optional.of(Collections.singletonList(widget)));
        when(widgetMapper.toWidgetsDtos(any()))
            .thenReturn(Collections.singletonList(widgetResponseDto));

        ResponseEntity<List<WidgetResponseDto>> actual = categoryController.getWidgetByCategory(1L);

        assertThat(actual.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody()).hasSize(1);
        assertThat(actual.getBody().get(0)).isEqualTo(widgetResponseDto);
    }
}
