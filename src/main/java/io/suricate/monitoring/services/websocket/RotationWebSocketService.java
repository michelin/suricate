package io.suricate.monitoring.services.websocket;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.suricate.monitoring.model.dto.api.rotation.RotationResponseDto;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.enums.UpdateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manage the rotation messaging through websockets
 */
@Lazy(false)
@Service
public class RotationWebSocketService {
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RotationWebSocketService.class);

    /**
     * The stomp websocket message template
     */
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Save all websocket clients by rotation token.
     *
     * Represents all the connected screens to a rotation
     */
    private final Multimap<String, WebsocketClient> websocketClientByRotationToken = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    /**
     * Constructor
     *
     * @param simpMessagingTemplate The stomp websocket message template
     */
    public RotationWebSocketService(final SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Send a rotation to a screen waiting for something (a project, or a rotation)
     *
     * @param rotation The rotation to send
     * @param screenCode The screen waiting for something
     */
    public void sendConnectRotationEventToScreenSubscriber(final RotationResponseDto rotation, final String screenCode) {
        UpdateEvent updateEvent = UpdateEvent.builder()
                .type(UpdateType.CONNECT_ROTATION)
                .content(rotation)
                .build();

        LOGGER.debug("Sending the event {} to the screen {}", updateEvent.getType(), screenCode.replaceAll("[\n\r\t]", "_"));

        simpMessagingTemplate.convertAndSendToUser(
                screenCode,
                "/queue/connect",
                updateEvent
        );
    }

    /**
     * Send event to a screen synchronized with a rotation
     *
     * @param rotationToken The rotation token used to identify screens synchronized to a rotation
     * @param screenCode The screen code used to identify the screen
     * @param payload The event to send to the screen
     */
    @Async
    public void sendEventToScreenRotationSubscriber(String rotationToken, String screenCode, UpdateEvent payload) {
        LOGGER.debug("Sending the event {} to the rotation {}", payload.getType(), rotationToken);

        simpMessagingTemplate.convertAndSendToUser(
                rotationToken.trim() + "-" + screenCode,
                "queue/unique",
                payload
        );
    }

    /**
     * Send an event through the associated websocket to all subscribers.
     * The path of the websocket contains a rotation token so it is unique for each
     * rotation.
     *
     * Used to display the screen code number of a rotation subscribers,
     *
     * @param rotationToken   The rotation token
     * @param payload         The payload content
     */
    @Async
    public void sendEventToRotationSubscribers(String rotationToken, UpdateEvent payload) {
        LOGGER.debug("Sending the event {} to the rotation {}", payload.getType(), rotationToken);

        if (rotationToken == null) {
            LOGGER.error("Project token null for payload: {}", payload);
            return;
        }

        simpMessagingTemplate.convertAndSendToUser(
                rotationToken.trim(),
                "/queue/live",
                payload
        );
    }

    /**
     * Add a new link between a rotation materialized by its token
     * and a client materialized by its WebsocketClient.
     * Triggered when a new subscription to a rotation is done.
     *
     * Schedule the rotation request execution.
     *
     * @param rotation        The connected rotation
     * @param websocketClient The related websocket client
     */
    public void addClientToRotation(final Rotation rotation, final WebsocketClient websocketClient) {
        this.websocketClientByRotationToken.put(rotation.getToken(), websocketClient);
    }

    /**
     * Remove a given websocket from the rotation/connection map
     *
     * @param websocketClient The websocket to remove
     */
    public void removeClientFromRotation(WebsocketClient websocketClient) {
        this.websocketClientByRotationToken.remove(websocketClient.getRotationToken(), websocketClient);
    }

    /**
     * Get a websocket by session ID
     *
     * @param sessionId The session ID
     * @return The websocket
     */
    public Optional<WebsocketClient> getWebsocketClientsBySessionId(final String sessionId) {
        return this.websocketClientByRotationToken.values()
                .stream()
                .filter(websocketClient -> websocketClient.getSessionId().equals(sessionId))
                .findFirst();
    }

    /**
     * Get the list of clients connected to a rotation
     *
     * @param rotationToken The rotation token
     * @return The list of related websocket clients
     */
    public List<WebsocketClient> getWebsocketClientsByRotationToken(final String rotationToken) {
        return new ArrayList<>(this.websocketClientByRotationToken.get(rotationToken));
    }

    /**
     * Method that force the reload of every connected clients for a project
     *
     * @param projectToken The project token
     */
    public void reloadAllConnectedClientsToARotation(final String rotationToken) {
        if (!this.getWebsocketClientsByRotationToken(rotationToken).isEmpty()) {
            this.sendEventToRotationSubscribers(rotationToken,
                    UpdateEvent.builder()
                            .type(UpdateType.RELOAD)
                            .build());
        }
    }

    /**
     * Disconnect all screens from rotation
     *
     * @param rotationToken The rotation token
     */
    public void disconnectAllClientsFromRotation(final String rotationToken) {
        if (!this.getWebsocketClientsByRotationToken(rotationToken).isEmpty()) {
            this.sendEventToRotationSubscribers(rotationToken,
                    UpdateEvent.builder()
                            .type(UpdateType.DISCONNECT)
                            .build());
        }
    }

    /**
     * Disconnect screen from rotation
     *
     * @param rotationToken The rotation token
     * @param screenCode    The screen code
     */
    public void disconnectClientFromRotation(final String rotationToken, final String screenCode) {
        this.sendEventToScreenRotationSubscriber(rotationToken, screenCode,
                UpdateEvent.builder().type(UpdateType.DISCONNECT).build());
    }
}
