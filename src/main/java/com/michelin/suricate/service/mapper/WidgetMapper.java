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

import com.michelin.suricate.model.dto.api.widget.WidgetResponseDto;
import com.michelin.suricate.model.entity.Widget;
import java.util.Collection;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/** Widget mapper. */
@Mapper(
        componentModel = "spring",
        uses = {WidgetParamMapper.class, CategoryMapper.class},
        imports = {java.util.stream.Collectors.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class WidgetMapper {
    /**
     * Map a widget into a DTO.
     *
     * @param widget The widget to map
     * @return The widget DTO
     */
    @Named("toWidgetDto")
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "libraryTechnicalNames", ignore = true)
    @Mapping(
            target = "imageToken",
            expression = "java( widget.getImage() != null "
                    + "? com.michelin.suricate.util.IdUtils.encrypt(widget.getImage().getId()) : null )")
    @Mapping(target = "category", qualifiedByName = "toCategoryWithHiddenValueParametersDto")
    @Mapping(target = "repositoryId", source = "widget.repository.id")
    @Mapping(target = "params", source = "widget.widgetParams", qualifiedByName = "toWidgetParameterDto")
    public abstract WidgetResponseDto toWidgetDto(Widget widget);

    /**
     * Map a widget into a DTO.
     *
     * @param widget The widget to map
     * @return The widget DTO
     */
    @Named("toWidgetWithoutCategoryParametersDto")
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "libraryTechnicalNames", ignore = true)
    @Mapping(
            target = "imageToken",
            expression = "java( widget.getImage() != null "
                    + "? com.michelin.suricate.util.IdUtils.encrypt(widget.getImage().getId()) : null )")
    @Mapping(target = "category", qualifiedByName = "toCategoryWithoutParametersDto")
    @Mapping(target = "repositoryId", source = "widget.repository.id")
    @Mapping(target = "params", source = "widget.widgetParams", qualifiedByName = "toWidgetParameterDto")
    public abstract WidgetResponseDto toWidgetWithoutCategoryParametersDto(Widget widget);

    /**
     * Map a list of widgets into a list of widgets DTOs.
     *
     * @param widgets The list of widgets to map
     * @return The list of widgets as DTOs
     */
    @Named("toWidgetsDtos")
    @IterableMapping(qualifiedByName = "toWidgetDto")
    public abstract List<WidgetResponseDto> toWidgetsDtos(Collection<Widget> widgets);
}
