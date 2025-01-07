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
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridRequestDto;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridResponseDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetPositionResponseDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.entity.Widget;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectGridMapperTest {
    @Mock
    private ProjectWidgetMapper projectWidgetMapper;

    @InjectMocks
    private ProjectGridMapperImpl projectGridMapper;

    @Test
    void shouldToProjectGridDto() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setTime(10);

        ProjectGridResponseDto actual = projectGridMapper.toProjectGridDto(projectGrid);

        assertEquals(1L, actual.getId());
        assertEquals(10, actual.getTime());
    }

    @Test
    void shouldToProjectGridEntity() {
        Project project = new Project();
        project.setId(1L);
        project.setCreatedBy("createdBy");
        project.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        project.setLastModifiedBy("lastModifiedBy");
        project.setLastModifiedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        ProjectGrid actual = projectGridMapper.toProjectGridEntity(project);

        assertEquals(60, actual.getTime());
        assertEquals(project, actual.getProject());
        assertEquals("createdBy", actual.getCreatedBy());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getCreatedDate());
        assertEquals("lastModifiedBy", actual.getLastModifiedBy());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getLastModifiedDate());
    }

    @Test
    void shouldToImportExportProjectGridDto() {
        Widget widget = new Widget();
        widget.setId(1L);
        widget.setTechnicalName("technicalName");

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setTime(10);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        ProjectWidgetPositionResponseDto projectWidgetPositionResponseDto = new ProjectWidgetPositionResponseDto();
        projectWidgetPositionResponseDto.setGridColumn(1);
        projectWidgetPositionResponseDto.setGridRow(1);
        projectWidgetPositionResponseDto.setHeight(1);
        projectWidgetPositionResponseDto.setWidth(1);

        ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto importExportProjectWidgetDto =
            new ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto();
        importExportProjectWidgetDto.setWidgetPosition(projectWidgetPositionResponseDto);
        importExportProjectWidgetDto.setWidgetTechnicalName("technicalName");
        importExportProjectWidgetDto.setBackendConfig("key=value");

        when(projectWidgetMapper.toImportExportProjectWidgetDto(any()))
            .thenReturn(importExportProjectWidgetDto);

        ImportExportProjectDto.ImportExportProjectGridDto actual =
            projectGridMapper.toImportExportProjectGridDto(projectGrid);

        assertEquals(1L, actual.getId());
        assertEquals(10, actual.getTime());
        assertEquals("technicalName", actual.getWidgets().getFirst().getWidgetTechnicalName());
        assertEquals("key=value", actual.getWidgets().getFirst().getBackendConfig());
        assertEquals(1, actual.getWidgets().getFirst().getWidgetPosition().getGridColumn());
        assertEquals(1, actual.getWidgets().getFirst().getWidgetPosition().getGridRow());
        assertEquals(1, actual.getWidgets().getFirst().getWidgetPosition().getHeight());
        assertEquals(1, actual.getWidgets().getFirst().getWidgetPosition().getWidth());
    }

    @Test
    void shouldToProjectGridEntityProject() {
        Project project = new Project();
        project.setId(1L);

        ProjectGridRequestDto.GridRequestDto projectGridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        projectGridRequestDto.setId(1L);
        projectGridRequestDto.setTime(10);

        ProjectGrid actual = projectGridMapper.toProjectGridEntity(projectGridRequestDto, project);

        assertNull(actual.getId());
        assertEquals(10, actual.getTime());
        assertEquals(project, actual.getProject());
    }

    @Test
    void shouldToProjectGridEntityImportExport() {
        ProjectWidgetPositionResponseDto projectWidgetPositionResponseDto = new ProjectWidgetPositionResponseDto();
        projectWidgetPositionResponseDto.setGridColumn(1);
        projectWidgetPositionResponseDto.setGridRow(1);
        projectWidgetPositionResponseDto.setHeight(1);
        projectWidgetPositionResponseDto.setWidth(1);

        ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto importExportProjectWidgetDto =
            new ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto();
        importExportProjectWidgetDto.setWidgetPosition(projectWidgetPositionResponseDto);
        importExportProjectWidgetDto.setWidgetTechnicalName("technicalName");
        importExportProjectWidgetDto.setBackendConfig("key=value");

        ImportExportProjectDto.ImportExportProjectGridDto importExportProjectGridDto =
            new ImportExportProjectDto.ImportExportProjectGridDto();
        importExportProjectGridDto.setId(1L);
        importExportProjectGridDto.setTime(10);
        importExportProjectGridDto.setWidgets(Collections.singletonList(importExportProjectWidgetDto));

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        when(projectWidgetMapper.toProjectWidgetEntity(any()))
            .thenReturn(projectWidget);

        ProjectGrid actual = projectGridMapper.toProjectGridEntity(importExportProjectGridDto);

        assertEquals(1L, actual.getId());
        assertEquals(10, actual.getTime());
        assertNull(actual.getProject());
        assertEquals(projectWidget, new ArrayList<>(actual.getWidgets()).getFirst());
    }
}
