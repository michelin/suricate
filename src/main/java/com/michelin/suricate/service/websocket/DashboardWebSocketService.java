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
package com.michelin.suricate.service.websocket;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.websocket.UpdateEvent;
import com.michelin.suricate.model.dto.websocket.WebsocketClient;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.enumeration.UpdateType;
import com.michelin.suricate.service.api.ProjectService;
import com.michelin.suricate.service.js.JsExecutionService;
import com.michelin.suricate.service.js.scheduler.JsExecutionScheduler;
import com.michelin.suricate.service.mapper.ProjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Dashboard websocket service. */
@Slf4j
@Lazy(false)
@Service
public class DashboardWebSocketService {
    private final Multimap<String, WebsocketClient> websocketClientByProjectToken =
            Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    @Autowired
    private JsExecutionScheduler jsExecutionScheduler;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Lazy
    @Autowired
    private ProjectService projectService;

    @Lazy
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private JsExecutionService jsExecutionService;

    /**
     * Send a connect project event through the associated websocket to the unique subscriber. The path of the websocket
     * contains a screen code so it is unique for each screen (so each subscriber). Used to connect a screen to a
     * dashboard just after the subscriber waits on the screen code waiting screen.
     *
     * @param project The project
     * @param screenCode The unique screen code
     */
    public void sendConnectProjectEventToScreenSubscriber(final Project project, final String screenCode) {
        UpdateEvent updateEvent = UpdateEvent.builder()
                .type(UpdateType.CONNECT_DASHBOARD)
                .content(projectMapper.toProjectDto(project))
                .build();

        log.debug(
                "Sending the event {} to the screen {}", updateEvent.getType(), screenCode.replaceAll("[\n\r\t]", "_"));

        simpMessagingTemplate.convertAndSendToUser(screenCode, "/queue/connect", updateEvent);
    }

    /**
     * Send an event through the associated websocket to all subscribers. The path of the websocket contains a project
     * token and a project widget ID so it is unique for each project widget. Used to update a widget.
     *
     * @param projectToken The project token
     * @param projectWidgetId The project widget id
     * @param payload The payload content
     */
    @Async
    public void sendEventToWidgetInstanceSubscribers(
            final String projectToken, final Long projectWidgetId, final UpdateEvent payload) {
        log.debug(
                "Sending the event {} for the widget instance {} of the project {}",
                payload.getType(),
                projectWidgetId,
                projectToken);

        if (projectToken == null) {
            log.error("Project token null for payload: {}", payload);
            return;
        }

        if (projectWidgetId == null) {
            log.error("Widget instance ID null for payload: {}", payload);
            return;
        }

        simpMessagingTemplate.convertAndSendToUser(
                projectToken.trim() + "-projectWidget-" + projectWidgetId, "/queue/live", payload);
    }

    /**
     * Send an event through the associated websocket to all subscribers. The path of the websocket contains a project
     * token so it is unique for each project. Used to reload a project, display the screen code number of a project,
     * disconnect all screens from a project or reposition a widget of a project.
     *
     * @param projectToken The project token
     * @param payload The payload content
     */
    @Async
    public void sendEventToProjectSubscribers(String projectToken, UpdateEvent payload) {
        log.debug("Sending the event {} to the project {}", payload.getType(), projectToken);

        if (projectToken == null) {
            log.error("Project token null for payload: {}", payload);
            return;
        }

        simpMessagingTemplate.convertAndSendToUser(projectToken.trim(), "/queue/live", payload);
    }

    /**
     * Add a new link between a project (dashboard) materialized by its projectToken and a client materialized by its
     * WebsocketClient. Triggered when a new subscription to a dashboard is done. If no client is connected to the
     * dashboard already, initialize a Js execution for each widget of the project to refresh them.
     *
     * @param project The connected project
     * @param websocketClient The related websocket client
     */
    public void addClientToProject(final Project project, final WebsocketClient websocketClient) {
        boolean refreshProject = !websocketClientByProjectToken.containsKey(project.getToken());
        websocketClientByProjectToken.put(project.getToken(), websocketClient);

        if (refreshProject) {
            List<JsExecutionDto> jsExecutionDtos = jsExecutionService.getJsExecutionsByProject(project);
            jsExecutionScheduler.scheduleJsRequests(jsExecutionDtos, true);
        }
    }

    /**
     * Get the list of every connected dashboard.
     *
     * @param projectToken The project token used for find every websocket clients
     * @return The list of related websocket clients
     */
    public List<WebsocketClient> getWebsocketClientsByProjectToken(final String projectToken) {
        return new ArrayList<>(websocketClientByProjectToken.get(projectToken));
    }

    /**
     * Get a websocket by session ID.
     *
     * @param sessionId The session ID
     * @return The websocket
     */
    public Optional<WebsocketClient> getWebsocketClientsBySessionId(final String sessionId) {
        return websocketClientByProjectToken.values().stream()
                .filter(websocketClient -> websocketClient.getSessionId().equals(sessionId))
                .findFirst();
    }

    /**
     * Count the number of connected clients.
     *
     * @return The websocket
     */
    public int countWebsocketClients() {
        return websocketClientByProjectToken.values().size();
    }

    /**
     * Get a websocket by session ID and subscription ID.
     *
     * @param sessionId The session ID
     * @param subscriptionId The subscription ID
     * @return The websocket
     */
    public Optional<WebsocketClient> getWebsocketClientsBySessionIdAndSubscriptionId(
            final String sessionId, final String subscriptionId) {
        return websocketClientByProjectToken.values().stream()
                .filter(websocketClient -> websocketClient.getSessionId().equals(sessionId)
                        && websocketClient.getSubscriptionId().equals(subscriptionId))
                .findFirst();
    }

    /**
     * Remove a given websocket from the project/connection map.
     *
     * @param websocketClient The websocket to remove
     */
    public void removeClientFromProject(WebsocketClient websocketClient) {
        websocketClientByProjectToken.remove(websocketClient.getProjectToken(), websocketClient);

        if (!websocketClientByProjectToken.containsKey(websocketClient.getProjectToken())) {
            projectService
                    .getOneByToken(websocketClient.getProjectToken())
                    .ifPresent(jsExecutionScheduler::cancelWidgetsExecutionByProject);
        }
    }

    /**
     * Disconnect screen from project.
     *
     * @param projectToken The project token
     * @param screenCode The screen code
     */
    @Async
    public void disconnectClient(final String projectToken, final String screenCode) {
        UpdateEvent payload = UpdateEvent.builder().type(UpdateType.DISCONNECT).build();

        log.info(
                "Sending the event {} to the project {} of the screen {}",
                payload.getType(),
                projectToken,
                screenCode.replaceAll("[\n\r\t]", "_"));

        simpMessagingTemplate.convertAndSendToUser(projectToken.trim() + "-" + screenCode, "/queue/unique", payload);
    }

    /** Reload all the connected clients to all the projects. */
    public void reloadAllConnectedClientsToAllProjects() {
        websocketClientByProjectToken.forEach((key, value) -> reloadAllConnectedClientsToProject(key));
    }

    /**
     * Method that force the reloading of every connected clients for a project.
     *
     * @param projectToken The project token
     */
    public void reloadAllConnectedClientsToProject(final String projectToken) {
        if (!websocketClientByProjectToken.get(projectToken).isEmpty()) {
            sendEventToProjectSubscribers(
                    projectToken, UpdateEvent.builder().type(UpdateType.RELOAD).build());
        }
    }
}
