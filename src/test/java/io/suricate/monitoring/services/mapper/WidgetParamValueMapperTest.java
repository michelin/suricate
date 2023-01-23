package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.widget.WidgetParamValueResponseDto;
import io.suricate.monitoring.model.entities.WidgetParamValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WidgetParamValueMapperTest {
    @InjectMocks
    private WidgetParamValueMapperImpl widgetParamValueMapper;

    @Test
    void shouldToWidgetParameterValueDTO() {
        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);
        widgetParamValue.setJsKey("key");
        widgetParamValue.setValue("value");

        WidgetParamValueResponseDto actual = widgetParamValueMapper.toWidgetParameterValueDTO(widgetParamValue);

        assertThat(actual.getJsKey()).isEqualTo("key");
        assertThat(actual.getValue()).isEqualTo("value");
    }

    @Test
    void shouldToWidgetParameterValuesDTOs() {
        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);
        widgetParamValue.setJsKey("key");
        widgetParamValue.setValue("value");

        List<WidgetParamValueResponseDto> actual = widgetParamValueMapper.toWidgetParameterValuesDTOs(Collections.singleton(widgetParamValue));

        assertThat(actual.get(0).getJsKey()).isEqualTo("key");
        assertThat(actual.get(0).getValue()).isEqualTo("value");
    }
}
