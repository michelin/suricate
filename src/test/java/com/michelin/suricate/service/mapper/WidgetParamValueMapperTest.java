package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.michelin.suricate.model.dto.api.widget.WidgetParamValueResponseDto;
import com.michelin.suricate.model.entity.WidgetParamValue;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WidgetParamValueMapperTest {
    @InjectMocks
    private WidgetParamValueMapperImpl widgetParamValueMapper;

    @Test
    void shouldConvertToWidgetParameterValueDto() {
        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);
        widgetParamValue.setJsKey("key");
        widgetParamValue.setValue("value");

        WidgetParamValueResponseDto actual = widgetParamValueMapper.toWidgetParameterValueDto(widgetParamValue);

        assertEquals("key", actual.getJsKey());
        assertEquals("value", actual.getValue());
    }

    @Test
    void shouldConvertToWidgetParameterValuesDtos() {
        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);
        widgetParamValue.setJsKey("key");
        widgetParamValue.setValue("value");

        List<WidgetParamValueResponseDto> actual =
            widgetParamValueMapper.toWidgetParameterValuesDtos(Collections.singleton(widgetParamValue));

        assertEquals("key", actual.getFirst().getJsKey());
        assertEquals("value", actual.getFirst().getValue());
    }
}
