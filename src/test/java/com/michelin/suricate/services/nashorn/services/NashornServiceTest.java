package com.michelin.suricate.services.nashorn.services;

import com.google.common.collect.Sets;
import com.michelin.suricate.model.dto.nashorn.NashornRequest;
import com.michelin.suricate.model.entities.*;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import com.michelin.suricate.services.api.ProjectWidgetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NashornServiceTest {
    @Mock
    private ProjectWidgetService projectWidgetService;

    @InjectMocks
    private NashornService nashornService;

    @Test
    void shouldGetNashornRequestsByProject() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("categoryKey");
        categoryParameter.setValue("categoryValue");

        Category category = new Category();
        category.setId(1L);
        category.setConfigurations(Collections.singleton(categoryParameter));

        Widget widget = new Widget();
        widget.setId(1L);
        widget.setCategory(category);
        widget.setBackendJs("backendJs");
        widget.setDelay(10L);
        widget.setTimeout(15L);

        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);
        project.setGrids(Collections.singleton(projectGrid));

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);
        projectWidget.setData("data");
        projectWidget.setBackendConfig("key=value");
        projectWidget.setState(WidgetStateEnum.RUNNING);
        projectWidget.setLastSuccessDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        projectWidget.setProjectGrid(projectGrid);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        List<NashornRequest> actual = nashornService.getNashornRequestsByProject(project);

        assertThat(actual.get(0).getProperties()).isEqualTo("key=value\ncategoryKey=categoryValue\n");
        assertThat(actual.get(0).getScript()).isEqualTo("backendJs");
        assertThat(actual.get(0).getPreviousData()).isEqualTo("data");
        assertThat(actual.get(0).getProjectId()).isEqualTo(1L);
        assertThat(actual.get(0).getProjectWidgetId()).isEqualTo(1L);
        assertThat(actual.get(0).getDelay()).isEqualTo(10L);
        assertThat(actual.get(0).getTimeout()).isEqualTo(15L);
        assertThat(actual.get(0).getWidgetState()).isEqualTo(WidgetStateEnum.RUNNING);
        assertThat(actual.get(0).isAlreadySuccess()).isTrue();
    }

    @Test
    void shouldGetNashornRequestByProjectWidgetId() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("categoryKey");
        categoryParameter.setValue("categoryValue");

        CategoryParameter categoryParameterInBackendConfig = new CategoryParameter();
        categoryParameterInBackendConfig.setKey("key");
        categoryParameterInBackendConfig.setValue("value");

        Category category = new Category();
        category.setId(1L);
        category.setConfigurations(Sets.newHashSet(categoryParameter, categoryParameterInBackendConfig));

        Widget widget = new Widget();
        widget.setId(1L);
        widget.setCategory(category);
        widget.setBackendJs("backendJs");
        widget.setDelay(10L);
        widget.setTimeout(15L);

        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);
        project.setGrids(Collections.singleton(projectGrid));

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);
        projectWidget.setData("data");
        projectWidget.setBackendConfig("key=value");
        projectWidget.setState(WidgetStateEnum.RUNNING);
        projectWidget.setLastSuccessDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        projectWidget.setProjectGrid(projectGrid);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));

        NashornRequest actual = nashornService.getNashornRequestByProjectWidgetId(1L);

        assertThat(actual.getProperties()).isEqualTo("key=value\ncategoryKey=categoryValue\n");
        assertThat(actual.getScript()).isEqualTo("backendJs");
        assertThat(actual.getPreviousData()).isEqualTo("data");
        assertThat(actual.getProjectId()).isEqualTo(1L);
        assertThat(actual.getProjectWidgetId()).isEqualTo(1L);
        assertThat(actual.getDelay()).isEqualTo(10L);
        assertThat(actual.getTimeout()).isEqualTo(15L);
        assertThat(actual.getWidgetState()).isEqualTo(WidgetStateEnum.RUNNING);
        assertThat(actual.isAlreadySuccess()).isTrue();
    }

    @Test
    void shouldGetNashornRequestByProjectWidgetIdEmptyCategoryParams() {
        Category category = new Category();
        category.setId(1L);

        Widget widget = new Widget();
        widget.setId(1L);
        widget.setCategory(category);
        widget.setBackendJs("backendJs");
        widget.setDelay(10L);
        widget.setTimeout(15L);

        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);
        project.setGrids(Collections.singleton(projectGrid));

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);
        projectWidget.setData("data");
        projectWidget.setBackendConfig("key=value");
        projectWidget.setState(WidgetStateEnum.RUNNING);
        projectWidget.setLastSuccessDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        projectWidget.setProjectGrid(projectGrid);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));

        NashornRequest actual = nashornService.getNashornRequestByProjectWidgetId(1L);

        assertThat(actual.getProperties()).isEqualTo("key=value");
        assertThat(actual.getScript()).isEqualTo("backendJs");
        assertThat(actual.getPreviousData()).isEqualTo("data");
        assertThat(actual.getProjectId()).isEqualTo(1L);
        assertThat(actual.getProjectWidgetId()).isEqualTo(1L);
        assertThat(actual.getDelay()).isEqualTo(10L);
        assertThat(actual.getTimeout()).isEqualTo(15L);
        assertThat(actual.getWidgetState()).isEqualTo(WidgetStateEnum.RUNNING);
        assertThat(actual.isAlreadySuccess()).isTrue();
    }

    @Test
    void shouldNashornRequestNotExecutableBecauseNoScript() {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);

        boolean actual = nashornService.isNashornRequestExecutable(nashornRequest);

        assertThat(actual).isFalse();
    }

    @Test
    void shouldNashornRequestNotExecutableBecauseInvalidData() {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setScript("script");
        nashornRequest.setPreviousData("invalid");

        boolean actual = nashornService.isNashornRequestExecutable(nashornRequest);

        assertThat(actual).isFalse();
    }

    @Test
    void shouldNashornRequestNotExecutableBecauseNoDelay() {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setScript("script");
        nashornRequest.setPreviousData("{\"key\": \"value\"}");

        boolean actual = nashornService.isNashornRequestExecutable(nashornRequest);

        assertThat(actual).isFalse();
    }

    @Test
    void shouldNashornRequestNotExecutableBecauseDelayLowerThan0() {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setScript("script");
        nashornRequest.setPreviousData("{\"key\": \"value\"}");
        nashornRequest.setDelay(-10L);

        boolean actual = nashornService.isNashornRequestExecutable(nashornRequest);

        assertThat(actual).isFalse();
    }

    @Test
    void shouldNashornRequestBeExecutable() {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setScript("script");
        nashornRequest.setPreviousData("{\"key\": \"value\"}");
        nashornRequest.setDelay(10L);

        boolean actual = nashornService.isNashornRequestExecutable(nashornRequest);

        assertThat(actual).isTrue();
    }
}
