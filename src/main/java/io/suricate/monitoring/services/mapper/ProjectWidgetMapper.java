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

import io.suricate.monitoring.model.dto.api.export.ImportExportProjectDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetRequestDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import io.suricate.monitoring.model.entities.ProjectWidget;
import io.suricate.monitoring.services.api.*;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manage the generation DTO/Model objects for project widget class
 */
@Component
@Mapper(
        componentModel = "spring",
        imports = {
                Collection.class,
                Collectors.class
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
     * The project service
     */
    @Autowired
    protected ProjectGridService projectGridService;

    /**
     * The widget service
     */
    @Autowired
    protected WidgetService widgetService;

    /**
     * The library service
     */
    @Autowired
    protected LibraryService libraryService;

    /**
     * Map a project widget into a DTO
     *
     * @param projectWidget The project widget to map
     * @return The project widget as DTO
     */
    @Named("toProjectWidgetDTO")
    @Mapping(target = "widgetPosition.gridColumn", source = "projectWidget.gridColumn")
    @Mapping(target = "widgetPosition.gridRow", source = "projectWidget.gridRow")
    @Mapping(target = "widgetPosition.height", source = "projectWidget.height")
    @Mapping(target = "widgetPosition.width", source = "projectWidget.width")
    @Mapping(target = "instantiateHtml", expression = "java(projectWidgetService.instantiateProjectWidgetHtml(projectWidget))")
    @Mapping(target = "backendConfig", expression = "java(projectWidgetService.decryptSecretParamsIfNeeded(projectWidget.getWidget(), projectWidget.getBackendConfig()))")
    @Mapping(target = "projectToken", source = "projectWidget.projectGrid.project.token")
    @Mapping(target = "widgetId", source = "projectWidget.widget.id")
    @Mapping(target = "gridId", source = "projectWidget.projectGrid.id")
    public abstract ProjectWidgetResponseDto toProjectWidgetDTO(ProjectWidget projectWidget);

    /**
     * Map a list of project widgets into a list of DTOs
     *
     * @param projectWidgets The list of project widgets to map
     * @return The list of project widgets as DTOs
     */
    @Named("toProjectWidgetsDTOs")
    @IterableMapping(qualifiedByName = "toProjectWidgetDTO")
    public abstract List<ProjectWidgetResponseDto> toProjectWidgetsDTOs(Collection<ProjectWidget> projectWidgets);

    /**
     * Map a project widget DTO into a project widget entity
     *
     * @param projectWidgetRequestDto The project widget to map
     * @return The project widget as entity
     */
    @Named("toProjectWidgetEntity")
    @Mapping(target = "projectGrid", expression = "java( projectGridService.getOneById(gridId).get())")
    @Mapping(target = "widget", expression = "java( widgetService.findOne(projectWidgetRequestDto.getWidgetId()).get() )")
    public abstract ProjectWidget toProjectWidgetEntity(ProjectWidgetRequestDto projectWidgetRequestDto, Long gridId);

    /**
     * Map a project widget DTO into a project widget entity
     *
     * @param projectWidgetRequestDto The project widget to map
     * @return The project widget as entity
     */
    @Named("toProjectWidgetEntity")
    @Mapping(target = "data", constant = "{}")
    @Mapping(target = "gridRow", source = "projectWidgetRequestDto.widgetPosition.gridRow")
    @Mapping(target = "gridColumn", source = "projectWidgetRequestDto.widgetPosition.gridColumn")
    @Mapping(target = "width", source = "projectWidgetRequestDto.widgetPosition.width")
    @Mapping(target = "height", source = "projectWidgetRequestDto.widgetPosition.height")
    @Mapping(target = "widget", expression = "java( widgetService.findOneByTechnicalName(projectWidgetRequestDto.getWidgetTechnicalName()).get() )")
    public abstract ProjectWidget toProjectWidgetEntity(ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto projectWidgetRequestDto);

    /**
     * Map a project widget into a DTO for export
     * @param projectWidget The project widget to map
     * @return The project widget as DTO
     */
    @Named("toImportExportProjectWidgetDTO")
    @Mapping(target = "widgetTechnicalName", source = "projectWidget.widget.technicalName")
    @Mapping(target = "backendConfig", expression = "java(projectWidgetService.decryptSecretParamsIfNeeded(projectWidget.getWidget(), projectWidget.getBackendConfig()))")
    @Mapping(target = "widgetPosition.gridColumn", source = "projectWidget.gridColumn")
    @Mapping(target = "widgetPosition.gridRow", source = "projectWidget.gridRow")
    @Mapping(target = "widgetPosition.height", source = "projectWidget.height")
    @Mapping(target = "widgetPosition.width", source = "projectWidget.width")
    public abstract ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto toImportExportProjectWidgetDTO(ProjectWidget projectWidget);
}
