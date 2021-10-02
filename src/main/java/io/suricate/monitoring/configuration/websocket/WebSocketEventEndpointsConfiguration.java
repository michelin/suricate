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

package io.suricate.monitoring.configuration.websocket;

import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.api.RotationService;
import io.suricate.monitoring.services.rotation.RotationExecutionScheduler;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.services.websocket.RotationWebSocketService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manage the subscription/unsubscription events of the web sockets
 */
@Configuration
public class WebSocketEventEndpointsConfiguration {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventEndpointsConfiguration.class);

    /**
     * Regex group for project or rotation token
     */
    private static final int PROJECT_OR_ROTATION_TOKEN_REGEX_GROUP = 1;

    /**
     * Regex group for screen code
     */
    private static final int SCREEN_CODE_REGEX_GROUP = 2;

    /**
     * The dashboard websocket service
     */
    private final DashboardWebSocketService dashboardWebSocketService;

    /**
     * The rotation websocket service
     */
    private final RotationWebSocketService rotationWebSocketService;

    /**
     * The project service
     */
    private final ProjectService projectService;

    /**
     * The rotation service
     */
    private final RotationService rotationService;

    /**
     * The rotation execution scheduler
     */
    private final RotationExecutionScheduler rotationExecutionScheduler;

    /**
     * Constructor
     *
     * @param dashboardWebSocketService  The dashboard websocket service
     * @param rotationExecutionScheduler The rotation execution scheduler
     */
    public WebSocketEventEndpointsConfiguration(final DashboardWebSocketService dashboardWebSocketService,
                                                final RotationWebSocketService rotationWebSocketService,
                                                final ProjectService projectService,
                                                final RotationService rotationService,
                                                final RotationExecutionScheduler rotationExecutionScheduler) {
        this.dashboardWebSocketService = dashboardWebSocketService;
        this.rotationWebSocketService = rotationWebSocketService;
        this.rotationExecutionScheduler = rotationExecutionScheduler;
        this.projectService = projectService;
        this.rotationService = rotationService;
    }

    /**
     * Entry point when a client subscribes to a socket. Intercept all the subscribe events
     *
     * Filter a subscribe event that matches the path /user/project_or_rotation_token-screen_code/queue/unique
     *
     * Determine which token is given : project or rotation token
     *
     * @param event The subscription event
     */
    @EventListener
    public void onSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String simpDestination = (String) stompHeaderAccessor.getHeader("simpDestination");

        if (simpDestination != null) {
            Pattern pattern = Pattern.compile("/user/([A-Z0-9]+)-([0-9]+)/queue/unique");
            Matcher matcher = pattern.matcher(simpDestination);

            if (matcher.find()) {
                Optional<Project> project = this.projectService.getOneByToken(matcher.group(PROJECT_OR_ROTATION_TOKEN_REGEX_GROUP));

                // Project token is given
                if (project.isPresent()) {
                    WebsocketClient websocketClient = WebsocketClient.builder()
                            .projectToken(matcher.group(PROJECT_OR_ROTATION_TOKEN_REGEX_GROUP))
                            .sessionId(stompHeaderAccessor.getSessionId())
                            .subscriptionId(stompHeaderAccessor.getSubscriptionId())
                            .screenCode(matcher.group(SCREEN_CODE_REGEX_GROUP))
                            .build();

                    LOGGER.debug("A new client (session ID: {}, sub ID: {}, screen code: {}) subscribes to the project {}",
                            websocketClient.getSessionId(), websocketClient.getSubscriptionId(), websocketClient.getScreenCode(), websocketClient.getProjectToken());

                    this.dashboardWebSocketService.addClientToProject(websocketClient.getProjectToken(), websocketClient);
                    this.dashboardWebSocketService.addSessionClient(websocketClient.getSessionId(), websocketClient);
                }
                // Rotation token is given
                else {
                    Optional<Rotation> rotation = this.rotationService.getOneByToken(matcher.group(PROJECT_OR_ROTATION_TOKEN_REGEX_GROUP));

                    if (rotation.isPresent()) {
                        WebsocketClient websocketClient = WebsocketClient.builder()
                                .rotationToken(matcher.group(PROJECT_OR_ROTATION_TOKEN_REGEX_GROUP))
                                .sessionId(stompHeaderAccessor.getSessionId())
                                .subscriptionId(stompHeaderAccessor.getSubscriptionId())
                                .screenCode(matcher.group(SCREEN_CODE_REGEX_GROUP))
                                .build();

                        LOGGER.debug("A new client (session ID: {}, sub ID: {}, screen code: {}) subscribes to the rotation {}",
                                websocketClient.getSessionId(), websocketClient.getSubscriptionId(), websocketClient.getScreenCode(), websocketClient.getRotationToken());

                        Iterator<RotationProject> iterator = rotation.get().getRotationProjects().iterator();
                        RotationProject current = iterator.next();

                        this.rotationService.scheduleRotation(rotation.get(), current, iterator, websocketClient.getScreenCode());
                    }
                }
            }
        }
    }

    /**
     * Entry point when a client unsubscribe to the web sockets
     * Called when an unsubscription is triggered manually
     *
     * @param event The unsubscribe event
     */
    @EventListener
    public void onSessionUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        WebsocketClient websocketClient = this.dashboardWebSocketService
                .removeSessionClientByWebsocketSessionIdAndSubscriptionId(stompHeaderAccessor.getSessionId(), stompHeaderAccessor.getSubscriptionId());

        if (websocketClient != null) {
            LOGGER.debug("Unsubscribe client {} with id {} for project {}", websocketClient.getSessionId(), websocketClient.getScreenCode(), websocketClient.getProjectToken());
            this.dashboardWebSocketService.removeProjectClient(websocketClient.getProjectToken(), websocketClient);
            //this.rotationExecutionScheduler.cancelRotationExecutionTask(websocketClient.getScreenCode());
        }
    }

    /**
     * Entry point when a client disconnect to the web sockets
     * Called when a disconnection is triggered by a page refreshment
     *
     * @param event The disconnect event
     */
    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        WebsocketClient websocketClient = dashboardWebSocketService
                .removeSessionClientByWebsocketSessionId(stompHeaderAccessor.getSessionId());

        if (websocketClient != null) {
            LOGGER.debug("Disconnect client {} with id {} for project {}", websocketClient.getSessionId(), websocketClient.getScreenCode(), websocketClient.getProjectToken());
            this.dashboardWebSocketService.removeProjectClient(websocketClient.getProjectToken(), websocketClient);
            this.rotationExecutionScheduler.cancelRotationExecutionTask(websocketClient.getScreenCode());
        }
    }
}
