package io.suricate.monitoring.services.websocket;

import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.mapper.ProjectMapper;
import io.suricate.monitoring.services.mapper.RotationMapper;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.nashorn.services.NashornService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
     * The rotation mapper
     */
    private final RotationMapper rotationMapper;

    /**
     * Constructor
     *
     * @param simpMessagingTemplate The stomp websocket message template
     * @param rotationMapper The rotation mapper
     */
    public RotationWebSocketService(final SimpMessagingTemplate simpMessagingTemplate,
                                    final RotationMapper rotationMapper) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.rotationMapper = rotationMapper;
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
    public void sendEventToRotationSubscribers(String rotationToken, String screenCode, UpdateEvent payload) {
        LOGGER.debug("Sending the event {} to the rotation {}", payload.getType(), rotationToken);

        if (rotationToken == null) {
            LOGGER.error("Rotation token null for payload: {}", payload);
            return;
        }

        simpMessagingTemplate.convertAndSendToUser(
                rotationToken.trim() + "-" + screenCode,
                "queue/unique",
                payload
        );
    }
}
