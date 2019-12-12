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

import io.suricate.monitoring.model.dto.api.widget.WidgetResponseDto;
import io.suricate.monitoring.model.entity.widget.Widget;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Widget class
 */
@Mapper(
    componentModel = "spring",
    uses = {
        WidgetParamMapper.class,
        CategoryMapper.class
    }
)
public interface WidgetMapper {

    /* ************************* TO DTO ********************************************** */

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a Widget into a WidgetResponseDto
     *
     * @param widget The widget to transform
     * @return The related widget DTO
     */
    @Named("toWidgetDtoDefault")
    @Mapping(target = "imageToken", expression = "java( widget.getImage() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(widget.getImage().getId()) : null )")
    @Mapping(target = "category", qualifiedByName = "toCategoryDtoDefault")
    @Mapping(target = "repositoryId", source = "widget.repository.id")
    @Mapping(target = "params", source = "widget.widgetParams", qualifiedByName = "toWidgetParamDtoDefault")
    WidgetResponseDto toWidgetDtoDefault(Widget widget);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of widgets into a list of widgetDto
     *
     * @param widgets The list of widget to transform
     * @return The related DTOs
     */
    @Named("toWidgetDtosDefault")
    @IterableMapping(qualifiedByName = "toWidgetDtoDefault")
    List<WidgetResponseDto> toWidgetDtosDefault(List<Widget> widgets);
}
