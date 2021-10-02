package io.suricate.monitoring.services.websocket;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.api.RotationService;
import io.suricate.monitoring.services.mapper.ProjectMapper;
import io.suricate.monitoring.services.mapper.RotationMapper;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.nashorn.services.NashornService;
import io.suricate.monitoring.services.rotation.RotationExecutionScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
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
     * The rotation service
     */
    private final RotationService rotationService;

    /**
     * The rotation mapper
     */
    private final RotationMapper rotationMapper;

    /**
     * The rotation execution scheduler
     */
    private final RotationExecutionScheduler rotationExecutionScheduler;

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
     * @param rotationService The rotation service
     * @param rotationMapper The rotation mapper
     * @param rotationExecutionScheduler The rotation execution scheduler
     */
    public RotationWebSocketService(final SimpMessagingTemplate simpMessagingTemplate,
                                    final RotationService rotationService,
                                    final RotationMapper rotationMapper,
                                    final RotationExecutionScheduler rotationExecutionScheduler) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.rotationMapper = rotationMapper;
        this.rotationService = rotationService;
        this.rotationExecutionScheduler = rotationExecutionScheduler;
    }

    /**
     * Send a rotation to a screen waiting for something (a project, or a rotation)
     *
     * @param rotation The rotation to send
     * @param screenCode The screen waiting for something
     */
    public void sendConnectRotationEventToScreenSubscriber(final Rotation rotation, final String screenCode) {
        UpdateEvent updateEvent = UpdateEvent.builder()
                .type(UpdateType.CONNECT_ROTATION)
                .content(this.rotationMapper.toRotationDTO(rotation))
                .build();

        LOGGER.debug("Sending the event {} to the screen {}", updateEvent.getType(), screenCode);

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

        Iterator<RotationProject> iterator = rotation.getRotationProjects().iterator();
        RotationProject current = iterator.next();

        this.rotationService.scheduleRotation(rotation, current, iterator, websocketClient.getScreenCode());
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
     * Remove a given websocket from the rotation/connection map
     *
     * @param websocketClient The websocket to remove
     */
    public void removeClientFromRotation(WebsocketClient websocketClient) {
        this.websocketClientByRotationToken.remove(websocketClient.getRotationToken(), websocketClient);

        this.rotationExecutionScheduler.
                cancelRotationExecutionTask(websocketClient.getScreenCode());
    }

    /**
     * Force dashboard to display client Id
     *
     * @param projectToken the specified project token
     */
    public void displayScreenCodeForProject(String projectToken) {
        this.sendEventToRotationSubscribers(projectToken, UpdateEvent.builder().type(UpdateType.DISPLAY_NUMBER).build());
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
}
