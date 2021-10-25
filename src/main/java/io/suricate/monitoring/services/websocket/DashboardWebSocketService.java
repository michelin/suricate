/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.services.websocket;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.mapper.ProjectMapper;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.nashorn.services.NashornService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manage the dashboards messaging through websockets
 */
@Lazy(false)
@Service
public class DashboardWebSocketService {
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardWebSocketService.class);

    /**
     * The scheduler scheduling the widget execution through Nashorn
     */
    private final NashornRequestWidgetExecutionScheduler nashornWidgetScheduler;

    /**
     * Save all websocket clients by project token.
     * 
     * Represents all the connected screens to a project
     */
    private final Multimap<String, WebsocketClient> websocketClientByProjectToken = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    /**
     * The stomp websocket message template
     */
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * The project service
     */
    private final ProjectService projectService;

    /**
     * The project mapper
     */
    private final ProjectMapper projectMapper;

    /**
     * The nashorn service
     */
    private final NashornService nashornService;

    /**
     * Constructor
     *
     * @param simpMessagingTemplate  message template used for send messages through stomp websockets
     * @param projectService         The project service
     * @param projectMapper          The project mapper
     * @param nashornService         The nashorn service
     * @param nashornWidgetScheduler The nashorn scheduler
     */
    @Autowired
    public DashboardWebSocketService(final SimpMessagingTemplate simpMessagingTemplate,
                                     @Lazy final ProjectService projectService,
                                     @Lazy final ProjectMapper projectMapper,
                                     final NashornService nashornService,
                                     final NashornRequestWidgetExecutionScheduler nashornWidgetScheduler) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.nashornService = nashornService;
        this.nashornWidgetScheduler = nashornWidgetScheduler;
    }

    /**
     * Send a connect project event through the associated websocket to the unique subscriber.
     * The path of the websocket contains a screen code so it is unique for each
     * screen (so each subscriber).
     *
     * Used to connect a screen to a dashboard just after the subscriber waits on the screen code
     * waiting screen.
     *
     * @param project The project
     * @param screenCode The unique screen code
     */
    public void sendConnectProjectEventToScreenSubscriber(final Project project, final String screenCode) {
        UpdateEvent updateEvent = UpdateEvent.builder()
                .type(UpdateType.CONNECT_SINGLE_DASHBOARD)
                .content(this.projectMapper.toProjectDTO(project))
                .build();

        LOGGER.debug("Sending the event {} to the screen {}", updateEvent.getType(), screenCode.replaceAll("[\n\r\t]", "_"));

        simpMessagingTemplate.convertAndSendToUser(
                screenCode,
                "/queue/connect",
                updateEvent
        );
    }

    /**
     * Send an event through the associated websocket to all subscribers.
     * The path of the websocket contains a project token and a project widget ID so it is unique for each
     * project widget.
     * Used to update a widget.
     *
     * @param projectToken    The project token
     * @param projectWidgetId The project widget id
     * @param payload         The payload content
     */
    @Async
    public void sendEventToWidgetInstanceSubscribers(final String projectToken, final Long projectWidgetId, final UpdateEvent payload) {
        LOGGER.debug("Sending the event {} for the widget instance {} of the project {}", payload.getType(), projectWidgetId, projectToken);

        if (projectToken == null) {
            LOGGER.error("Project token null for payload: {}", payload);
            return;
        }
        if (projectWidgetId == null) {
            LOGGER.error("Widget instance ID null for payload: {}", payload);
            return;
        }

        simpMessagingTemplate.convertAndSendToUser(
                projectToken.trim() + "-projectWidget-" + projectWidgetId,
                "/queue/live",
                payload
        );
    }

    /**
     * Send an event through the associated websocket to all subscribers.
     * The path of the websocket contains a project token so it is unique for each
     * project.
     *
     * Used to reload a project,
     * display the screen code number of a project,
     * disconnect all screens from a project
     * or reposition a widget of a project.
     *
     * @param projectToken    The project token
     * @param payload         The payload content
     */
    @Async
    public void sendEventToProjectSubscribers(String projectToken, UpdateEvent payload) {
        LOGGER.debug("Sending the event {} to the project {}", payload.getType(), projectToken);

        if (projectToken == null) {
            LOGGER.error("Project token null for payload: {}", payload);
            return;
        }

        simpMessagingTemplate.convertAndSendToUser(
                projectToken.trim(),
                "/queue/live",
                payload
        );
    }

    /**
     * Send an event through the associated websocket to the unique subscriber.
     * The path of the websocket contains a project token and a screen code so it is unique for each
     * screen (so each subscriber).
     *
     * Used to disconnect a given single screen from a dashboard.
     *
     * @param projectToken The project
     * @param screenCode The unique screen code
     */
    @Async
    public void sendEventToScreenProjectSubscriber(String projectToken, String screenCode, UpdateEvent payload) {
        LOGGER.debug("Sending the event {} to the project {} of the screen {}", payload.getType(), projectToken, screenCode);

        simpMessagingTemplate.convertAndSendToUser(
                projectToken.trim() + "-" + screenCode,
                "/queue/unique",
                payload
        );
    }

    /**
     * Add a new link between a project (dashboard) materialized by its projectToken
     * and a client materialized by its WebsocketClient.
     * Triggered when a new subscription to a dashboard is done.
     *
     * Initialize a Nashorn request for each widget of the project.
     * Schedule the Nashorn requests execution.
     *
     * @param project         The connected project
     * @param websocketClient The related websocket client
     */
    public void addClientToProject(final Project project, final WebsocketClient websocketClient) {
        boolean refreshProject = !websocketClientByProjectToken.containsKey(project.getToken());
        websocketClientByProjectToken.put(project.getToken(), websocketClient);

        if (refreshProject) {
            List<NashornRequest> nashornRequests = nashornService.getNashornRequestsByProject(project);
            nashornWidgetScheduler.scheduleNashornRequests(nashornRequests, true);
        }
    }

    /**
     * Get the list of every connected dashboard
     *
     * @param projectToken The project token used for find every websocket clients
     * @return The list of related websocket clients
     */
    public List<WebsocketClient> getWebsocketClientsByProjectToken(final String projectToken) {
        return new ArrayList<>(websocketClientByProjectToken.get(projectToken));
    }

    /**
     * Get a websocket by session ID
     *
     * @param sessionId The session ID
     * @return The websocket
     */
    public Optional<WebsocketClient> getWebsocketClientsBySessionId(final String sessionId) {
        return this.websocketClientByProjectToken.values()
                .stream()
                .filter(websocketClient -> websocketClient.getSessionId().equals(sessionId))
                .findFirst();
    }

    /**
     * Get a websocket by session ID and subscription ID
     *
     * @param sessionId The session ID
     * @param subscriptionId The subscription ID
     * @return The websocket
     */
    public Optional<WebsocketClient> getWebsocketClientsBySessionIdAndSubscriptionId(final String sessionId, final String subscriptionId) {
        return this.websocketClientByProjectToken.values()
                .stream()
                .filter(websocketClient -> websocketClient.getSessionId().equals(sessionId) &&
                        websocketClient.getSubscriptionId().equals(subscriptionId))
                .findFirst();
    }

    /**
     * Remove a given websocket from the project/connection map
     *
     * @param websocketClient The websocket to remove
     */
    public void removeClientFromProject(WebsocketClient websocketClient) {
        this.websocketClientByProjectToken.remove(websocketClient.getProjectToken(), websocketClient);

        if (!this.websocketClientByProjectToken.containsKey(websocketClient.getProjectToken())) {
            this.projectService.getOneByToken(websocketClient.getProjectToken())
                    .ifPresent(this.nashornWidgetScheduler::cancelWidgetsExecutionByProject);
        }
    }

    /**
     * Method used for updates by project id every screens connected to this project
     *
     * @param projectId the project id
     * @param payload   the payload content
     */
    public void updateGlobalScreensByProjectId(Long projectId, UpdateEvent payload) {
        this.sendEventToProjectSubscribers(projectService.getTokenByProjectId(projectId), payload);
    }

    /**
     * Disconnect screen from project
     *
     * @param projectToken The project token
     * @param screenCode   The screen code
     */
    public void disconnectClient(final String projectToken, final String screenCode) {
        this.sendEventToScreenProjectSubscriber(projectToken, screenCode, UpdateEvent.builder().type(UpdateType.DISCONNECT).build());
    }

    /**
     * Reload all the connected clients to all the projects
     */
    public void reloadAllConnectedClientsToAllProjects() {
        this.websocketClientByProjectToken.forEach((key, value) ->
                reloadAllConnectedClientsToAProject(key));
    }

    /**
     * Method that force the reload of every connected clients for a project
     *
     * @param projectToken The project token
     */
    public void reloadAllConnectedClientsToAProject(final String projectToken) {
        if (!this.websocketClientByProjectToken.get(projectToken).isEmpty()) {
            this.sendEventToProjectSubscribers(projectToken, UpdateEvent.builder().type(UpdateType.RELOAD).build());
        }
    }
}
