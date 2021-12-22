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

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.widget.WidgetResponseDto;
import io.suricate.monitoring.model.entities.Widget;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Manage the generation DTO/Model objects for Widget class
 */
@Mapper(
    componentModel = "spring",
    uses = {
        WidgetParamMapper.class,
        CategoryMapper.class
    }
)
public abstract class WidgetMapper {

    /**
     * Map a widget into a DTO
     *
     * @param widget The widget to map
     * @return The widget DTO
     */
    @Named("toWidgetDTO")
    @Mapping(target = "imageToken", expression = "java( widget.getImage() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(widget.getImage().getId()) : null )")
    @Mapping(target = "category", qualifiedByName = "toCategoryWithHiddenValueParametersDTO")
    @Mapping(target = "repositoryId", source = "widget.repository.id")
    @Mapping(target = "params", source = "widget.widgetParams", qualifiedByName = "toWidgetParameterDTO")
    public abstract WidgetResponseDto toWidgetDTO(Widget widget);

    /**
     * Map a widget into a DTO
     *
     * @param widget The widget to map
     * @return The widget DTO
     */
    @Named("toWidgetWithoutCategoryParametersDTO")
    @Mapping(target = "imageToken", expression = "java( widget.getImage() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(widget.getImage().getId()) : null )")
    @Mapping(target = "category", qualifiedByName = "toCategoryWithoutParametersDTO")
    @Mapping(target = "repositoryId", source = "widget.repository.id")
    @Mapping(target = "params", source = "widget.widgetParams", qualifiedByName = "toWidgetParameterDTO")
    public abstract WidgetResponseDto toWidgetWithoutCategoryParametersDTO(Widget widget);

    /**
     * Map a list of widgets into a list of widgets DTOs
     *
     * @param widgets The list of widgets to map
     * @return The list of widgets as DTOs
     */
    @Named("toWidgetsDTOs")
    @IterableMapping(qualifiedByName = "toWidgetDTO")
    public abstract List<WidgetResponseDto> toWidgetsDTOs(Collection<Widget> widgets);
}
