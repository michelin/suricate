package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.widget.WidgetRequestDto;
import com.michelin.suricate.model.dto.api.widget.WidgetResponseDto;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.enumeration.WidgetAvailabilityEnum;
import com.michelin.suricate.service.api.WidgetService;
import com.michelin.suricate.service.mapper.WidgetMapper;
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

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.get().count());
        assertEquals(widgetResponseDto, actual.get().toList().getFirst());
    }

    @Test
    void shouldGetOneByIdNotFound() {
        when(widgetService.findOne(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> widgetController.getOneById(1L)
        );

        assertEquals("Widget '1' not found", exception.getMessage());
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

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(widgetResponseDto, actual.getBody());
    }

    @Test
    void shouldUpdateWidgetNotFound() {
        WidgetRequestDto widgetRequestDto = new WidgetRequestDto();
        widgetRequestDto.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);

        when(widgetService.updateWidget(any(), any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> widgetController.updateWidget(1L, widgetRequestDto)
        );

        assertEquals("Widget '1' not found", exception.getMessage());
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

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }
}
