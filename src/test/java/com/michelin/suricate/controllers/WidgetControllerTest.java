package com.michelin.suricate.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.widget.WidgetRequestDto;
import com.michelin.suricate.model.dto.api.widget.WidgetResponseDto;
import com.michelin.suricate.model.entities.Widget;
import com.michelin.suricate.model.enums.WidgetAvailabilityEnum;
import com.michelin.suricate.services.api.WidgetService;
import com.michelin.suricate.services.mapper.WidgetMapper;
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
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class WidgetControllerTest {
    @Mock
    private WidgetService widgetService;

    @Mock
    private WidgetMapper widgetMapper;

    @InjectMocks
    private WidgetController widgetController;

    @Test
    void shouldGetWidgets() {
        WidgetResponseDto widgetResponseDto = new WidgetResponseDto();
        widgetResponseDto.setId(1L);

        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetService.getAll(any(), any()))
            .thenReturn(new PageImpl<>(Collections.singletonList(widget)));
        when(widgetMapper.toWidgetWithoutCategoryParametersDto(any()))
            .thenReturn(widgetResponseDto);

        Page<WidgetResponseDto> actual = widgetController.getWidgets("search", Pageable.unpaged());

        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).hasSize(1);
        assertThat(actual.get().collect(Collectors.toList()).get(0)).isEqualTo(widgetResponseDto);
    }

    @Test
    void shouldGetOneByIdNotFound() {
        when(widgetService.findOne(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> widgetController.getOneById(1L))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Widget '1' not found");
    }

    @Test
    void shouldGetOneById() {
        WidgetResponseDto widgetResponseDto = new WidgetResponseDto();
        widgetResponseDto.setId(1L);

        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetService.findOne(any()))
            .thenReturn(Optional.of(widget));
        when(widgetMapper.toWidgetDto(any()))
            .thenReturn(widgetResponseDto);

        ResponseEntity<WidgetResponseDto> actual = widgetController.getOneById(1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(widgetResponseDto);
    }

    @Test
    void shouldUpdateWidgetNotFound() {
        WidgetRequestDto widgetRequestDto = new WidgetRequestDto();
        widgetRequestDto.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);

        when(widgetService.updateWidget(any(), any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> widgetController.updateWidget(1L, widgetRequestDto))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Widget '1' not found");
    }

    @Test
    void shouldUpdateWidget() {
        WidgetRequestDto widgetRequestDto = new WidgetRequestDto();
        widgetRequestDto.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);

        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetService.updateWidget(any(), any()))
            .thenReturn(Optional.of(widget));

        ResponseEntity<Void> actual = widgetController.updateWidget(1L, widgetRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }
}
