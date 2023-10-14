package com.michelin.suricate.services.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.model.dto.api.widget.WidgetParamValueResponseDto;
import com.michelin.suricate.model.entities.WidgetParamValue;
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
    void shouldtoWidgetParameterValueDto() {
        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);
        widgetParamValue.setJsKey("key");
        widgetParamValue.setValue("value");

        WidgetParamValueResponseDto actual = widgetParamValueMapper.toWidgetParameterValueDto(widgetParamValue);

        assertThat(actual.getJsKey()).isEqualTo("key");
        assertThat(actual.getValue()).isEqualTo("value");
    }

    @Test
    void shouldtoWidgetParameterValuesDtos() {
        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);
        widgetParamValue.setJsKey("key");
        widgetParamValue.setValue("value");

        List<WidgetParamValueResponseDto> actual =
            widgetParamValueMapper.toWidgetParameterValuesDtos(Collections.singleton(widgetParamValue));

        assertThat(actual.get(0).getJsKey()).isEqualTo("key");
        assertThat(actual.get(0).getValue()).isEqualTo("value");
    }
}
