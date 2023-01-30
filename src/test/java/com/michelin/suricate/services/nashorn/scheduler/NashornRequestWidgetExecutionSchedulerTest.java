package com.michelin.suricate.services.nashorn.scheduler;

import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectGrid;
import com.michelin.suricate.services.api.ProjectWidgetService;
import com.michelin.suricate.model.dto.nashorn.NashornRequest;
import com.michelin.suricate.model.dto.nashorn.WidgetVariableResponse;
import com.michelin.suricate.model.entities.ProjectWidget;
import com.michelin.suricate.model.entities.Widget;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import com.michelin.suricate.services.api.WidgetService;
import com.michelin.suricate.services.nashorn.services.NashornService;
import com.michelin.suricate.services.nashorn.tasks.NashornRequestResultAsyncTask;
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
class NashornRequestWidgetExecutionSchedulerTest {
    @Mock
    private NashornService nashornService;

    @Mock
    private ProjectWidgetService projectWidgetService;

    @Mock
    private WidgetService widgetService;

    @Mock
    private NashornRequestResultAsyncTask nashornRequestResultAsyncTask;

    @Mock
    private ApplicationContext applicationContext;

    @Spy
    @InjectMocks
    private NashornRequestWidgetExecutionScheduler scheduler;

    @Test
    void shouldNotScheduleNullRequest() {
        scheduler.schedule(null, true);

        verify(nashornService, times(0)).isNashornRequestExecutable(any());
    }

    @Test
    void shouldNotScheduleNotExecutableRequest() {
        when(nashornService.isNashornRequestExecutable(any()))
                .thenReturn(false);
        doNothing().when(projectWidgetService)
                .resetProjectWidgetsState();
        doNothing().when(projectWidgetService)
                .updateState(any(), any(), any());

        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectWidgetId(1L);

        scheduler.init();
        scheduler.schedule(nashornRequest, true);

        verify(nashornService, times(1))
                .isNashornRequestExecutable(nashornRequest);
        verify(projectWidgetService, times(1))
                .updateState(argThat(WidgetStateEnum.STOPPED::equals), argThat(projectWidgetId -> projectWidgetId.equals(1L)), any());
    }

    @Test
    void shouldScheduleWidgetStoppedWithDelay() {
        WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
        widgetVariableResponse.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(nashornService.isNashornRequestExecutable(any()))
                .thenReturn(true);
        doNothing().when(projectWidgetService)
                .resetProjectWidgetsState();
        doNothing().when(projectWidgetService)
                .updateState(any(), any(), any());
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForNashorn(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponse));
        when(applicationContext.getBean(eq(NashornRequestResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(nashornRequestResultAsyncTask);

        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setWidgetState(WidgetStateEnum.STOPPED);
        nashornRequest.setDelay(15L);

        scheduler.init();
        scheduler.schedule(nashornRequest, false);

        verify(nashornService, times(1))
                .isNashornRequestExecutable(nashornRequest);
        verify(projectWidgetService, times(1))
                .getOne(1L);
        verify(widgetService, times(1))
                .getWidgetParametersForNashorn(widget);
        verify(projectWidgetService, times(1))
                .updateState(argThat(WidgetStateEnum.RUNNING::equals), argThat(projectWidgetId -> projectWidgetId.equals(1L)), any());
    }

    @Test
    void shouldScheduleWidgetRunning() {
        WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
        widgetVariableResponse.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(nashornService.isNashornRequestExecutable(any()))
                .thenReturn(true);
        doNothing().when(projectWidgetService)
                .resetProjectWidgetsState();
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForNashorn(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponse));
        when(applicationContext.getBean(eq(NashornRequestResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(nashornRequestResultAsyncTask);

        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.schedule(nashornRequest, true);

        verify(nashornService, times(1))
                .isNashornRequestExecutable(nashornRequest);
        verify(projectWidgetService, times(1))
                .getOne(1L);
        verify(widgetService, times(1))
                .getWidgetParametersForNashorn(widget);
        verify(projectWidgetService, times(0))
                .updateState(any(), any(), any());
    }

    @Test
    void shouldScheduleNashornRequests() {
        WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
        widgetVariableResponse.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(nashornService.isNashornRequestExecutable(any()))
                .thenReturn(true);
        doNothing().when(projectWidgetService)
                .resetProjectWidgetsState();
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForNashorn(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponse));
        when(applicationContext.getBean(eq(NashornRequestResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(nashornRequestResultAsyncTask);

        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.scheduleNashornRequests(Collections.singletonList(nashornRequest), true);

        verify(nashornService, times(1))
                .isNashornRequestExecutable(nashornRequest);
        verify(projectWidgetService, times(1))
                .getOne(1L);
        verify(widgetService, times(1))
                .getWidgetParametersForNashorn(widget);
        verify(projectWidgetService, times(0))
                .updateState(any(), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenScheduleNashornRequests() {
        WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
        widgetVariableResponse.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setWidgetState(WidgetStateEnum.RUNNING);

        doThrow(new RuntimeException("error")).when(scheduler)
                .schedule(nashornRequest, true);

        scheduler.scheduleNashornRequests(Collections.singletonList(nashornRequest), true);

        verify(nashornService, times(0))
                .isNashornRequestExecutable(any());
        verify(projectWidgetService, times(0))
                .getOne(any());
        verify(widgetService, times(0))
                .getWidgetParametersForNashorn(any());
        verify(projectWidgetService, times(0))
                .updateState(any(), any(), any());
    }

    @Test
    void shouldCancelAndScheduleNashornRequestWhenPreviousTaskExist() {
        WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
        widgetVariableResponse.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(nashornService.isNashornRequestExecutable(any()))
                .thenReturn(true);
        doNothing().when(projectWidgetService)
                .resetProjectWidgetsState();
        doNothing().when(projectWidgetService)
                .updateState(any(), any());
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForNashorn(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponse));
        when(applicationContext.getBean(eq(NashornRequestResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(nashornRequestResultAsyncTask);

        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.schedule(nashornRequest, true);
        scheduler.cancelAndScheduleNashornRequest(nashornRequest);

        verify(nashornService, times(2))
                .isNashornRequestExecutable(nashornRequest);
        verify(projectWidgetService, times(2))
                .getOne(1L);
        verify(widgetService, times(2))
                .getWidgetParametersForNashorn(widget);
        verify(scheduler, times(2))
                .cancelScheduledFutureTask(eq(1L), any());
        verify(projectWidgetService, times(1))
                .updateState(WidgetStateEnum.STOPPED, 1L);
    }

    @Test
    void shouldCancelAndScheduleNashornRequest() {
        WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
        widgetVariableResponse.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(nashornService.isNashornRequestExecutable(any()))
                .thenReturn(true);
        doNothing().when(projectWidgetService)
                .resetProjectWidgetsState();
        doNothing().when(projectWidgetService)
                .updateState(any(), any());
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForNashorn(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponse));
        when(applicationContext.getBean(eq(NashornRequestResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(nashornRequestResultAsyncTask);

        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.cancelAndScheduleNashornRequest(nashornRequest);

        verify(nashornService, times(1))
                .isNashornRequestExecutable(nashornRequest);
        verify(projectWidgetService, times(1))
                .getOne(1L);
        verify(widgetService, times(1))
                .getWidgetParametersForNashorn(widget);
        verify(scheduler, times(0))
                .cancelScheduledFutureTask(eq(1L), any());
        verify(projectWidgetService, times(1))
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

        verify(projectWidgetService, times(1))
                .updateState(WidgetStateEnum.STOPPED, 1L);
    }
}
