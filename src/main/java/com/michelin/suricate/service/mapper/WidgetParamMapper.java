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

import com.michelin.suricate.model.dto.api.widget.WidgetParamResponseDto;
import com.michelin.suricate.model.entity.WidgetParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Widget param mapper.
 */
@Mapper(
    componentModel = "spring",
    uses = {
        WidgetParamValueMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class WidgetParamMapper {
    /**
     * Map a widget parameter into a widget parameter DTO.
     *
     * @param widgetParam The widget parameter to map
     * @return The widget parameter as DTO
     */
    @Named("toWidgetParameterDto")
    @Mapping(target = "values", source = "widgetParam.possibleValuesMap",
        qualifiedByName = "toWidgetParameterValuesDtos")
    public abstract WidgetParamResponseDto toWidgetParameterDto(WidgetParam widgetParam);
}
