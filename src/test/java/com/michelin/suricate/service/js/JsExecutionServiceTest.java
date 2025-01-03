package com.michelin.suricate.service.js;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.CategoryParameter;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.enumeration.WidgetStateEnum;
import com.michelin.suricate.service.api.ProjectWidgetService;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JsExecutionServiceTest {
    @Mock
    private ProjectWidgetService projectWidgetService;

    @InjectMocks
    private JsExecutionService jsExecutionService;

    @Test
    void shouldGetJsExecutionsByProject() {
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

        List<JsExecutionDto> actual = jsExecutionService.getJsExecutionsByProject(project);

        assertEquals("key=value\ncategoryKey=categoryValue\n", actual.getFirst().getProperties());
        assertEquals("backendJs", actual.getFirst().getScript());
        assertEquals("data", actual.getFirst().getPreviousData());
        assertEquals(1L, actual.getFirst().getProjectId());
        assertEquals(1L, actual.getFirst().getProjectWidgetId());
        assertEquals(10L, actual.getFirst().getDelay());
        assertEquals(15L, actual.getFirst().getTimeout());
        assertEquals(WidgetStateEnum.RUNNING, actual.getFirst().getWidgetState());
        assertTrue(actual.getFirst().isAlreadySuccess());
    }

    @Test
    void shouldGetJsExecutionByProjectWidgetId() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("categoryKey");
        categoryParameter.setValue("categoryValue");

        CategoryParameter categoryParameterInBackendConfig = new CategoryParameter();
        categoryParameterInBackendConfig.setKey("key");
        categoryParameterInBackendConfig.setValue("value");

        Category category = new Category();
        category.setId(1L);
        category.setConfigurations(Set.of(categoryParameter, categoryParameterInBackendConfig));

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

        JsExecutionDto actual = jsExecutionService.getJsExecutionByProjectWidgetId(1L);

        assertEquals("key=value\ncategoryKey=categoryValue\n", actual.getProperties());
        assertEquals("backendJs", actual.getScript());
        assertEquals("data", actual.getPreviousData());
        assertEquals(1L, actual.getProjectId());
        assertEquals(1L, actual.getProjectWidgetId());
        assertEquals(10L, actual.getDelay());
        assertEquals(15L, actual.getTimeout());
        assertEquals(WidgetStateEnum.RUNNING, actual.getWidgetState());
        assertTrue(actual.isAlreadySuccess());
    }

    @Test
    void shouldGetJsExecutionByProjectWidgetIdEmptyCategoryParams() {
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

        JsExecutionDto actual = jsExecutionService.getJsExecutionByProjectWidgetId(1L);

        assertEquals("key=value", actual.getProperties());
        assertEquals("backendJs", actual.getScript());
        assertEquals("data", actual.getPreviousData());
        assertEquals(1L, actual.getProjectId());
        assertEquals(1L, actual.getProjectWidgetId());
        assertEquals(10L, actual.getDelay());
        assertEquals(15L, actual.getTimeout());
        assertEquals(WidgetStateEnum.RUNNING, actual.getWidgetState());
        assertTrue(actual.isAlreadySuccess());
    }

    @Test
    void shouldJsExecutionNotExecutableBecauseNoScript() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);

        boolean actual = jsExecutionService.isJsExecutable(jsExecutionDto);

        assertFalse(actual);
    }

    @Test
    void shouldJsExecutionNotExecutableBecauseInvalidData() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setScript("script");
        jsExecutionDto.setPreviousData("invalid");

        boolean actual = jsExecutionService.isJsExecutable(jsExecutionDto);

        assertFalse(actual);
    }

    @Test
    void shouldJsExecutionNotExecutableBecauseNoDelay() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setScript("script");
        jsExecutionDto.setPreviousData("{\"key\": \"value\"}");

        boolean actual = jsExecutionService.isJsExecutable(jsExecutionDto);

        assertFalse(actual);
    }

    @Test
    void shouldJsExecutionNotExecutableBecauseDelayLowerThan0() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setScript("script");
        jsExecutionDto.setPreviousData("{\"key\": \"value\"}");
        jsExecutionDto.setDelay(-10L);

        boolean actual = jsExecutionService.isJsExecutable(jsExecutionDto);

        assertFalse(actual);
    }

    @Test
    void shouldJsExecutionBeExecutable() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setScript("script");
        jsExecutionDto.setPreviousData("{\"key\": \"value\"}");
        jsExecutionDto.setDelay(10L);

        boolean actual = jsExecutionService.isJsExecutable(jsExecutionDto);

        assertTrue(actual);
    }
}
