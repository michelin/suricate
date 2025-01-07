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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.export.ImportExportProjectDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetPositionResponseDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetRequestDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.enumeration.WidgetStateEnum;
import com.michelin.suricate.service.api.ProjectGridService;
import com.michelin.suricate.service.api.ProjectWidgetService;
import com.michelin.suricate.service.api.WidgetService;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectWidgetMapperTest {
    @Mock
    protected ProjectWidgetService projectWidgetService;

    @Mock
    protected ProjectGridService projectGridService;

    @Mock
    protected WidgetService widgetService;

    @InjectMocks
    private ProjectWidgetMapperImpl projectWidgetMapper;

    @Test
    void shouldToProjectWidgetDto() {
        Project project = new Project();
        project.setToken("token");

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);
        projectWidget.setProjectGrid(projectGrid);
        projectWidget.setWidth(1);
        projectWidget.setHeight(1);
        projectWidget.setBackendConfig("backendConfig");
        projectWidget.setGridRow(1);
        projectWidget.setGridColumn(1);
        projectWidget.setData("data");
        projectWidget.setCustomStyle("style");
        projectWidget.setLastExecutionDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        projectWidget.setLastSuccessDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        projectWidget.setState(WidgetStateEnum.RUNNING);
        projectWidget.setLog("log");

        when(projectWidgetService.decryptSecretParamsIfNeeded(any(), any()))
            .thenReturn("backendConfig");
        when(projectWidgetService.instantiateProjectWidgetHtml(any()))
            .thenReturn("html");

        ProjectWidgetResponseDto actual = projectWidgetMapper.toProjectWidgetDto(projectWidget);

        assertEquals(1L, actual.getId());
        assertEquals(1L, actual.getWidgetId());
        assertEquals("backendConfig", actual.getBackendConfig());
        assertEquals("data", actual.getData());
        assertEquals("style", actual.getCustomStyle());
        assertEquals("html", actual.getInstantiateHtml());
        assertEquals(1, actual.getGridId());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getLastExecutionDate());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getLastSuccessDate());
        assertEquals("token", actual.getProjectToken());
        assertEquals("log", actual.getLog());
        assertEquals(1, actual.getWidgetPosition().getWidth());
        assertEquals(1, actual.getWidgetPosition().getHeight());
        assertEquals(1, actual.getWidgetPosition().getGridRow());
        assertEquals(1, actual.getWidgetPosition().getGridColumn());
        assertNull(actual.getWidgetTechnicalName());
    }

    @Test
    void shouldToProjectWidgetsDtos() {
        Project project = new Project();
        project.setToken("token");

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);
        projectWidget.setProjectGrid(projectGrid);
        projectWidget.setWidth(1);
        projectWidget.setHeight(1);
        projectWidget.setBackendConfig("backendConfig");
        projectWidget.setGridRow(1);
        projectWidget.setGridColumn(1);
        projectWidget.setData("data");
        projectWidget.setCustomStyle("style");
        projectWidget.setLastExecutionDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        projectWidget.setLastSuccessDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        projectWidget.setState(WidgetStateEnum.RUNNING);
        projectWidget.setLog("log");

        when(projectWidgetService.decryptSecretParamsIfNeeded(any(), any()))
            .thenReturn("backendConfig");
        when(projectWidgetService.instantiateProjectWidgetHtml(any()))
            .thenReturn("html");

        List<ProjectWidgetResponseDto> actual =
            projectWidgetMapper.toProjectWidgetsDtos(Collections.singleton(projectWidget));

        assertEquals(1L, actual.getFirst().getId());
        assertEquals(1L, actual.getFirst().getWidgetId());
        assertEquals("backendConfig", actual.getFirst().getBackendConfig());
        assertEquals("data", actual.getFirst().getData());
        assertEquals("style", actual.getFirst().getCustomStyle());
        assertEquals("html", actual.getFirst().getInstantiateHtml());
        assertEquals(1, actual.getFirst().getGridId());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getFirst().getLastExecutionDate());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getFirst().getLastSuccessDate());
        assertEquals("token", actual.getFirst().getProjectToken());
        assertEquals("log", actual.getFirst().getLog());
        assertEquals(1, actual.getFirst().getWidgetPosition().getWidth());
        assertEquals(1, actual.getFirst().getWidgetPosition().getHeight());
        assertEquals(1, actual.getFirst().getWidgetPosition().getGridRow());
        assertEquals(1, actual.getFirst().getWidgetPosition().getGridColumn());
        assertNull(actual.getFirst().getWidgetTechnicalName());
    }

    @Test
    void shouldToProjectWidgetEntity() {
        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);
        projectWidgetRequestDto.setBackendConfig("backendConfig");
        projectWidgetRequestDto.setData("data");
        projectWidgetRequestDto.setHeight(1);
        projectWidgetRequestDto.setWidth(1);
        projectWidgetRequestDto.setGridColumn(1);
        projectWidgetRequestDto.setGridRow(1);
        projectWidgetRequestDto.setCustomStyle("style");

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        Widget widget = new Widget();
        widget.setId(1L);

        when(projectGridService.getOneById(any()))
            .thenReturn(Optional.of(projectGrid));
        when(widgetService.findOne(any()))
            .thenReturn(Optional.of(widget));

        ProjectWidget actual = projectWidgetMapper.toProjectWidgetEntity(projectWidgetRequestDto, 1L);

        assertNull(actual.getId());
        assertEquals("backendConfig", actual.getBackendConfig());
        assertEquals("data", actual.getData());
        assertEquals(1, actual.getGridColumn());
        assertEquals(1, actual.getGridRow());
        assertEquals(1, actual.getHeight());
        assertEquals(1, actual.getWidth());
        assertEquals("style", actual.getCustomStyle());
        assertEquals(projectGrid, actual.getProjectGrid());
        assertEquals(widget, actual.getWidget());
    }

    @Test
    void shouldToProjectWidgetEntityImportExportProjectWidgetDto() {
        ProjectWidgetPositionResponseDto projectWidgetPositionResponseDto = new ProjectWidgetPositionResponseDto();
        projectWidgetPositionResponseDto.setGridColumn(1);
        projectWidgetPositionResponseDto.setGridRow(1);
        projectWidgetPositionResponseDto.setWidth(1);
        projectWidgetPositionResponseDto.setHeight(1);

        ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto importExportProjectWidgetDto =
            new ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto();
        importExportProjectWidgetDto.setId(1L);
        importExportProjectWidgetDto.setBackendConfig("backendConfig");
        importExportProjectWidgetDto.setWidgetPosition(projectWidgetPositionResponseDto);

        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetService.findOneByTechnicalName(any()))
            .thenReturn(Optional.of(widget));

        ProjectWidget actual = projectWidgetMapper.toProjectWidgetEntity(importExportProjectWidgetDto);

        assertEquals(1L, actual.getId());
        assertEquals("backendConfig", actual.getBackendConfig());
        assertEquals("{}", actual.getData());
        assertEquals(1, actual.getGridColumn());
        assertEquals(1, actual.getGridRow());
        assertEquals(1, actual.getHeight());
        assertEquals(1, actual.getWidth());
        assertNull(actual.getCustomStyle());
        assertNull(actual.getProjectGrid());
        assertEquals(widget, actual.getWidget());
    }

    @Test
    void shouldToImportExportProjectWidgetDto() {
        Widget widget = new Widget();
        widget.setId(1L);
        widget.setTechnicalName("technicalName");

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);
        projectWidget.setWidth(1);
        projectWidget.setHeight(1);
        projectWidget.setBackendConfig("backendConfig");
        projectWidget.setGridRow(1);
        projectWidget.setGridColumn(1);
        projectWidget.setData("data");
        projectWidget.setCustomStyle("style");
        projectWidget.setLastExecutionDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        projectWidget.setLastSuccessDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        projectWidget.setState(WidgetStateEnum.RUNNING);
        projectWidget.setLog("log");

        when(projectWidgetService.decryptSecretParamsIfNeeded(any(), any()))
            .thenReturn("backendConfig");

        ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto actual =
            projectWidgetMapper.toImportExportProjectWidgetDto(projectWidget);

        assertEquals(1L, actual.getId());
        assertEquals("backendConfig", actual.getBackendConfig());
        assertEquals("technicalName", actual.getWidgetTechnicalName());
        assertEquals(1, actual.getWidgetPosition().getWidth());
        assertEquals(1, actual.getWidgetPosition().getHeight());
        assertEquals(1, actual.getWidgetPosition().getGridRow());
        assertEquals(1, actual.getWidgetPosition().getGridColumn());
    }
}
