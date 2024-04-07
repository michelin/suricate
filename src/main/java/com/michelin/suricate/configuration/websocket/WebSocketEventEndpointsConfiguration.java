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

package com.michelin.suricate.configuration.websocket;

import com.michelin.suricate.model.dto.websocket.WebsocketClient;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.service.api.ProjectService;
import com.michelin.suricate.service.websocket.DashboardWebSocketService;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * Websocket event endpoints configuration.
 */
@Slf4j
@Configuration
public class WebSocketEventEndpointsConfiguration {
    private static final int PROJECT_TOKEN_REGEX_GROUP = 1;
    private static final int SCREEN_CODE_REGEX_GROUP = 2;

    @Autowired
    private DashboardWebSocketService dashboardWebSocketService;

    @Autowired
    private ProjectService projectService;

    /**
     * Entry point when a client subscribes to a socket. Intercept all the subscribe events but only keep
     * the uniq screen events by filtering events that matches the
     * path /user/project_token-screen_code/queue/unique
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

                Optional<Project> project = this.projectService.getOneByToken(matcher.group(PROJECT_TOKEN_REGEX_GROUP));
                if (project.isPresent()) {
                    websocketClientBuilder
                        .projectToken(matcher.group(PROJECT_TOKEN_REGEX_GROUP));

                    log.debug("A new client (session ID: {}, sub ID: {}, screen code: {}) subscribes to the project {}",
                        websocketClientBuilder.build().getSessionId(),
                        websocketClientBuilder.build().getSubscriptionId(),
                        websocketClientBuilder.build().getScreenCode(),
                        websocketClientBuilder.build().getProjectToken());

                    dashboardWebSocketService.addClientToProject(project.get(), websocketClientBuilder.build());
                }
            }
        }
    }

    /**
     * Entry point when a client unsubscribes from a websocket.
     * If the received unsubscribed event is an unsubscription from the project websocket connection,
     * then we remove it from the project/connection map to close the connection.
     *
     * @param event The unsubscribe event
     */
    @EventListener
    public void onSessionUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Optional<WebsocketClient> websocketClient =
            dashboardWebSocketService.getWebsocketClientsBySessionIdAndSubscriptionId(
                stompHeaderAccessor.getSessionId(),
                stompHeaderAccessor.getSubscriptionId());

        if (websocketClient.isPresent()) {
            log.debug("Unsubscribe client {} subscription {} with id {} for project {}",
                websocketClient.get().getSessionId(), websocketClient.get().getSubscriptionId(),
                websocketClient.get().getScreenCode(),
                websocketClient.get().getProjectToken());

            dashboardWebSocketService.removeClientFromProject(websocketClient.get());
        }
    }

    /**
     * Entry point when a client definitely closes a websocket.
     * In this case, remove the closed websocket from the project/connection map
     * unconditionally
     *
     * @param event The disconnect event
     */
    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Optional<WebsocketClient> websocketClient =
            dashboardWebSocketService.getWebsocketClientsBySessionId(stompHeaderAccessor.getSessionId());

        if (websocketClient.isPresent()) {
            log.debug("Disconnect client {} with id {} from project {}",
                websocketClient.get().getSessionId(), websocketClient.get().getScreenCode(),
                websocketClient.get().getProjectToken());

            dashboardWebSocketService.removeClientFromProject(websocketClient.get());
        }
    }
}
