package com.michelin.suricate.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
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

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getWidgetId()).isEqualTo(1L);
        assertThat(actual.getBackendConfig()).isEqualTo("backendConfig");
        assertThat(actual.getData()).isEqualTo("data");
        assertThat(actual.getCustomStyle()).isEqualTo("style");
        assertThat(actual.getInstantiateHtml()).isEqualTo("html");
        assertThat(actual.getGridId()).isEqualTo(1);
        assertThat(actual.getLastExecutionDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        assertThat(actual.getLastSuccessDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        assertThat(actual.getProjectToken()).isEqualTo("token");
        assertThat(actual.getLog()).isEqualTo("log");
        assertThat(actual.getWidgetPosition().getWidth()).isEqualTo(1);
        assertThat(actual.getWidgetPosition().getHeight()).isEqualTo(1);
        assertThat(actual.getWidgetPosition().getGridRow()).isEqualTo(1);
        assertThat(actual.getWidgetPosition().getGridColumn()).isEqualTo(1);
        assertThat(actual.getWidgetTechnicalName()).isNull();
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

        assertThat(actual.get(0).getId()).isEqualTo(1L);
        assertThat(actual.get(0).getWidgetId()).isEqualTo(1L);
        assertThat(actual.get(0).getBackendConfig()).isEqualTo("backendConfig");
        assertThat(actual.get(0).getData()).isEqualTo("data");
        assertThat(actual.get(0).getCustomStyle()).isEqualTo("style");
        assertThat(actual.get(0).getInstantiateHtml()).isEqualTo("html");
        assertThat(actual.get(0).getGridId()).isEqualTo(1);
        assertThat(actual.get(0).getLastExecutionDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        assertThat(actual.get(0).getLastSuccessDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        assertThat(actual.get(0).getProjectToken()).isEqualTo("token");
        assertThat(actual.get(0).getLog()).isEqualTo("log");
        assertThat(actual.get(0).getWidgetPosition().getWidth()).isEqualTo(1);
        assertThat(actual.get(0).getWidgetPosition().getHeight()).isEqualTo(1);
        assertThat(actual.get(0).getWidgetPosition().getGridRow()).isEqualTo(1);
        assertThat(actual.get(0).getWidgetPosition().getGridColumn()).isEqualTo(1);
        assertThat(actual.get(0).getWidgetTechnicalName()).isNull();
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

        assertThat(actual.getId()).isNull();
        assertThat(actual.getBackendConfig()).isEqualTo("backendConfig");
        assertThat(actual.getData()).isEqualTo("data");
        assertThat(actual.getGridColumn()).isEqualTo(1);
        assertThat(actual.getGridRow()).isEqualTo(1);
        assertThat(actual.getHeight()).isEqualTo(1);
        assertThat(actual.getWidth()).isEqualTo(1);
        assertThat(actual.getCustomStyle()).isEqualTo("style");
        assertThat(actual.getProjectGrid()).isEqualTo(projectGrid);
        assertThat(actual.getWidget()).isEqualTo(widget);
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

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getBackendConfig()).isEqualTo("backendConfig");
        assertThat(actual.getData()).isEqualTo("{}");
        assertThat(actual.getGridColumn()).isEqualTo(1);
        assertThat(actual.getGridRow()).isEqualTo(1);
        assertThat(actual.getHeight()).isEqualTo(1);
        assertThat(actual.getWidth()).isEqualTo(1);
        assertThat(actual.getCustomStyle()).isNull();
        assertThat(actual.getProjectGrid()).isNull();
        assertThat(actual.getWidget()).isEqualTo(widget);
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

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getBackendConfig()).isEqualTo("backendConfig");
        assertThat(actual.getWidgetTechnicalName()).isEqualTo("technicalName");
        assertThat(actual.getWidgetPosition().getWidth()).isEqualTo(1);
        assertThat(actual.getWidgetPosition().getHeight()).isEqualTo(1);
        assertThat(actual.getWidgetPosition().getGridRow()).isEqualTo(1);
        assertThat(actual.getWidgetPosition().getGridColumn()).isEqualTo(1);
    }
}
