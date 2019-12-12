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

package io.suricate.monitoring.service.mapper;

import io.suricate.monitoring.model.dto.api.widget.WidgetParamResponseDto;
import io.suricate.monitoring.model.entity.widget.WidgetParam;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Widget params class
 */
@Mapper(
    componentModel = "spring",
    uses = {
        WidgetParamValueMapper.class
    }
)
public interface WidgetParamMapper {

    /* ************************* TO DTO ********************************************** */

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
    @Mapping(target = "values", source = "widgetParam.possibleValuesMap", qualifiedByName = "toWidgetParamValueDtosDefault")
    WidgetParamResponseDto toWidgetParamDtoDefault(WidgetParam widgetParam);

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
    List<WidgetParamResponseDto> toWidgetParamDtosDefault(List<WidgetParam> widgetParams);
}
