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
package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.widget.WidgetParamResponseDto;
import com.michelin.suricate.model.dto.api.widget.WidgetParamValueResponseDto;
import com.michelin.suricate.model.entity.WidgetParam;
import com.michelin.suricate.model.entity.WidgetParamValue;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WidgetParamMapperTest {
    @Mock
    protected WidgetParamValueMapper widgetParamValueMapper;

    @InjectMocks
    private WidgetParamMapperImpl widgetParamMapper;

    @Test
    void shouldMapToWidgetParameterDto() {
        WidgetParamValueResponseDto widgetParamValueResponseDto = new WidgetParamValueResponseDto();
        widgetParamValueResponseDto.setValue("value");
        widgetParamValueResponseDto.setJsKey("key");

        when(widgetParamValueMapper.toWidgetParameterValuesDtos(any()))
                .thenReturn(Collections.singletonList(widgetParamValueResponseDto));

        WidgetParamResponseDto actual = widgetParamMapper.toWidgetParameterDto(getWidgetParam());

        assertEquals("name", actual.getName());
        assertEquals("description", actual.getDescription());
        assertEquals("defaultValue", actual.getDefaultValue());
        assertEquals(DataTypeEnum.TEXT, actual.getType());
        assertEquals("regex", actual.getAcceptFileRegex());
        assertEquals("usageExample", actual.getUsageExample());
        assertEquals("tooltip", actual.getUsageTooltip());
        assertTrue(actual.isRequired());
        assertEquals(widgetParamValueResponseDto, actual.getValues().getFirst());
    }

    @NotNull private static WidgetParam getWidgetParam() {
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
        return widgetParam;
    }
}
