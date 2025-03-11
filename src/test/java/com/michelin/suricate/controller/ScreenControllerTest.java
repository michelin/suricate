/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
        when(projectService.getOneByToken(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class, () -> screenController.connectProjectToScreen("token", "code"));

        assertEquals("Project 'token' not found", exception.getMessage());
    }

    @Test
    void shouldConnectProjectToScreen() {
        Project project = new Project();
        project.setId(1L);

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));

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
        when(projectService.getOneByToken(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> screenController.displayScreenCodeEveryConnectedScreensForProject("token"));

        assertEquals("Project 'token' not found", exception.getMessage());
    }

    @Test
    void shouldDisplayScreenCodeEveryConnectedScreensForProject() {
        Project project = new Project();
        project.setId(1L);

        when(projectService.getOneByToken(any())).thenReturn(Optional.of(project));

        ResponseEntity<Void> actual = screenController.displayScreenCodeEveryConnectedScreensForProject("token");

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldGetConnectedScreensQuantity() {
        when(dashboardWebSocketService.countWebsocketClients()).thenReturn(15);

        ResponseEntity<Integer> actual = screenController.getConnectedScreensQuantity();

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(15, actual.getBody());
    }
}
