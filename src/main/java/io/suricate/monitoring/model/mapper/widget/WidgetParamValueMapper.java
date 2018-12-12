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

package io.suricate.monitoring.model.mapper.widget;

import io.suricate.monitoring.model.dto.api.widget.WidgetParamValueResponseDto;
import io.suricate.monitoring.model.entity.widget.WidgetParamValue;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for widgetParamValue class
 */
@Component
@Mapper(
    componentModel = "spring"
)
public abstract class WidgetParamValueMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a widgetParamValue into a widgetParamValueDto
     *
     * @param widgetParamValue The widgetParamValue to transform
     * @return The related widgetParamValue DTO
     */
    @Named("toWidgetParamValueDtoDefault")
    public abstract WidgetParamValueResponseDto toWidgetParamValueDtoDefault(WidgetParamValue widgetParamValue);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of widgetParamValues into a list of widgetParamValueDto
     *
     * @param widgetParamValues The list of widgetParamValues to transform
     * @return The related DTOs
     */
    @Named("toWidgetParamValueDtosDefault")
    @IterableMapping(qualifiedByName = "toWidgetParamValueDtoDefault")
    public abstract List<WidgetParamValueResponseDto> toWidgetParamValueDtosDefault(List<WidgetParamValue> widgetParamValues);
}
