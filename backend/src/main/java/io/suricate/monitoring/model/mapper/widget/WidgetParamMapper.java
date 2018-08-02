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

import io.suricate.monitoring.model.dto.widget.WidgetParamDto;
import io.suricate.monitoring.model.entity.widget.WidgetParam;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Widget params class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        WidgetParamValueMapper.class
    }
)
public abstract class WidgetParamMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a widgetParam into a widgetParamDto
     *
     * @param widgetParam The widgetParam to transform
     * @return The related widgetParam DTO
     */
    @Named("toWidgetParamDtoDefault")
    @Mappings({
        @Mapping(target = "values", source = "widgetParam.possibleValuesMap", qualifiedByName = "toWidgetParamValueDtosDefault")
    })
    public abstract WidgetParamDto toWidgetParamDtoDefault(WidgetParam widgetParam);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of widgetParams into a list of widgetParamDto
     *
     * @param widgetParams The list of widgetParams to transform
     * @return The related DTOs
     */
    @Named("toWidgetParamDtosDefault")
    @IterableMapping(qualifiedByName = "toWidgetParamDtoDefault")
    public abstract List<WidgetParamDto> toWidgetParamDtosDefault(List<WidgetParam> widgetParams);
}
