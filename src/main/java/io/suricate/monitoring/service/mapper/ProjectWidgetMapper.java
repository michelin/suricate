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

import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetRequestDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.service.api.ProjectService;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.service.api.WidgetService;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for project widget class
 */
@Component
@Mapper(componentModel = "spring")
public abstract class ProjectWidgetMapper {

    /**
     * The project widget service
     */
    @Autowired
    protected ProjectWidgetService projectWidgetService;

    /**
     * The project service
     */
    @Autowired
    protected ProjectService projectService;

    /**
     * The widget service
     */
    @Autowired
    protected WidgetService widgetService;

    /* ************************* TO DTO ********************************************** */

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a project widget into a ProjectWidgetResponseDto
     *
     * @param projectWidget The project widget to transform
     * @return The related project widget DTO
     */
    @Named("toProjectWidgetDtoDefault")
    @Mapping(target = "widgetPosition.col", source = "projectWidget.col")
    @Mapping(target = "widgetPosition.row", source = "projectWidget.row")
    @Mapping(target = "widgetPosition.height", source = "projectWidget.height")
    @Mapping(target = "widgetPosition.width", source = "projectWidget.width")
    @Mapping(target = "instantiateHtml", expression = "java(projectWidgetService.instantiateProjectWidgetHtml(projectWidget))")
    @Mapping(target = "backendConfig", expression = "java(projectWidgetService.decryptSecretParamsIfNeeded(projectWidget.getWidget().getWidgetParams(), projectWidget.getBackendConfig()))")
    @Mapping(target = "projectToken", source = "projectWidget.project.token")
    @Mapping(target = "widgetId", source = "projectWidget.widget.id")
    public abstract ProjectWidgetResponseDto toProjectWidgetDtoDefault(ProjectWidget projectWidget);

    /* ******************************************************* */
    /*                  List Mapping                         */
    /* ******************************************************* */

    @Named("toProjectWidgetDtosDefault")
    @IterableMapping(qualifiedByName = "toProjectWidgetDtoDefault")
    public abstract List<ProjectWidgetResponseDto> toProjectWidgetDtosDefault(List<ProjectWidget> projectWidgets);

    /* ************************* TO MODEL **************************************** */

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a projectWidgetDto into a new projectwidget when we want to add a new project widget
     *
     * @param projectWidgetRequestDto The project widget to transform
     * @return The domain object
     */
    @Named("toNewProjectWidget")
    @Mapping(target = "col", source = "projectWidgetRequestDto.col")
    @Mapping(target = "row", source = "projectWidgetRequestDto.row")
    @Mapping(target = "height", source = "projectWidgetRequestDto.height")
    @Mapping(target = "width", source = "projectWidgetRequestDto.width")
    @Mapping(target = "project", expression = "java( projectService.getOneByToken(projectToken).get())")
    @Mapping(target = "widget", expression = "java( widgetService.findOne(projectWidgetRequestDto.getWidgetId()) )")
    @Mapping(target = "data", source = "projectWidgetRequestDto.data")
    public abstract ProjectWidget toNewProjectWidget(ProjectWidgetRequestDto projectWidgetRequestDto, String projectToken);
}
