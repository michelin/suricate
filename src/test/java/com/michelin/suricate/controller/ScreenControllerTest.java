package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> screenController.connectProjectToScreen("token", "code")
        );

        assertEquals("Project 'token' not found", exception.getMessage());
    }

    @Test
    void shouldConnectProjectToScreen() {
        Project project = new Project();
        project.setId(1L);

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));

        ResponseEntity<Void> actual = screenController.connectProjectToScreen("token", "code");

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldDisconnectProjectFromScreen() {
        ResponseEntity<Void> actual = screenController.disconnectProjectFromScreen("token", "code");

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldRefreshEveryConnectedScreensForProject() {
        ResponseEntity<Void> actual = screenController.refreshEveryConnectedScreensForProject("token");

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldDisplayScreenCodeEveryConnectedScreensForProjectNotFound() {
        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> screenController.displayScreenCodeEveryConnectedScreensForProject("token")
        );

        assertEquals("Project 'token' not found", exception.getMessage());
    }

    @Test
    void shouldDisplayScreenCodeEveryConnectedScreensForProject() {
        Project project = new Project();
        project.setId(1L);

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));

        ResponseEntity<Void> actual = screenController.displayScreenCodeEveryConnectedScreensForProject("token");

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldGetConnectedScreensQuantity() {
        when(dashboardWebSocketService.countWebsocketClients())
            .thenReturn(15);

        ResponseEntity<Integer> actual = screenController.getConnectedScreensQuantity();

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(15, actual.getBody());
    }
}
