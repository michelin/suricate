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

import io.suricate.monitoring.model.dto.api.widget.WidgetParamResponseDto;
import io.suricate.monitoring.model.entities.WidgetParam;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Manage the generation DTO/Model objects for Widget params class
 */
@Mapper(
    componentModel = "spring",
    uses = {
        WidgetParamValueMapper.class
    }
)
public abstract class WidgetParamMapper {

    /**
     * Map a widget parameter into a widget parameter DTO
     *
     * @param widgetParam The widget parameter to map
     * @return The widget parameter as DTO
     */
    @Named("toWidgetParameterDTO")
    @Mapping(target = "values", source = "widgetParam.possibleValuesMap", qualifiedByName = "toWidgetParameterValuesDTOs")
    public abstract WidgetParamResponseDto toWidgetParameterDTO(WidgetParam widgetParam);

    /**
     * Map a list of widget parameters into a list of widget parameters DTOs
     *
     * @param widgetParams The list of widget parameters to map
     * @return The list of widget parameters as DTOs
     */
    @Named("toWidgetParametersDTO")
    @IterableMapping(qualifiedByName = "toWidgetParameterDTO")
    public abstract List<WidgetParamResponseDto> toWidgetParametersDTO(List<WidgetParam> widgetParams);
}
