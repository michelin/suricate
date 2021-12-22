package io.suricate.monitoring.services.tasks;

import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.services.websocket.RotationWebSocketService;

import java.util.concurrent.Callable;

public class RotationAsyncTask implements Callable<Void> {
    /**
     * The rotation web socket service
     */
    private RotationWebSocketService rotationWebSocketService;

    /**
     * The rotation
     */
    private Rotation rotation;

    /**
     * The current index of the dashboard to display
     */
    private Integer currentIndex;

    /**
     * The screen code
     */
    private String screenCode;

    /**
     * Constructor
     *
     * @param rotation The rotation
     * @param currentIndex The current index of the dashboard to display
     * @param screenCode The screen code
     * @param rotationWebSocketService The rotation web socket service
     */
    public RotationAsyncTask(Rotation rotation, Integer currentIndex, String screenCode, RotationWebSocketService rotationWebSocketService) {
        this.rotation = rotation;
        this.currentIndex = currentIndex;
        this.screenCode = screenCode;
        this.rotationWebSocketService = rotationWebSocketService;
    }

    /**
     * Trigger the rotation
     *
     * @return Void
     * @throws Exception
     */
    @Override
    public Void call() throws Exception {
        int nextIndex = currentIndex < rotation.getRotationProjects().size() - 1 ? currentIndex + 1 : 0;

        rotationWebSocketService.scheduleRotation(rotation, nextIndex, screenCode);

        return null;
    }
}
