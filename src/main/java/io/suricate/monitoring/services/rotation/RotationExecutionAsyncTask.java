package io.suricate.monitoring.services.rotation;

import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.services.nashorn.services.DashboardScheduleService;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.services.websocket.RotationWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Callable;

@Component
@Scope(value="prototype")
public class RotationExecutionAsyncTask implements Callable<Void>  {
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RotationExecutionAsyncTask.class);

    /**
     * Web socket service for rotation
     */
    @Autowired
    private RotationWebSocketService rotationWebSocketService;

    /**
     * Screen code of the screen where to push the next project rotation
     */
    private final String screenCode;

    /**
     * The rotation
     */
    private final Rotation rotation;

    /**
     * An iterator pointing on the rotation
     */
    private Iterator<RotationProject> iterator;

    /**
     * The rotation scheduler, used to schedule the next rotation
     */
    private final RotationExecutionScheduler rotationExecutionScheduler;

    /**
     * Constructor
     *
     * @param rotationExecutionScheduler The rotation scheduler
     * @param rotation The rotation
     * @param iterator An iterator pointing on the rotation
     * @param screenCode Screen code of the screen where to push the next project rotation
     */
    public RotationExecutionAsyncTask(RotationExecutionScheduler rotationExecutionScheduler, Rotation rotation,
                                      Iterator<RotationProject> iterator, String screenCode) {
        this.rotationExecutionScheduler = rotationExecutionScheduler;
        this.rotation = rotation;
        this.iterator = iterator;
        this.screenCode = screenCode;
    }

    /**
     * Execute the async task
     *
     * Get the next project to push in the web socket, push it, and schedule
     * the next task that will display the next project
     *
     * @throws Exception Any exception triggered during the async task execution
     */
    @Override
    public Void call() throws Exception {
        LOGGER.debug("Executing the project rotation for rotation {} of screen {}", this.rotation.getId(), this.screenCode);

        if (!iterator.hasNext()) {
            LOGGER.debug("End of rotation {} reached for screen {}. Resetting iterator to the beginning", this.rotation.getId(),
                    this.screenCode);

            iterator = rotation.getRotationProjects().iterator();
        }

        RotationProject next = iterator.next();

        this.rotationWebSocketService
                .sendEventToScreenRotationSubscriber(rotation.getToken(), this.screenCode, UpdateEvent.builder()
                        .type(UpdateType.ROTATE)
                        .build());

        this.rotationExecutionScheduler.scheduleRotation(rotation, next, iterator, screenCode);

        return null;
    }
}
