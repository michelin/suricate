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

import com.michelin.suricate.model.dto.api.export.ImportExportProjectDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetRequestDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.service.api.ProjectGridService;
import com.michelin.suricate.service.api.ProjectWidgetService;
import com.michelin.suricate.service.api.WidgetService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Project widget mapper.
 */
@Component
@Mapper(componentModel = "spring",
    imports = {Collection.class, Collectors.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProjectWidgetMapper {
    @Autowired
    protected ProjectWidgetService projectWidgetService;

    @Autowired
    protected ProjectGridService projectGridService;

    @Autowired
    protected WidgetService widgetService;

    /**
     * Map a project widget into a DTO.
     *
     * @param projectWidget The project widget to map
     * @return The project widget as DTO
     */
    @Named("toProjectWidgetDto")
    @Mapping(target = "widgetPosition.gridColumn", source = "projectWidget.gridColumn")
    @Mapping(target = "widgetPosition.gridRow", source = "projectWidget.gridRow")
    @Mapping(target = "widgetPosition.height", source = "projectWidget.height")
    @Mapping(target = "widgetPosition.width", source = "projectWidget.width")
    @Mapping(target = "instantiateHtml", expression = "java("
        + "projectWidgetService.instantiateProjectWidgetHtml(projectWidget))")
    @Mapping(target = "backendConfig", expression = "java(projectWidgetService.decryptSecretParamsIfNeeded("
        + "projectWidget.getWidget(), projectWidget.getBackendConfig()))")
    @Mapping(target = "projectToken", source = "projectWidget.projectGrid.project.token")
    @Mapping(target = "widgetId", source = "projectWidget.widget.id")
    @Mapping(target = "gridId", source = "projectWidget.projectGrid.id")
    public abstract ProjectWidgetResponseDto toProjectWidgetDto(ProjectWidget projectWidget);

    /**
     * Map a list of project widgets into a list of DTOs.
     *
     * @param projectWidgets The list of project widgets to map
     * @return The list of project widgets as DTOs
     */
    @Named("toProjectWidgetsDtos")
    @IterableMapping(qualifiedByName = "toProjectWidgetDto")
    public abstract List<ProjectWidgetResponseDto> toProjectWidgetsDtos(Collection<ProjectWidget> projectWidgets);

    /**
     * Map a project widget DTO into a project widget entity.
     *
     * @param projectWidgetRequestDto The project widget to map
     * @return The project widget as entity
     */
    @Named("toProjectWidgetEntity")
    @Mapping(target = "projectGrid", expression = "java( projectGridService.getOneById(gridId).get())")
    @Mapping(target = "widget", expression = "java( widgetService.findOne("
        + "projectWidgetRequestDto.getWidgetId()).get() )")
    public abstract ProjectWidget toProjectWidgetEntity(ProjectWidgetRequestDto projectWidgetRequestDto, Long gridId);

    /**
     * Map a project widget DTO into a project widget entity.
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
    @Mapping(target = "widget", expression = "java( widgetService.findOneByTechnicalName("
        + "projectWidgetRequestDto.getWidgetTechnicalName()).get() )")
    public abstract ProjectWidget toProjectWidgetEntity(
        ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto projectWidgetRequestDto);

    /**
     * Map a project widget into a DTO for export.
     *
     * @param projectWidget The project widget to map
     * @return The project widget as DTO
     */
    @Named("toImportExportProjectWidgetDto")
    @Mapping(target = "widgetTechnicalName", source = "projectWidget.widget.technicalName")
    @Mapping(target = "backendConfig", expression = "java(projectWidgetService.decryptSecretParamsIfNeeded("
        + "projectWidget.getWidget(), projectWidget.getBackendConfig()))")
    @Mapping(target = "widgetPosition.gridColumn", source = "projectWidget.gridColumn")
    @Mapping(target = "widgetPosition.gridRow", source = "projectWidget.gridRow")
    @Mapping(target = "widgetPosition.height", source = "projectWidget.height")
    @Mapping(target = "widgetPosition.width", source = "projectWidget.width")
    public abstract ImportExportProjectDto.ImportExportProjectGridDto
        .ImportExportProjectWidgetDto toImportExportProjectWidgetDto(ProjectWidget projectWidget);
}
