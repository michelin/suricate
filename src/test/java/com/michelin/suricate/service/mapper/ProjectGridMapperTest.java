package com.michelin.suricate.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.assertj.core.api.Assertions;
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

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getTime()).isEqualTo(10);
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

        assertThat(actual.getTime()).isEqualTo(60);
        assertThat(actual.getProject()).isEqualTo(project);
        Assertions.assertThat(actual.getCreatedBy()).isEqualTo("createdBy");
        Assertions.assertThat(actual.getCreatedDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        Assertions.assertThat(actual.getLastModifiedBy()).isEqualTo("lastModifiedBy");
        Assertions.assertThat(actual.getLastModifiedDate())
            .isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
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

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getTime()).isEqualTo(10);
        assertThat(actual.getWidgets().get(0).getWidgetTechnicalName()).isEqualTo("technicalName");
        assertThat(actual.getWidgets().get(0).getBackendConfig()).isEqualTo("key=value");
        Assertions.assertThat(actual.getWidgets().get(0).getWidgetPosition().getGridColumn()).isEqualTo(1);
        Assertions.assertThat(actual.getWidgets().get(0).getWidgetPosition().getGridRow()).isEqualTo(1);
        Assertions.assertThat(actual.getWidgets().get(0).getWidgetPosition().getHeight()).isEqualTo(1);
        Assertions.assertThat(actual.getWidgets().get(0).getWidgetPosition().getWidth()).isEqualTo(1);
    }

    @Test
    void shouldToProjectGridEntityProject() {
        Project project = new Project();
        project.setId(1L);

        ProjectGridRequestDto.GridRequestDto projectGridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        projectGridRequestDto.setId(1L);
        projectGridRequestDto.setTime(10);

        ProjectGrid actual = projectGridMapper.toProjectGridEntity(projectGridRequestDto, project);

        assertThat(actual.getId()).isNull();
        assertThat(actual.getTime()).isEqualTo(10);
        assertThat(actual.getProject()).isEqualTo(project);
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

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getTime()).isEqualTo(10);
        assertThat(actual.getProject()).isNull();
        assertThat(new ArrayList<>(actual.getWidgets()).get(0)).isEqualTo(projectWidget);
    }
}
