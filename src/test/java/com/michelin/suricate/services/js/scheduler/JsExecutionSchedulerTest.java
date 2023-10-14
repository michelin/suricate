package com.michelin.suricate.services.js.scheduler;

import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectGrid;
import com.michelin.suricate.services.api.ProjectWidgetService;
import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.js.WidgetVariableResponseDto;
import com.michelin.suricate.model.entities.ProjectWidget;
import com.michelin.suricate.model.entities.Widget;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import com.michelin.suricate.services.api.WidgetService;
import com.michelin.suricate.services.js.services.JsExecutionService;
import com.michelin.suricate.services.js.tasks.JsResultAsyncTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsExecutionSchedulerTest {
    @Mock
    private JsExecutionService jsExecutionService;

    @Mock
    private ProjectWidgetService projectWidgetService;

    @Mock
    private WidgetService widgetService;

    @Mock
    private JsResultAsyncTask jsResultAsyncTask;

    @Mock
    private ApplicationContext applicationContext;

    @Spy
    @InjectMocks
    private JsExecutionScheduler scheduler;

    @Test
    void shouldNotScheduleNullRequest() {
        scheduler.schedule(null, true);

        verify(jsExecutionService, times(0)).isJsExecutable(any());
    }

    @Test
    void shouldNotScheduleNotExecutableRequest() {
        when(jsExecutionService.isJsExecutable(any()))
                .thenReturn(false);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);

        scheduler.init();
        scheduler.schedule(jsExecutionDto, true);

        verify(jsExecutionService)
                .isJsExecutable(jsExecutionDto);
        verify(projectWidgetService)
                .updateState(argThat(WidgetStateEnum.STOPPED::equals), argThat(projectWidgetId -> projectWidgetId.equals(1L)), any());
    }

    @Test
    void shouldScheduleWidgetStoppedWithDelay() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(jsExecutionService.isJsExecutable(any()))
                .thenReturn(true);
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForJsExecution(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponseDto));
        when(applicationContext.getBean(eq(JsResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(jsResultAsyncTask);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.STOPPED);
        jsExecutionDto.setDelay(15L);

        scheduler.init();
        scheduler.schedule(jsExecutionDto, false);

        verify(jsExecutionService)
                .isJsExecutable(jsExecutionDto);
        verify(projectWidgetService)
                .getOne(1L);
        verify(widgetService)
                .getWidgetParametersForJsExecution(widget);
        verify(projectWidgetService)
                .updateState(argThat(WidgetStateEnum.RUNNING::equals), argThat(projectWidgetId -> projectWidgetId.equals(1L)), any());
    }

    @Test
    void shouldScheduleWidgetRunning() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(jsExecutionService.isJsExecutable(any()))
                .thenReturn(true);
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForJsExecution(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponseDto));
        when(applicationContext.getBean(eq(JsResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(jsResultAsyncTask);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.schedule(jsExecutionDto, true);

        verify(jsExecutionService)
                .isJsExecutable(jsExecutionDto);
        verify(projectWidgetService)
                .getOne(1L);
        verify(widgetService)
                .getWidgetParametersForJsExecution(widget);
        verify(projectWidgetService, times(0))
                .updateState(any(), any(), any());
    }

    @Test
    void shouldScheduleJsExecRequests() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(jsExecutionService.isJsExecutable(any()))
                .thenReturn(true);
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForJsExecution(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponseDto));
        when(applicationContext.getBean(eq(JsResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(jsResultAsyncTask);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.scheduleJsRequests(Collections.singletonList(jsExecutionDto), true);

        verify(jsExecutionService)
                .isJsExecutable(jsExecutionDto);
        verify(projectWidgetService)
                .getOne(1L);
        verify(widgetService)
                .getWidgetParametersForJsExecution(widget);
        verify(projectWidgetService, times(0))
                .updateState(any(), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenScheduleJsRequests() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.RUNNING);

        doThrow(new RuntimeException("error")).when(scheduler)
                .schedule(jsExecutionDto, true);

        scheduler.scheduleJsRequests(Collections.singletonList(jsExecutionDto), true);

        verify(jsExecutionService, times(0))
                .isJsExecutable(any());
        verify(projectWidgetService, times(0))
                .getOne(any());
        verify(widgetService, times(0))
                .getWidgetParametersForJsExecution(any());
        verify(projectWidgetService, times(0))
                .updateState(any(), any(), any());
    }

    @Test
    void shouldCancelAndScheduleJsExecutionWhenPreviousTaskExist() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(jsExecutionService.isJsExecutable(any()))
                .thenReturn(true);
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForJsExecution(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponseDto));
        when(applicationContext.getBean(eq(JsResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(jsResultAsyncTask);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.schedule(jsExecutionDto, true);
        scheduler.cancelAndScheduleJsExecution(jsExecutionDto);

        verify(jsExecutionService, times(2))
                .isJsExecutable(jsExecutionDto);
        verify(projectWidgetService, times(2))
                .getOne(1L);
        verify(widgetService, times(2))
                .getWidgetParametersForJsExecution(widget);
        verify(scheduler, times(2))
                .cancelScheduledFutureTask(eq(1L), any());
        verify(projectWidgetService)
                .updateState(WidgetStateEnum.STOPPED, 1L);
    }

    @Test
    void shouldCancelAndScheduleJsExecution() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(jsExecutionService.isJsExecutable(any()))
                .thenReturn(true);
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForJsExecution(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponseDto));
        when(applicationContext.getBean(eq(JsResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(jsResultAsyncTask);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.cancelAndScheduleJsExecution(jsExecutionDto);

        verify(jsExecutionService)
                .isJsExecutable(jsExecutionDto);
        verify(projectWidgetService)
                .getOne(1L);
        verify(widgetService)
                .getWidgetParametersForJsExecution(widget);
        verify(scheduler, times(0))
                .cancelScheduledFutureTask(eq(1L), any());
        verify(projectWidgetService)
                .updateState(WidgetStateEnum.STOPPED, 1L);
    }

    @Test
    void shouldCancelWidgetsExecutionByProject() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setId(1L);
        project.setGrids(Collections.singleton(projectGrid));

        scheduler.init();
        scheduler.cancelWidgetsExecutionByProject(project);

        verify(projectWidgetService)
                .updateState(WidgetStateEnum.STOPPED, 1L);
    }
}
