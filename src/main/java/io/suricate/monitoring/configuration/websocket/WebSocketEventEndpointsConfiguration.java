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

import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.api.RotationService;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.services.websocket.RotationWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

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
     * Constructor
     *
     * @param dashboardWebSocketService The dashboard web socket service
     * @param rotationWebSocketService  The rotation web socket service
     * @param projectService            The project service
     * @param rotationService           The rotation service
     */
    public WebSocketEventEndpointsConfiguration(final DashboardWebSocketService dashboardWebSocketService,
                                                final RotationWebSocketService rotationWebSocketService,
                                                final ProjectService projectService,
                                                final RotationService rotationService) {
        this.dashboardWebSocketService = dashboardWebSocketService;
        this.rotationWebSocketService = rotationWebSocketService;
        this.projectService = projectService;
        this.rotationService = rotationService;
    }

    /**
     * Entry point when a client subscribes to a socket. Intercept all the subscribe events but only keep
     * the uniq screen events (project or rotation) by filtering events that matches the
     * path /user/project_or_rotation_token-screen_code/queue/unique
     *
     * Determine which token is given : project or rotation token, build a new websocket session
     * and save it into the dedicated service
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
                WebsocketClient.WebsocketClientBuilder websocketClientBuilder = WebsocketClient.builder()
                        .sessionId(stompHeaderAccessor.getSessionId())
                        .subscriptionId(stompHeaderAccessor.getSubscriptionId())
                        .screenCode(matcher.group(SCREEN_CODE_REGEX_GROUP));

                Optional<Project> project = this.projectService.getOneByToken(matcher.group(PROJECT_OR_ROTATION_TOKEN_REGEX_GROUP));
                if (project.isPresent()) {
                    websocketClientBuilder
                            .projectToken(matcher.group(PROJECT_OR_ROTATION_TOKEN_REGEX_GROUP));

                    LOGGER.debug("A new client (session ID: {}, sub ID: {}, screen code: {}) subscribes to the project {}",
                            websocketClientBuilder.build().getSessionId(),
                            websocketClientBuilder.build().getSubscriptionId(),
                            websocketClientBuilder.build().getScreenCode(),
                            websocketClientBuilder.build().getProjectToken());

                    this.dashboardWebSocketService.addClientToProject(project.get(), websocketClientBuilder.build());
                } else {
                    Optional<Rotation> rotation = this.rotationService.getOneByToken(matcher.group(PROJECT_OR_ROTATION_TOKEN_REGEX_GROUP));

                    if (rotation.isPresent()) {
                        websocketClientBuilder
                                .rotationToken(matcher.group(PROJECT_OR_ROTATION_TOKEN_REGEX_GROUP));

                        LOGGER.debug("A new client (session ID: {}, sub ID: {}, screen code: {}) subscribes to the rotation {}",
                                websocketClientBuilder.build().getSessionId(),
                                websocketClientBuilder.build().getSubscriptionId(),
                                websocketClientBuilder.build().getScreenCode(),
                                websocketClientBuilder.build().getRotationToken());

                        this.rotationWebSocketService.addClientToRotation(rotation.get(), websocketClientBuilder.build());
                    }
                }
            }
        }
    }

    /**
     * Entry point when a client unsubscribes from a websocket.
     *
     * If the received unsubscribed event is an unsubscription from the project websocket connection,
     * then we remove it from the project/connection map to close the connection.
     *
     * @param event The unsubscribe event
     */
    @EventListener
    public void onSessionUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Optional<WebsocketClient> websocketClient = this.dashboardWebSocketService.getWebsocketClientsBySessionIdAndSubscriptionId(stompHeaderAccessor.getSessionId(),
                stompHeaderAccessor.getSubscriptionId());

        if (websocketClient.isPresent()) {
            LOGGER.debug("Unsubscribe client {} subscription {} with id {} for project {}",
                    websocketClient.get().getSessionId(), websocketClient.get().getSubscriptionId(), websocketClient.get().getScreenCode(),
                    websocketClient.get().getProjectToken());

            this.dashboardWebSocketService.removeClientFromProject(websocketClient.get());
        }
    }

    /**
     * Entry point when a client definitely closes a websocket.
     *
     * In this case, remove the closed websocket from the project/connection map
     * unconditionally
     *
     * @param event The disconnect event
     */
    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Optional<WebsocketClient> websocketClient = this.dashboardWebSocketService.getWebsocketClientsBySessionId(stompHeaderAccessor.getSessionId());

        if (websocketClient.isPresent()) {
            LOGGER.debug("Disconnect client {} with id {} from project {}",
                    websocketClient.get().getSessionId(), websocketClient.get().getScreenCode(), websocketClient.get().getProjectToken());

            this.dashboardWebSocketService.removeClientFromProject(websocketClient.get());
        }

        websocketClient = this.rotationWebSocketService.getWebsocketClientsBySessionId(stompHeaderAccessor.getSessionId());

        if (websocketClient.isPresent()) {
            LOGGER.debug("Disconnect client {} with id {} from rotation {}",
                    websocketClient.get().getSessionId(), websocketClient.get().getScreenCode(),
                    websocketClient.get().getRotationToken());

            this.rotationWebSocketService.removeClientFromRotation(websocketClient.get());
        }
    }
}
