/*
 * Copyright 2012-2018 the original author or authors.
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
import io.suricate.monitoring.services.nashorn.services.NashornService;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
     * The multimap containing all the connected web socket clients to a project token
     */
    private Multimap<String, WebsocketClient> clientsByProjectToken = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    /**
     * SynchronizedMap containing the websocket session ID as key and the related WebsocketClient
     */
    private Map<String, WebsocketClient> sessionClient = Collections.synchronizedMap(new HashMap<>());

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
    public void sendEventToScreenProjectSubscriber(String projectToken, int screenCode, UpdateEvent payload) {
        LOGGER.debug("Sending the event {} to the project {} of the screen {}", payload.getType(), projectToken, screenCode);

        simpMessagingTemplate.convertAndSendToUser(
                projectToken.trim() + "-" + screenCode,
                "/queue/unique",
                payload
        );
    }

    /**
     * Send a connect event through the associated websocket to the unique subscriber.
     * The path of the websocket contains a screen code so it is unique for each
     * screen (so each subscriber).
     *
     * Used to connect a screen to a dashboard just after the subscriber waits on the screen code
     * waiting screen.
     *
     * @param project The project
     * @param screenCode The unique screen code
     */
    public void sendConnectEventToScreenSubscriber(final Project project, final String screenCode) {
        UpdateEvent updateEvent = new UpdateEvent(UpdateType.CONNECT);
        updateEvent.setContent(projectMapper.toProjectDTO(project));

        LOGGER.debug("Sending the event {} to the screen {}", updateEvent.getType(), screenCode);

        simpMessagingTemplate.convertAndSendToUser(
                screenCode,
                "/queue/connect",
                updateEvent
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
     * @param projectToken    The connected projectToken
     * @param websocketClient The related websocket client
     */
    @Transactional
    public void addClientToProject(final String projectToken, final WebsocketClient websocketClient) {
        boolean refreshProject = !clientsByProjectToken.containsKey(projectToken);
        clientsByProjectToken.put(projectToken, websocketClient);

        if (refreshProject) {
            Optional<Project> project = projectService.getOneByToken(projectToken);

            if (project.isPresent()) {
                List<NashornRequest> nashornRequests = nashornService.getNashornRequestsByProject(project.get());
                nashornWidgetScheduler.scheduleNashornRequests(nashornRequests, true);
            }
        }
    }

    /**
     * Get the list of every connected dashboard
     *
     * @param projectToken The project token used for find every websocket clients
     * @return The list of related websocket clients
     */
    @Transactional
    public List<WebsocketClient> getWebsocketClientsByProjectToken(final String projectToken) {
        return new ArrayList<>(clientsByProjectToken.get(projectToken));
    }

    /**
     * Remove a link between a projectToken and WebsocketClient
     *
     * @param projectToken    The projectToken
     * @param websocketClient The websocket client
     */
    @Transactional
    public void removeProjectClient(final String projectToken, final WebsocketClient websocketClient) {
        clientsByProjectToken.remove(projectToken, websocketClient);

        if (!clientsByProjectToken.containsKey(projectToken)) {
            projectService.getOneByToken(projectToken).ifPresent(nashornWidgetScheduler::cancelWidgetsExecutionByProject);
        }
    }

    /**
     * Add new link between the websocket session ID and a WebsocketClient
     *
     * @param websocketSessionId The websocket session id
     * @param websocketClient    The related websocket client
     */
    public void addSessionClient(final String websocketSessionId, final WebsocketClient websocketClient) {
        if (sessionClient.containsKey(websocketSessionId)) {
            sessionClient.replace(websocketSessionId, websocketClient);
        } else {
            sessionClient.values().stream()
                .filter(wsClient -> wsClient.getScreenCode().equals(websocketClient.getScreenCode()))
                .findAny()
                .ifPresent(sessionClientWithSameScreenCode -> sessionClient.remove(sessionClientWithSameScreenCode.getScreenCode()));

            sessionClient.put(websocketSessionId, websocketClient);
        }
    }

    /**
     * Remove a websocket session from the map
     *
     * @param websocketSessionId      The websocket session to remove
     * @param websocketSubscriptionId The subscription ID related to the unique screen destination
     * @return The websocket session removed
     */
    public WebsocketClient removeSessionClientByWebsocketSessionIdAndSubscriptionId(final String websocketSessionId, final String websocketSubscriptionId) {
        WebsocketClient websocketClient = null;

        if (sessionClient.containsKey(websocketSessionId) &&
            sessionClient.get(websocketSessionId).getSubscriptionId().equals(websocketSubscriptionId)) {
            websocketClient = sessionClient.remove(websocketSessionId);
        }

        return websocketClient;
    }

    /**
     * Remove a websocket session from the map
     *
     * @param websocketSessionId The websocket session to remove
     * @return The websocket session removed
     */
    public WebsocketClient removeSessionClientByWebsocketSessionId(final String websocketSessionId) {
        WebsocketClient websocketClient = null;

        if (sessionClient.containsKey(websocketSessionId)) {
            websocketClient = sessionClient.remove(websocketSessionId);
        }

        return websocketClient;
    }

    /**
     * Method used for updates by project id every screens connected to this project
     *
     * @param projectId the project id
     * @param payload   the payload content
     */
    public void updateGlobalScreensByProjectId(Long projectId, UpdateEvent payload) {
        sendEventToProjectSubscribers(projectService.getTokenByProjectId(projectId), payload);
    }

    /**
     * Force dashboard to display client Id
     *
     * @param projectToken the specified project token
     */
    public void displayScreenCodeForProject(String projectToken) {
        sendEventToProjectSubscribers(projectToken, new UpdateEvent(UpdateType.DISPLAY_NUMBER));
    }

    /**
     * Disconnect screen from project
     *
     * @param projectToken The project token
     * @param screenCode   The screen code
     */
    public void disconnectClient(final String projectToken, final int screenCode) {
        sendEventToScreenProjectSubscriber(projectToken, screenCode, new UpdateEvent(UpdateType.DISCONNECT));
    }

    /**
     * Reload all the connected clients to all the projects
     */
    public void reloadAllConnectedClientsToAllProjects() {
        clientsByProjectToken.forEach((key, value) ->
                sendEventToProjectSubscribers(key, new UpdateEvent(UpdateType.RELOAD)));
    }

    /**
     * Method that force the reload of every connected clients for a project
     *
     * @param projectToken The project token
     */
    public void reloadAllConnectedClientsToAProject(final String projectToken) {
        sendEventToProjectSubscribers(projectToken, new UpdateEvent(UpdateType.RELOAD));
    }
}
