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

package io.suricate.monitoring.model.mapper.project;

import io.suricate.monitoring.model.dto.project.ProjectWidgetDto;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.mapper.widget.WidgetMapper;
import io.suricate.monitoring.service.api.ProjectService;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.service.api.WidgetService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for project widget class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        WidgetMapper.class,
        ProjectMapper.class
    }
)
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
     * Tranform a project widget into a ProjectWidgetDto
     *
     * @param projectWidget The project widget to transform
     * @return The related project widget DTO
     */
    @Named("toProjectWidgetDtoDefault")
    @Mappings({
        @Mapping(target = "widgetPosition.col", source = "projectWidget.col"),
        @Mapping(target = "widgetPosition.row", source = "projectWidget.row"),
        @Mapping(target = "widgetPosition.height", source = "projectWidget.height"),
        @Mapping(target = "widgetPosition.width", source = "projectWidget.width"),
        @Mapping(target = "instantiateHtml", expression = "java(projectWidgetService.instantiateProjectWidgetHtml(projectWidget))"),
        @Mapping(target = "project", qualifiedByName = "toProjectDtoWithoutProjectWidget"),
        @Mapping(target = "widget", qualifiedByName = "toWidgetDtoDefault"),
        @Mapping(target = "backendConfig", expression = "java(projectWidgetService.decryptSecretParamsIfNeeded(projectWidget.getWidget().getWidgetParams(), projectWidget.getBackendConfig()))")
    })
    public abstract ProjectWidgetDto toProjectWidgetDtoDefault(ProjectWidget projectWidget);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of project widgets into a list of projectWidgetDtos
     *
     * @param projectWidgets The list of project widget to tranform
     * @return The related list of dto object
     */
    @Named("toProjectWidgetDtosDefault")
    @IterableMapping(qualifiedByName = "toProjectWidgetDtoDefault")
    public abstract List<ProjectWidgetDto> toProjectWidgetDtosDefault(List<ProjectWidget> projectWidgets);

    /* ************************* TO MODEL **************************************** */

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a projectWidgetDto into a new projectWidget when we want to add a new project widget
     *
     * @param projectWidgetDto The project widget to transform
     * @return The domain object
     */
    @Named("toNewProjectWidget")
    @Mappings({
        @Mapping(target = "col", expression = "java(0)"),
        @Mapping(target = "row", expression = "java(0)"),
        @Mapping(target = "height", expression = "java(1)"),
        @Mapping(target = "width", expression = "java(1)"),
        @Mapping(target = "project", expression = "java( projectService.getOneById(projectId).get())"),
        @Mapping(target = "widget", expression = "java( widgetService.findOne(projectWidgetDto.getWidget().getId()) )"),
        @Mapping(target = "data", expression = "java( \"{}\" )")
    })
    public abstract ProjectWidget toNewProjectWidget(ProjectWidgetDto projectWidgetDto, Long projectId);


}
