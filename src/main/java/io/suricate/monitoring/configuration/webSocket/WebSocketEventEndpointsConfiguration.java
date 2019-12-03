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

package io.suricate.monitoring.configuration.webSocket;

import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.service.websocket.DashboardWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configuration class managing the subscription and disconnect events of web sockets
 */
@Configuration
public class WebSocketEventEndpointsConfiguration {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventEndpointsConfiguration.class);

    /**
     * Regex group for project token
     */
    private static final int PROJECT_TOKEN_REGEX_GROUP = 1;

    /**
     * Regex group for screen code
     */
    private static final int SCREEN_CODE_REGEX_GROUP = 2;

    /**
     * The dashboard websocket service
     */
    private final DashboardWebSocketService dashboardWebSocketService;

    /**
     * Constructor
     *
     * @param dashboardWebSocketService The dashboard websocket service
     */
    public WebSocketEventEndpointsConfiguration(final DashboardWebSocketService dashboardWebSocketService) {
        this.dashboardWebSocketService = dashboardWebSocketService;
    }

    /**
     * Entry point when a client subscribe to the web sockets
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
                WebsocketClient websocketClient = new WebsocketClient(
                    matcher.group(PROJECT_TOKEN_REGEX_GROUP),
                    stompHeaderAccessor.getSessionId(),
                    stompHeaderAccessor.getSubscriptionId(),
                    matcher.group(SCREEN_CODE_REGEX_GROUP)
                );
                LOGGER.debug("New Client {} with id {} for project {}", websocketClient.getSessionId(), websocketClient.getScreenCode(), websocketClient.getProjectToken());

                dashboardWebSocketService.addProjectClient(websocketClient.getProjectToken(), websocketClient);
                dashboardWebSocketService.addSessionClient(websocketClient.getSessionId(), websocketClient);
            }
        }
    }

    /**
     * Entry point when a client unsubscribe to the web sockets
     *
     * @param event The unsubscribe event
     */
    @EventListener
    public void onSessionUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        WebsocketClient websocketClient = dashboardWebSocketService.removeSessionClientByWebsocketSessionIdAndSubscriptionId(stompHeaderAccessor.getSessionId(), stompHeaderAccessor.getSubscriptionId());

        if (websocketClient != null) {
            LOGGER.debug("Disconnected Client {} with id {} for project {}", websocketClient.getSessionId(), websocketClient.getScreenCode(), websocketClient.getProjectToken());
            dashboardWebSocketService.removeProjectClient(websocketClient.getProjectToken(), websocketClient);
        }
    }

    /**
     * Entry point when a client disconnect to the web sockets
     *
     * @param event The disconnect event
     */
    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        WebsocketClient websocketClient = dashboardWebSocketService.removeSessionClientByWebsocketSessionId(stompHeaderAccessor.getSessionId());

        if (websocketClient != null) {
            LOGGER.debug("Disconnected Client {} with id {} for project {}", websocketClient.getSessionId(), websocketClient.getScreenCode(), websocketClient.getProjectToken());
            dashboardWebSocketService.removeProjectClient(websocketClient.getProjectToken(), websocketClient);
        }
    }
}
