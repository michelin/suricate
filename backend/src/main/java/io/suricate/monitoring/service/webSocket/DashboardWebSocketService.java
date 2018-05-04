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

package io.suricate.monitoring.service.webSocket;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.service.api.ProjectService;
import io.suricate.monitoring.service.nashorn.NashornService;
import io.suricate.monitoring.service.scheduler.NashornWidgetScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
     * The stomp websocket message template
     */
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * The project service
     */
    private final ProjectService projectService;

    /**
     * The nashorn service
     */
    private final NashornService nashornService;

    /**
     * The nashorn widget Scheduler
     */
    private final NashornWidgetScheduler nashornWidgetScheduler;

    /**
     * MultiMap containing the projectToken as Key and the list of the WebsocketClient connected as value
     */
    private Multimap<String, WebsocketClient> projectClients = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    /**
     * SynchronizedMap containing the websocket session ID as key and the related WebsocketClient
     */
    private Map<String, WebsocketClient> sessionClient = Collections.synchronizedMap(new HashMap<>());

    /**
     * Constructor
     *
     * @param simpMessagingTemplate message template used for send messages through stomp websockets
     * @param projectService The project service
     */
    @Autowired
    public DashboardWebSocketService(final SimpMessagingTemplate simpMessagingTemplate,
                                     @Lazy final ProjectService projectService,
                                     final NashornService nashornService,
                                     final NashornWidgetScheduler nashornWidgetScheduler) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.projectService = projectService;
        this.nashornService = nashornService;
        this.nashornWidgetScheduler = nashornWidgetScheduler;
    }

    /**
     * Add a new link between the projectToken and a WebsocketClient
     * Used when a new Websocket connection is done
     *
     * @param projectToken The connected projectToken
     * @param websocketClient The related websocket client
     */
    @Transactional
    public void addProjectClient (final String projectToken, final WebsocketClient websocketClient) {
        boolean shouldInstantiateProject = !projectClients.containsKey(projectToken);
        projectClients.put(projectToken, websocketClient);

        if(shouldInstantiateProject) {
            Optional<Project> projectOpt = projectService.getOneByToken(projectToken);

            if(projectOpt.isPresent()) {
                List<NashornRequest> nashornRequest = nashornService.getNashornRequestsByProject(projectOpt.get());
                nashornWidgetScheduler.scheduleList(nashornRequest, true, true);
            }
        }
    }

    /**
     * Remove a link between a projectToken and WebsocketClient
     *
     * @param projectToken The projectToken
     * @param websocketClient The websocket client
     */
    @Transactional
    public void removeProjectClient (final String projectToken, final WebsocketClient websocketClient) {
        projectClients.remove(projectToken, websocketClient);

        if(projectClients == null || !projectClients.containsKey(projectToken)) {
            projectService
                .getOneByToken(projectToken)
                .ifPresent(nashornWidgetScheduler::cancelProjectScheduling);
        }
    }

    /**
     * Add new link between the websocket session ID and a WebsocketClient
     *
     * @param websocketSessionId The websocket session id
     * @param websocketClient The related websocket client
     */
    public void addSessionClient (final String websocketSessionId, final WebsocketClient websocketClient) {
        sessionClient.put(websocketSessionId, websocketClient);
    }

    /**
     * Remove a websocket session from the map
     *
     * @param websocketSessionId The websocket session to remove
     * @return The websocket session removed
     */
    public WebsocketClient removeSessionClient (final String websocketSessionId) {
        return sessionClient.remove(websocketSessionId);
    }

    /**
     * Send notification to users subscribed on channel "/user/queue/notify".
     *
     * @param clientId the client ID to notify
     * @param payload the object to send to the client
     */
    @Async
    public void notifyRegister(String clientId, Object payload) {
        LOGGER.debug("Register screen {}", clientId);
        simpMessagingTemplate.convertAndSendToUser(
                clientId.trim(),
                "/queue/register",
                payload
        );
    }

    /**
     * Method used for updates by project id every screens connected to this project
     *
     * @param projectId the project id
     * @param payload the payload content
     */
    public void updateGlobalScreensByProjectId(Long projectId, Object payload) {
        updateGlobalScreensByProjectToken(projectService.getTokenByProjectId(projectId), payload);
    }

    /**
     * Method used for updates by project token every screens connected to this project
     *
     * @param projectToken the project token
     * @param payload the payload content
     */
    @Async
    public void updateGlobalScreensByProjectToken(String projectToken, Object payload) {
        LOGGER.debug("Update project's screen {}", projectToken);
        LOGGER.trace("Update project's screen {}, data: {}", projectToken, payload);

        if (projectToken == null){
            LOGGER.error("Project token not found for payload: {}", payload);
            return;
        }

        simpMessagingTemplate.convertAndSendToUser(
                projectToken.trim(),
                "/queue/live",
                payload
        );
    }


    /**
     * Method used to update unique screen by project token and screen code
     *
     * @param projectToken project token
     * @param userId user id
     * @param payload data to send
     */
    @Async
    public void updateUniqueScreen(String projectToken, String userId, Object payload) {
        LOGGER.debug("screen unique");
        LOGGER.debug("Update project's screen {} for user {}, data: {}", projectToken, userId, payload);

        simpMessagingTemplate.convertAndSendToUser(
                projectToken.trim()+"-"+userId,
                "/queue/unique",
                payload
        );
    }

    /**
     * Get client number on a specified projectId
     *
     * @param projectId the project iD
     * @return the number of client for the specified client ID
     */
    public int getClientNumber(String projectId) {
        return projectClients.get(projectId).size();
    }

    /**
     * Get client on a specified projectId
     *
     * @param projectId the project iD
     * @return the list of client
     */
    public Collection<WebsocketClient> getClient(String projectId) {
        return projectClients.get(projectId);
    }


    /**
     * Force dashboard to display client Id
     *
     * @param projectId the specified project Id
     */
    public void displayUniqueNumber(String projectId) {
        Iterator<WebsocketClient> it = projectClients.values().iterator();
        while (it.hasNext()) {
            updateUniqueScreen(projectId, it.next().getScreenCode(), new UpdateEvent(UpdateType.DISPLAY_NUMBER));
        }
    }

    /**
     * Disconnect screen from project
     *
     * @param websocketClient the websocketClient to disconnect
     */
    public void disconnectClient(WebsocketClient websocketClient) {
        updateUniqueScreen(websocketClient.getProjectToken(), websocketClient.getScreenCode(), new UpdateEvent(UpdateType.DISCONNECT));
    }

    /**
     * Method used to force reload all connected client
     */
    public void reloadAllConnectedDashboard(){
        Iterator<String> it = projectClients.keySet().iterator();
        while (it.hasNext()) {
            updateGlobalScreensByProjectToken(it.next(), new UpdateEvent(UpdateType.GRID));
        }
    }
}
