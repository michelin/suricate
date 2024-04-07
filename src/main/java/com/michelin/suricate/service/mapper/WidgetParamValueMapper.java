/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michelin.suricate.service.mapper;

import com.michelin.suricate.model.dto.api.widget.WidgetParamValueResponseDto;
import com.michelin.suricate.model.entity.WidgetParamValue;
import java.util.Collection;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Widget param value mapper.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class WidgetParamValueMapper {
    /**
     * Map a widget parameter value into a widget parameter value DTO.
     *
     * @param widgetParamValue The widget parameter value to map
     * @return The widget parameter value as DTO
     */
    @Named("toWidgetParameterValueDto")
    public abstract WidgetParamValueResponseDto toWidgetParameterValueDto(WidgetParamValue widgetParamValue);

    /**
     * Map a list of widget parameter values into a list of widget parameter values DTOs.
     *
     * @param widgetParamValues The list of widget parameter value to map
     * @return The widget parameter values DTOs
     */
    @Named("toWidgetParameterValuesDtos")
    @IterableMapping(qualifiedByName = "toWidgetParameterValueDto")
    public abstract List<WidgetParamValueResponseDto> toWidgetParameterValuesDtos(
        Collection<WidgetParamValue> widgetParamValues);
}
