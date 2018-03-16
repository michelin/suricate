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

package io.suricate.monitoring.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.suricate.monitoring.model.dto.Client;
import io.suricate.monitoring.model.dto.UpdateEvent;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Lazy(false)
@Service
public class SocketService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketService.class);

    /**
     * Web socket project id regex group
     */
    private static final int PROJECT_REGEX_GROUP = 1;

    /**
     * Web socket client Id group in regex
     */
    private static final int ID_REGEX_GROUP = 2;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ProjectRepository projectRepository;


    private Multimap<String /* Project ID */,Client> projectClients = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
    private Map<String /* Client session Id */,Client> sessionClient = Collections.synchronizedMap(new HashMap<>());

    /**
     * Method used handle user connection
     * @param event
     */
    @EventListener
    protected void onSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String simpDestination = (String) sha.getHeader("simpDestination");
        if (simpDestination != null) {
            Pattern pattern = Pattern.compile("/user/([A-Z0-9]+)-([0-9]+)/queue/unique");
            Matcher matcher = pattern.matcher(simpDestination);
            if (matcher.find()){
                Client client = new Client(matcher.group(PROJECT_REGEX_GROUP),sha.getSessionId(),matcher.group(ID_REGEX_GROUP));
                LOGGER.debug("New Client {} with id {} for project {}", client.getSessionId() ,client.getId(), client.getProjectId());
                projectClients.put(client.getProjectId(), client);
                sessionClient.put(client.getSessionId(), client);
            }
        }
    }

    /**
     * Method used to handle session disconnect
     * @param event
     */
    @EventListener
    protected void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        Client client = sessionClient.remove(sha.getSessionId());
        if (client != null) {
            projectClients.remove(client.getProjectId(), client);
        }
    }

    /**
     * Send notification to users subscribed on channel "/user/queue/notify".
     * @param clientId the client ID to notify
     * @param payload the object to send to the client
     */
    @Async
    public void notifyRegister(String clientId, Object payload) {
        LOGGER.debug("Register screen {}", clientId);
        messagingTemplate.convertAndSendToUser(
                clientId.trim(),
                "/queue/register",
                payload
        );
    }

    /**
     * Method used to update a dashboard from project token
     * @param projectToken the project token
     * @param payload the payload content
     */
    @Async
    public void updateProjectScreen(String projectToken, Object payload) {
        LOGGER.debug("Update project's screen {}", projectToken);
        LOGGER.trace("Update project's screen {}, data: {}", projectToken, payload);
        if (projectToken == null){
            LOGGER.error("Project token not found for payload: {}", payload);
            return;
        }
        messagingTemplate.convertAndSendToUser(
                projectToken.trim(),
                "/queue/live",
                payload
        );
    }


    /**
     * Method used to update project screen
     * @param projectToken project token
     * @param userId user id
     * @param payload data to send
     */
    @Async
    public void updateProjectScreen(String projectToken, String userId, Object payload) {
        LOGGER.debug("Update project's screen {} for user {}, data: {}", projectToken, userId, payload);
        messagingTemplate.convertAndSendToUser(
                projectToken.trim()+"-"+userId,
                "/queue/unique",
                payload
        );
    }

    /**
     * Method used to update a dashboard from project id
     * @param projectId the project id
     * @param payload the payload content
     */
    public void updateProjectScreen(Long projectId, Object payload) {
        updateProjectScreen(projectRepository.getToken(projectId), payload);
    }

    /**
     * Get client number on a specified projectId
     * @param projectId the project iD
     * @return the number of client for the specified client ID
     */
    public int getClientNumber(String projectId) {
        return projectClients.get(projectId).size();
    }

    /**
     * Get client on a specified projectId
     * @param projectId the project iD
     * @return the list of client
     */
    public Collection<Client> getClient(String projectId) {
        return projectClients.get(projectId);
    }


    /**
     * Force dashboard to display client Id
     * @param projectId the specified project Id
     */
    public void displayUniqueNumber(String projectId) {
        Iterator<Client> it = projectClients.values().iterator();
        while (it.hasNext()) {
            updateProjectScreen(projectId, it.next().getId(), new UpdateEvent(UpdateType.DISPLAY_NUMBER));
        }
    }

    /**
     * Disconnect screen from project
     * @param client the client to disconnect
     */
    public void disconnectClient(Client client) {
        updateProjectScreen(client.getProjectId(), client.getId(), new UpdateEvent(UpdateType.DISCONNECT));
    }

    /**
     * Method used to force reload all connected client
     */
    public void reloadAllConnectedDashboard(){
        Iterator<String> it = projectClients.keySet().iterator();
        while (it.hasNext()) {
            updateProjectScreen(it.next(), new UpdateEvent(UpdateType.GRID));
        }
    }
}
