/*
 * Copyright 2012-2018 the original author or authors.
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

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.widget.WidgetParamValueResponseDto;
import io.suricate.monitoring.model.entities.WidgetParamValue;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Manage the generation DTO/Model objects for widgetParamValue class
 */
@Mapper(componentModel = "spring")
public abstract class WidgetParamValueMapper {

    /**
     * Map a widget parameter value into a widget parameter value DTO
     *
     * @param widgetParamValue The widget parameter value to map
     * @return The widget parameter value as DTO
     */
    @Named("toWidgetParameterValueDTO")
    public abstract WidgetParamValueResponseDto toWidgetParameterValueDTO(WidgetParamValue widgetParamValue);

    /**
     * Map a list of widget parameter values into a list of widget parameter values DTOs
     *
     * @param widgetParamValues The list of widget parameter value to map
     * @return The widget parameter values DTOs
     */
    @Named("toWidgetParameterValuesDTOs")
    @IterableMapping(qualifiedByName = "toWidgetParameterValueDTO")
    public abstract List<WidgetParamValueResponseDto> toWidgetParameterValuesDTOs(Collection<WidgetParamValue> widgetParamValues);
}
