package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.widget.WidgetParamResponseDto;
import com.michelin.suricate.model.dto.api.widget.WidgetParamValueResponseDto;
import com.michelin.suricate.model.entities.WidgetParam;
import com.michelin.suricate.model.entities.WidgetParamValue;
import com.michelin.suricate.model.enums.DataTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WidgetParamMapperTest {
    @Mock
    protected WidgetParamValueMapper widgetParamValueMapper;

    @InjectMocks
    private WidgetParamMapperImpl widgetParamMapper;

    @Test
    void shouldToWidgetParameterDTO() {
        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);

        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setName("name");
        widgetParam.setDescription("description");
        widgetParam.setDefaultValue("defaultValue");
        widgetParam.setType(DataTypeEnum.TEXT);
        widgetParam.setAcceptFileRegex("regex");
        widgetParam.setUsageExample("usageExample");
        widgetParam.setUsageTooltip("tooltip");
        widgetParam.setRequired(true);
        widgetParam.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        WidgetParamValueResponseDto widgetParamValueResponseDto = new WidgetParamValueResponseDto();
        widgetParamValueResponseDto.setValue("value");
        widgetParamValueResponseDto.setJsKey("key");

        when(widgetParamValueMapper.toWidgetParameterValuesDTOs(any()))
                .thenReturn(Collections.singletonList(widgetParamValueResponseDto));

        WidgetParamResponseDto actual = widgetParamMapper.toWidgetParameterDTO(widgetParam);

        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getDescription()).isEqualTo("description");
        assertThat(actual.getDefaultValue()).isEqualTo("defaultValue");
        assertThat(actual.getType()).isEqualTo(DataTypeEnum.TEXT);
        assertThat(actual.getAcceptFileRegex()).isEqualTo("regex");
        assertThat(actual.getUsageExample()).isEqualTo("usageExample");
        assertThat(actual.getUsageTooltip()).isEqualTo("tooltip");
        assertThat(actual.isRequired()).isTrue();
        assertThat(actual.getValues().get(0)).isEqualTo(widgetParamValueResponseDto);
    }
}
