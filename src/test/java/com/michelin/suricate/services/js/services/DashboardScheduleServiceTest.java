package com.michelin.suricate.services.js.services;

import static com.michelin.suricate.model.enums.UpdateType.REFRESH_WIDGET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.js.JsResultDto;
import com.michelin.suricate.model.entities.ProjectWidget;
import com.michelin.suricate.model.enums.JsExecutionErrorTypeEnum;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import com.michelin.suricate.services.api.ProjectService;
import com.michelin.suricate.services.api.ProjectWidgetService;
import com.michelin.suricate.services.js.scheduler.JsExecutionScheduler;
import com.michelin.suricate.services.mapper.ProjectWidgetMapper;
import com.michelin.suricate.services.websocket.DashboardWebSocketService;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class DashboardScheduleServiceTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private JsExecutionScheduler jsExecutionScheduler;

    @Mock
    private DashboardWebSocketService dashboardWebSocketService;

    @Mock
    private ProjectWidgetService projectWidgetService;

    @Mock
    private ProjectWidgetMapper projectWidgetMapper;

    @Mock
    private JsExecutionService jsExecutionService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private DashboardScheduleService dashboardScheduleService;

    @Test
    void shouldProcessValidJsResult() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);

        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);
        jsResultDto.setProjectWidgetId(1L);
        jsResultDto.setData("{}");
        jsResultDto.setLog("log");
        jsResultDto.setLaunchDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        when(jsExecutionService.getJsExecutionByProjectWidgetId(any())).thenReturn(jsExecutionDto);
        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDto(any())).thenReturn(projectWidgetResponseDto);
        when(projectService.getTokenByProjectId(any())).thenReturn("token");

        dashboardScheduleService.processJsResult(jsResultDto, jsExecutionScheduler);

        verify(projectWidgetService)
            .updateWidgetInstanceAfterSucceededExecution(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")),
                "log", "{}", 1L, WidgetStateEnum.RUNNING);
        verify(jsExecutionService)
            .getJsExecutionByProjectWidgetId(1L);
        verify(jsExecutionScheduler)
            .schedule(jsExecutionDto, false);
        verify(projectWidgetService)
            .getOne(1L);
        verify(projectWidgetMapper)
            .toProjectWidgetDto(projectWidget);
        verify(projectService)
            .getTokenByProjectId(1L);
        verify(dashboardWebSocketService)
            .sendEventToWidgetInstanceSubscribers(eq("token"), eq(1L), argThat(event ->
                event.getType().equals(REFRESH_WIDGET) && event.getContent().equals(projectWidgetResponseDto)));
    }

    @Test
    void shouldProcessErrorJsResult() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);

        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);
        jsResultDto.setProjectWidgetId(1L);
        jsResultDto.setData("{}");
        jsResultDto.setLog("log");
        jsResultDto.setError(JsExecutionErrorTypeEnum.ERROR);
        jsResultDto.setLaunchDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDto(any())).thenReturn(projectWidgetResponseDto);
        when(projectService.getTokenByProjectId(any())).thenReturn("token");

        dashboardScheduleService.processJsResult(jsResultDto, jsExecutionScheduler);

        verify(projectWidgetService)
            .updateWidgetInstanceAfterFailedExecution(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")),
                "log", 1L, WidgetStateEnum.WARNING);
        verify(projectWidgetService)
            .getOne(1L);
        verify(projectWidgetMapper)
            .toProjectWidgetDto(projectWidget);
        verify(projectService)
            .getTokenByProjectId(1L);
        verify(dashboardWebSocketService)
            .sendEventToWidgetInstanceSubscribers(eq("token"), eq(1L), argThat(event ->
                event.getType().equals(REFRESH_WIDGET) && event.getContent().equals(projectWidgetResponseDto)));
    }

    @Test
    void shouldProcessFatalJsResult() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);

        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);
        jsResultDto.setProjectWidgetId(1L);
        jsResultDto.setData("{}");
        jsResultDto.setLog("log");
        jsResultDto.setError(JsExecutionErrorTypeEnum.FATAL);
        jsResultDto.setLaunchDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDto(any())).thenReturn(projectWidgetResponseDto);
        when(projectService.getTokenByProjectId(any())).thenReturn("token");

        dashboardScheduleService.processJsResult(jsResultDto, jsExecutionScheduler);

        verify(projectWidgetService)
            .updateWidgetInstanceAfterFailedExecution(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")),
                "log", 1L, WidgetStateEnum.STOPPED);
        verify(projectWidgetService)
            .getOne(1L);
        verify(projectWidgetMapper)
            .toProjectWidgetDto(projectWidget);
        verify(projectService)
            .getTokenByProjectId(1L);
        verify(dashboardWebSocketService)
            .sendEventToWidgetInstanceSubscribers(eq("token"), eq(1L), argThat(event ->
                event.getType().equals(REFRESH_WIDGET) && event.getContent().equals(projectWidgetResponseDto)));
    }

    @Test
    void shouldUpdateWidgetInstanceNoJsResult() {
        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDto(any())).thenReturn(projectWidgetResponseDto);
        when(projectService.getTokenByProjectId(any())).thenReturn("token");

        dashboardScheduleService.updateWidgetInstanceNoJsResult("logs", 1L, 1L);

        verify(projectWidgetService)
            .updateWidgetInstanceAfterFailedExecution(any(),
                eq("logs"), eq(1L), eq(WidgetStateEnum.STOPPED));
        verify(projectWidgetService)
            .getOne(1L);
        verify(projectWidgetMapper)
            .toProjectWidgetDto(projectWidget);
        verify(projectService)
            .getTokenByProjectId(1L);
        verify(dashboardWebSocketService)
            .sendEventToWidgetInstanceSubscribers(eq("token"), eq(1L), argThat(event ->
                event.getType().equals(REFRESH_WIDGET) && event.getContent().equals(projectWidgetResponseDto)));
    }

    @Test
    void shouldSendWidgetUpdateNotification() {
        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDto(any())).thenReturn(projectWidgetResponseDto);
        when(projectService.getTokenByProjectId(any())).thenReturn("token");

        dashboardScheduleService.sendWidgetUpdateNotification(1L, 1L);

        verify(projectWidgetService)
            .getOne(1L);
        verify(projectWidgetMapper)
            .toProjectWidgetDto(projectWidget);
        verify(projectService)
            .getTokenByProjectId(1L);
        verify(dashboardWebSocketService)
            .sendEventToWidgetInstanceSubscribers(eq("token"), eq(1L), argThat(event ->
                event.getType().equals(REFRESH_WIDGET) && event.getContent().equals(projectWidgetResponseDto)));
    }

    @Test
    void shouldScheduleWidget() {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);

        when(jsExecutionService.getJsExecutionByProjectWidgetId(any())).thenReturn(jsExecutionDto);
        when(applicationContext.getBean(JsExecutionScheduler.class))
            .thenReturn(jsExecutionScheduler);

        dashboardScheduleService.scheduleWidget(1L);

        verify(jsExecutionService)
            .getJsExecutionByProjectWidgetId(1L);
        verify(jsExecutionScheduler)
            .cancelAndScheduleJsExecution(jsExecutionDto);
    }
}
