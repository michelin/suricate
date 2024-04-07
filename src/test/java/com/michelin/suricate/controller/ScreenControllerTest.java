package com.michelin.suricate.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.service.api.ProjectService;
import com.michelin.suricate.service.websocket.DashboardWebSocketService;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ScreenControllerTest {
    @Mock
    private ProjectService projectService;

    @Mock
    private DashboardWebSocketService dashboardWebSocketService;

    @InjectMocks
    private ScreenController screenController;

    @Test
    void shouldConnectProjectToScreenNotFound() {
        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> screenController.connectProjectToScreen("token", "code"))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldConnectProjectToScreen() {
        Project project = new Project();
        project.setId(1L);

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));

        ResponseEntity<Void> actual = screenController.connectProjectToScreen("token", "code");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldDisconnectProjectFromScreen() {
        ResponseEntity<Void> actual = screenController.disconnectProjectFromScreen("token", "code");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldRefreshEveryConnectedScreensForProject() {
        ResponseEntity<Void> actual = screenController.refreshEveryConnectedScreensForProject("token");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldDisplayScreenCodeEveryConnectedScreensForProjectNotFound() {
        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> screenController.displayScreenCodeEveryConnectedScreensForProject("token"))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldDisplayScreenCodeEveryConnectedScreensForProject() {
        Project project = new Project();
        project.setId(1L);

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));

        ResponseEntity<Void> actual = screenController.displayScreenCodeEveryConnectedScreensForProject("token");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldGetConnectedScreensQuantity() {
        when(dashboardWebSocketService.countWebsocketClients())
            .thenReturn(15);

        ResponseEntity<Integer> actual = screenController.getConnectedScreensQuantity();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(15);
    }
}
