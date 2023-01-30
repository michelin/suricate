package com.michelin.suricate.services.nashorn.services;

import com.michelin.suricate.services.api.ProjectService;
import com.michelin.suricate.services.api.ProjectWidgetService;
import com.michelin.suricate.services.mapper.ProjectWidgetMapper;
import com.michelin.suricate.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import com.michelin.suricate.services.websocket.DashboardWebSocketService;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import com.michelin.suricate.model.dto.nashorn.NashornRequest;
import com.michelin.suricate.model.dto.nashorn.NashornResponse;
import com.michelin.suricate.model.entities.ProjectWidget;
import com.michelin.suricate.model.enums.NashornErrorTypeEnum;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static com.michelin.suricate.model.enums.UpdateType.REFRESH_WIDGET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardScheduleServiceTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private NashornRequestWidgetExecutionScheduler nashornRequestWidgetExecutionScheduler;

    @Mock
    private DashboardWebSocketService dashboardWebSocketService;

    @Mock
    private ProjectWidgetService projectWidgetService;

    @Mock
    private ProjectWidgetMapper projectWidgetMapper;

    @Mock
    private NashornService nashornService;

    @Mock
    private ProjectService projectService;

    @Mock
    private NashornRequestWidgetExecutionScheduler nashornWidgetScheduler;

    @InjectMocks
    private DashboardScheduleService dashboardScheduleService;

    @Test
    void shouldProcessValidNashornResponse() {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);

        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);
        nashornResponse.setProjectWidgetId(1L);
        nashornResponse.setData("{}");
        nashornResponse.setLog("log");
        nashornResponse.setLaunchDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        doNothing().when(projectWidgetService)
                .updateWidgetInstanceAfterSucceededExecution(any(), any(), any(), any(), any());
        when(nashornService.getNashornRequestByProjectWidgetId(any())).thenReturn(nashornRequest);
        doNothing().when(nashornWidgetScheduler)
                .schedule(any(), anyBoolean());
        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDTO(any())).thenReturn(projectWidgetResponseDto);
        when(projectService.getTokenByProjectId(any())).thenReturn("token");
        doNothing().when(dashboardWebSocketService)
                .sendEventToWidgetInstanceSubscribers(any(), any(), any());

        dashboardScheduleService.processNashornResponse(nashornResponse, nashornWidgetScheduler);

        verify(projectWidgetService, times(1))
                .updateWidgetInstanceAfterSucceededExecution(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")),
                        "log", "{}", 1L, WidgetStateEnum.RUNNING);
        verify(nashornService, times(1))
                .getNashornRequestByProjectWidgetId(1L);
        verify(nashornWidgetScheduler, times(1))
                .schedule(nashornRequest, false);
        verify(projectWidgetService, times(1))
                .getOne(1L);
        verify(projectWidgetMapper, times(1))
                .toProjectWidgetDTO(projectWidget);
        verify(projectService, times(1))
                .getTokenByProjectId(1L);
        verify(dashboardWebSocketService, times(1))
                .sendEventToWidgetInstanceSubscribers(eq("token"), eq(1L), argThat(event ->
                        event.getType().equals(REFRESH_WIDGET) && event.getContent().equals(projectWidgetResponseDto)));
    }

    @Test
    void shouldProcessErrorNashornResponse() {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);

        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);
        nashornResponse.setProjectWidgetId(1L);
        nashornResponse.setData("{}");
        nashornResponse.setLog("log");
        nashornResponse.setError(NashornErrorTypeEnum.ERROR);
        nashornResponse.setLaunchDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        doNothing().when(projectWidgetService)
                .updateWidgetInstanceAfterFailedExecution(any(), any(), any(), any());
        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDTO(any())).thenReturn(projectWidgetResponseDto);
        when(projectService.getTokenByProjectId(any())).thenReturn("token");
        doNothing().when(dashboardWebSocketService)
                .sendEventToWidgetInstanceSubscribers(any(), any(), any());

        dashboardScheduleService.processNashornResponse(nashornResponse, nashornWidgetScheduler);

        verify(projectWidgetService, times(1))
                .updateWidgetInstanceAfterFailedExecution(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")),
                        "log", 1L, WidgetStateEnum.WARNING);
        verify(projectWidgetService, times(1))
                .getOne(1L);
        verify(projectWidgetMapper, times(1))
                .toProjectWidgetDTO(projectWidget);
        verify(projectService, times(1))
                .getTokenByProjectId(1L);
        verify(dashboardWebSocketService, times(1))
                .sendEventToWidgetInstanceSubscribers(eq("token"), eq(1L), argThat(event ->
                        event.getType().equals(REFRESH_WIDGET) && event.getContent().equals(projectWidgetResponseDto)));
    }

    @Test
    void shouldProcessFatalNashornResponse() {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);

        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);
        nashornResponse.setProjectWidgetId(1L);
        nashornResponse.setData("{}");
        nashornResponse.setLog("log");
        nashornResponse.setError(NashornErrorTypeEnum.FATAL);
        nashornResponse.setLaunchDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        doNothing().when(projectWidgetService)
                .updateWidgetInstanceAfterFailedExecution(any(), any(), any(), any());
        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDTO(any())).thenReturn(projectWidgetResponseDto);
        when(projectService.getTokenByProjectId(any())).thenReturn("token");
        doNothing().when(dashboardWebSocketService)
                .sendEventToWidgetInstanceSubscribers(any(), any(), any());

        dashboardScheduleService.processNashornResponse(nashornResponse, nashornWidgetScheduler);

        verify(projectWidgetService, times(1))
                .updateWidgetInstanceAfterFailedExecution(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")),
                        "log", 1L, WidgetStateEnum.STOPPED);
        verify(projectWidgetService, times(1))
                .getOne(1L);
        verify(projectWidgetMapper, times(1))
                .toProjectWidgetDTO(projectWidget);
        verify(projectService, times(1))
                .getTokenByProjectId(1L);
        verify(dashboardWebSocketService, times(1))
                .sendEventToWidgetInstanceSubscribers(eq("token"), eq(1L), argThat(event ->
                        event.getType().equals(REFRESH_WIDGET) && event.getContent().equals(projectWidgetResponseDto)));
    }

    @Test
    void shouldUpdateWidgetInstanceNoNashornResponse() {
        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        doNothing().when(projectWidgetService)
                .updateWidgetInstanceAfterFailedExecution(any(), any(), any(), any());
        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDTO(any())).thenReturn(projectWidgetResponseDto);
        when(projectService.getTokenByProjectId(any())).thenReturn("token");
        doNothing().when(dashboardWebSocketService)
                .sendEventToWidgetInstanceSubscribers(any(), any(), any());

        dashboardScheduleService.updateWidgetInstanceNoNashornResponse("logs", 1L, 1L);

        verify(projectWidgetService, times(1))
                .updateWidgetInstanceAfterFailedExecution(any(),
                        eq("logs"), eq(1L), eq(WidgetStateEnum.STOPPED));
        verify(projectWidgetService, times(1))
                .getOne(1L);
        verify(projectWidgetMapper, times(1))
                .toProjectWidgetDTO(projectWidget);
        verify(projectService, times(1))
                .getTokenByProjectId(1L);
        verify(dashboardWebSocketService, times(1))
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
        when(projectWidgetMapper.toProjectWidgetDTO(any())).thenReturn(projectWidgetResponseDto);
        when(projectService.getTokenByProjectId(any())).thenReturn("token");
        doNothing().when(dashboardWebSocketService)
                .sendEventToWidgetInstanceSubscribers(any(), any(), any());

        dashboardScheduleService.sendWidgetUpdateNotification(1L, 1L);

        verify(projectWidgetService, times(1))
                .getOne(1L);
        verify(projectWidgetMapper, times(1))
                .toProjectWidgetDTO(projectWidget);
        verify(projectService, times(1))
                .getTokenByProjectId(1L);
        verify(dashboardWebSocketService, times(1))
                .sendEventToWidgetInstanceSubscribers(eq("token"), eq(1L), argThat(event ->
                        event.getType().equals(REFRESH_WIDGET) && event.getContent().equals(projectWidgetResponseDto)));
    }

    @Test
    void shouldScheduleWidget() {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);

        when(nashornService.getNashornRequestByProjectWidgetId(any())).thenReturn(nashornRequest);
        when(applicationContext.getBean(NashornRequestWidgetExecutionScheduler.class))
                .thenReturn(nashornRequestWidgetExecutionScheduler);
        doNothing().when(nashornRequestWidgetExecutionScheduler)
                .cancelAndScheduleNashornRequest(any());

        dashboardScheduleService.scheduleWidget(1L);

        verify(nashornService, times(1))
                .getNashornRequestByProjectWidgetId(1L);
        verify(nashornRequestWidgetExecutionScheduler, times(1))
                .cancelAndScheduleNashornRequest(nashornRequest);
    }
}
