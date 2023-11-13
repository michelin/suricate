package com.michelin.suricate.services.websocket;

import static com.michelin.suricate.model.enums.UpdateType.CONNECT_DASHBOARD;
import static com.michelin.suricate.model.enums.UpdateType.DISCONNECT;
import static com.michelin.suricate.model.enums.UpdateType.RELOAD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.project.ProjectResponseDto;
import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.websocket.UpdateEvent;
import com.michelin.suricate.model.dto.websocket.WebsocketClient;
import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.services.api.ProjectService;
import com.michelin.suricate.services.js.scheduler.JsExecutionScheduler;
import com.michelin.suricate.services.js.services.JsExecutionService;
import com.michelin.suricate.services.mapper.ProjectMapper;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class DashboardWebSocketServiceTest {
    @Mock
    private JsExecutionScheduler jsExecutionScheduler;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private JsExecutionService jsExecutionService;

    @InjectMocks
    private DashboardWebSocketService dashboardWebSocketService;

    @Test
    void shouldSendConnectProjectEventToScreenSubscriber() {
        Project project = new Project();
        project.setId(1L);

        ProjectResponseDto projectResponseDto = new ProjectResponseDto();
        projectResponseDto.setName("name");

        when(projectMapper.toProjectDto(any())).thenReturn(projectResponseDto);

        dashboardWebSocketService.sendConnectProjectEventToScreenSubscriber(project, "screenCode");

        verify(projectMapper)
            .toProjectDto(project);
        verify(simpMessagingTemplate)
            .convertAndSendToUser(eq("screenCode"), eq("/queue/connect"),
                argThat(updateEvent -> ((UpdateEvent) updateEvent).getType().equals(CONNECT_DASHBOARD)
                    && ((UpdateEvent) updateEvent).getContent().equals(projectResponseDto)));
    }

    @Test
    void shouldSendEventToWidgetInstanceSubscribers() {
        UpdateEvent updateEvent = new UpdateEvent();
        updateEvent.setDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        updateEvent.setContent("test");
        updateEvent.setType(CONNECT_DASHBOARD);

        dashboardWebSocketService.sendEventToWidgetInstanceSubscribers("token", 1L, updateEvent);

        verify(simpMessagingTemplate)
            .convertAndSendToUser("token-projectWidget-1", "/queue/live", updateEvent);
    }

    @Test
    void shouldNotSendEventToWidgetInstanceSubscribersBecauseTokenNull() {
        UpdateEvent updateEvent = new UpdateEvent();
        updateEvent.setDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        updateEvent.setContent("test");
        updateEvent.setType(CONNECT_DASHBOARD);

        dashboardWebSocketService.sendEventToWidgetInstanceSubscribers(null, 1L, updateEvent);

        verify(simpMessagingTemplate, times(0))
            .convertAndSendToUser(any(), any(), any());
    }

    @Test
    void shouldNotSendEventToWidgetInstanceSubscribersBecauseProjectWidgetNull() {
        UpdateEvent updateEvent = new UpdateEvent();
        updateEvent.setDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        updateEvent.setContent("test");
        updateEvent.setType(CONNECT_DASHBOARD);

        dashboardWebSocketService.sendEventToWidgetInstanceSubscribers("token", null, updateEvent);

        verify(simpMessagingTemplate, times(0))
            .convertAndSendToUser(any(), any(), any());
    }

    @Test
    void shouldSendEventToProjectSubscribers() {
        UpdateEvent updateEvent = new UpdateEvent();
        updateEvent.setDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        updateEvent.setContent("test");
        updateEvent.setType(CONNECT_DASHBOARD);

        dashboardWebSocketService.sendEventToProjectSubscribers("token", updateEvent);

        verify(simpMessagingTemplate)
            .convertAndSendToUser("token", "/queue/live", updateEvent);
    }

    @Test
    void shouldNotSendEventToProjectSubscribersBecauseTokenNull() {
        UpdateEvent updateEvent = new UpdateEvent();
        updateEvent.setDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        updateEvent.setContent("test");
        updateEvent.setType(CONNECT_DASHBOARD);

        dashboardWebSocketService.sendEventToProjectSubscribers(null, updateEvent);

        verify(simpMessagingTemplate, times(0))
            .convertAndSendToUser(any(), any(), any());
    }

    @Test
    void shouldAddClientToProjectAndRefreshFirstClient() {
        WebsocketClient websocketClient = new WebsocketClient();
        websocketClient.setProjectToken("token");

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        List<JsExecutionDto> jsExecutionDtos = Collections.singletonList(jsExecutionDto);

        when(jsExecutionService.getJsExecutionsByProject(any())).thenReturn(jsExecutionDtos);

        dashboardWebSocketService.addClientToProject(project, websocketClient);
        dashboardWebSocketService.addClientToProject(project, websocketClient);
        List<WebsocketClient> actual = dashboardWebSocketService.getWebsocketClientsByProjectToken("token");

        assertThat(actual).contains(websocketClient);

        verify(jsExecutionService)
            .getJsExecutionsByProject(project);
        verify(jsExecutionScheduler)
            .scheduleJsRequests(jsExecutionDtos, true);
    }

    @Test
    void shouldGetWebsocketClientsBySessionId() {
        WebsocketClient websocketClient = new WebsocketClient();
        websocketClient.setProjectToken("token");
        websocketClient.setSessionId("session");

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        List<JsExecutionDto> jsExecutionDtos = Collections.singletonList(jsExecutionDto);

        when(jsExecutionService.getJsExecutionsByProject(any())).thenReturn(jsExecutionDtos);

        dashboardWebSocketService.addClientToProject(project, websocketClient);
        Optional<WebsocketClient> actual = dashboardWebSocketService.getWebsocketClientsBySessionId("session");

        assertThat(actual).contains(websocketClient);

        verify(jsExecutionService)
            .getJsExecutionsByProject(project);
        verify(jsExecutionScheduler)
            .scheduleJsRequests(jsExecutionDtos, true);
    }

    @Test
    void shouldCountWebsocketClients() {
        WebsocketClient websocketClient = new WebsocketClient();
        websocketClient.setProjectToken("token");
        websocketClient.setSessionId("session");

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        List<JsExecutionDto> jsExecutionDtos = Collections.singletonList(jsExecutionDto);

        when(jsExecutionService.getJsExecutionsByProject(any())).thenReturn(jsExecutionDtos);

        dashboardWebSocketService.addClientToProject(project, websocketClient);
        int actual = dashboardWebSocketService.countWebsocketClients();

        assertThat(actual).isEqualTo(1);

        verify(jsExecutionService)
            .getJsExecutionsByProject(project);
        verify(jsExecutionScheduler)
            .scheduleJsRequests(jsExecutionDtos, true);
    }

    @Test
    void shouldGetWebsocketClientsBySessionIdAndSubscriptionId() {
        WebsocketClient websocketClient = new WebsocketClient();
        websocketClient.setProjectToken("token");
        websocketClient.setSessionId("session");
        websocketClient.setSubscriptionId("subscription");

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        List<JsExecutionDto> jsExecutionDtos = Collections.singletonList(jsExecutionDto);

        when(jsExecutionService.getJsExecutionsByProject(any())).thenReturn(jsExecutionDtos);

        dashboardWebSocketService.addClientToProject(project, websocketClient);
        Optional<WebsocketClient> actual =
            dashboardWebSocketService.getWebsocketClientsBySessionIdAndSubscriptionId("session", "subscription");

        assertThat(actual).contains(websocketClient);
    }

    @Test
    void shouldNotGetWebsocketClientsBySessionIdAndSubscriptionIdWhenNotfound() {
        WebsocketClient websocketClient = new WebsocketClient();
        websocketClient.setProjectToken("token");
        websocketClient.setSessionId("session");
        websocketClient.setSubscriptionId("subscription");

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        List<JsExecutionDto> jsExecutionDtos = Collections.singletonList(jsExecutionDto);

        when(jsExecutionService.getJsExecutionsByProject(any())).thenReturn(jsExecutionDtos);

        dashboardWebSocketService.addClientToProject(project, websocketClient);
        Optional<WebsocketClient> actual = dashboardWebSocketService
            .getWebsocketClientsBySessionIdAndSubscriptionId("unknownSession", "unknownSubscription");

        assertThat(actual).isEmpty();

        actual = dashboardWebSocketService
            .getWebsocketClientsBySessionIdAndSubscriptionId("session", "unknownSubscription");

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldRemoveClientAndCancelTask() {
        WebsocketClient websocketClient = new WebsocketClient();
        websocketClient.setProjectToken("token");
        websocketClient.setSessionId("session");
        websocketClient.setSubscriptionId("subscription");

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        List<JsExecutionDto> jsExecutionDtos = Collections.singletonList(jsExecutionDto);

        when(jsExecutionService.getJsExecutionsByProject(any())).thenReturn(jsExecutionDtos);
        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));

        dashboardWebSocketService.addClientToProject(project, websocketClient);
        List<WebsocketClient> actual = dashboardWebSocketService.getWebsocketClientsByProjectToken("token");
        assertThat(actual).contains(websocketClient);

        dashboardWebSocketService.removeClientFromProject(websocketClient);

        actual = dashboardWebSocketService.getWebsocketClientsByProjectToken("token");
        assertThat(actual).isEmpty();

        verify(jsExecutionService)
            .getJsExecutionsByProject(project);
        verify(jsExecutionScheduler)
            .scheduleJsRequests(jsExecutionDtos, true);
        verify(projectService)
            .getOneByToken("token");
        verify(jsExecutionScheduler)
            .cancelWidgetsExecutionByProject(project);
    }

    @Test
    void shouldRemoveClientAndNotCancelTask() {
        WebsocketClient websocketClient = new WebsocketClient();
        websocketClient.setProjectToken("token");
        websocketClient.setSessionId("session");
        websocketClient.setSubscriptionId("subscription");

        WebsocketClient websocketClient2 = new WebsocketClient();
        websocketClient2.setProjectToken("token2");
        websocketClient2.setSessionId("session2");
        websocketClient2.setSubscriptionId("subscription2");

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        List<JsExecutionDto> jsExecutionDtos = Collections.singletonList(jsExecutionDto);

        when(jsExecutionService.getJsExecutionsByProject(any())).thenReturn(jsExecutionDtos);

        dashboardWebSocketService.addClientToProject(project, websocketClient);
        dashboardWebSocketService.addClientToProject(project, websocketClient2);
        List<WebsocketClient> actual = dashboardWebSocketService.getWebsocketClientsByProjectToken("token");

        assertThat(actual)
            .contains(websocketClient)
            .contains(websocketClient2);

        dashboardWebSocketService.removeClientFromProject(websocketClient);

        actual = dashboardWebSocketService.getWebsocketClientsByProjectToken("token");
        assertThat(actual).contains(websocketClient2);

        verify(jsExecutionService)
            .getJsExecutionsByProject(project);
        verify(jsExecutionScheduler)
            .scheduleJsRequests(jsExecutionDtos, true);
        verify(projectService, times(0))
            .getOneByToken(any());
        verify(jsExecutionScheduler, times(0))
            .cancelWidgetsExecutionByProject(any());
    }

    @Test
    void shouldDisconnectClient() {
        dashboardWebSocketService.disconnectClient("token", "screen");

        verify(simpMessagingTemplate)
            .convertAndSendToUser(eq("token-screen"), eq("/queue/unique"), argThat(updateEvent ->
                ((UpdateEvent) updateEvent).getType().equals(DISCONNECT)));
    }

    @Test
    void shouldReloadAllConnectedClientsToAllProjects() {
        WebsocketClient websocketClient = new WebsocketClient();
        websocketClient.setProjectToken("token");
        websocketClient.setSessionId("session");
        websocketClient.setSubscriptionId("subscription");

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        Project project2 = new Project();
        project2.setId(2L);
        project2.setToken("token2");

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        List<JsExecutionDto> jsExecutionDtos = Collections.singletonList(jsExecutionDto);

        when(jsExecutionService.getJsExecutionsByProject(any())).thenReturn(jsExecutionDtos);

        dashboardWebSocketService.addClientToProject(project, websocketClient);
        dashboardWebSocketService.reloadAllConnectedClientsToAllProjects();

        verify(jsExecutionService)
            .getJsExecutionsByProject(project);
        verify(jsExecutionScheduler)
            .scheduleJsRequests(jsExecutionDtos, true);
        verify(simpMessagingTemplate)
            .convertAndSendToUser(eq("token"), eq("/queue/live"), argThat(updateEvent ->
                ((UpdateEvent) updateEvent).getType().equals(RELOAD)));
    }

    @Test
    void shouldNotReloadAllConnectedClientsToProjectWhenEmpty() {
        dashboardWebSocketService.reloadAllConnectedClientsToProject("token");

        verify(simpMessagingTemplate, times(0))
            .convertAndSendToUser(any(), any(), any());
    }
}
