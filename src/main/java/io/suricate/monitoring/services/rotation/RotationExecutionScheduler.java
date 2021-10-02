package io.suricate.monitoring.services.rotation;

import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.model.enums.WidgetStateEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class RotationExecutionScheduler {
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RotationExecutionScheduler.class.getName());

    /**
     * The number of executor
     */
    private static final int EXECUTOR_POOL_SIZE = 60;

    /**
     * The Spring boot application context
     */
    private final ApplicationContext applicationContext;

    /**
     * Thread scheduler scheduling the asynchronous task which will execute the rotation
     */
    private ScheduledThreadPoolExecutor rotationExecutionThread;

    /**
     * Store the rotation tasks by screen code
     */
    private Map<String, WeakReference<ScheduledFuture<Void>>>
            rotationTasksByScreenCode = new ConcurrentHashMap<>();

    /**
     * Constructor
     *
     * @param applicationContext The application context
     */
    @Autowired
    public RotationExecutionScheduler(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Init the rotation scheduler
     */
    @PostConstruct
    public void init() {
        LOGGER.debug("Initializing the rotation scheduler");

        if (rotationExecutionThread != null) {
            rotationExecutionThread.shutdownNow();
        }

        rotationExecutionThread = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
        rotationExecutionThread.setRemoveOnCancelPolicy(true);

        rotationTasksByScreenCode.clear();
    }

    /**
     * Schedule a rotation of projects
     *
     * Build an async task that will push the next project of the rotation in the websocket and
     * schedule the next async task
     *
     * Schedule the async task with the delay of the current project behing displayed
     *
     * @param rotation The rotation to schedule
     * @param current The current project of the rotation
     * @param iterator An iterator pointing on the next projects to rotate
     * @param screenCode The screen code where to push the next project
     */
    public void scheduleRotation(Rotation rotation, RotationProject current, Iterator<RotationProject> iterator, String screenCode) {
        RotationExecutionAsyncTask rotationExecutionAsyncTask = this.applicationContext
                .getBean(RotationExecutionAsyncTask.class, this, rotation, iterator, screenCode);

        LOGGER.debug("Scheduling next rotation in {} seconds of rotation {} for screen {}",
                current.getRotationSpeed(), rotation.getId(), screenCode);

        ScheduledFuture<Void> scheduledRotationTask = this.rotationExecutionThread
                .schedule(rotationExecutionAsyncTask, current.getRotationSpeed(), TimeUnit.SECONDS);

        rotationTasksByScreenCode
                .put(screenCode, new WeakReference<>(scheduledRotationTask));
    }

    /**
     * Cancel the rotation execution of a screen
     *
     * @param screenCode The screen code
     */
    public void cancelRotationExecutionTask(String screenCode) {
        if (!this.isScreenInRotation(screenCode)) {
            return;
        }

        WeakReference<ScheduledFuture<Void>> scheduledTaskReference = this.rotationTasksByScreenCode.get(screenCode);

        if (scheduledTaskReference != null) {
            ScheduledFuture<Void> scheduledTask = scheduledTaskReference.get();

            if (scheduledTask != null && (!scheduledTask.isDone() || !scheduledTask.isCancelled())) {
                LOGGER.debug("Canceling future rotation task for client with id {}", screenCode);
                scheduledTask.cancel(true);
                this.rotationTasksByScreenCode.remove(screenCode);
            }
        }
    }

    /**
     * Is the given screen has a rotation currently running
     *
     * @param screenCode The screen code
     * @return True if it does, false otherwise
     */
    public boolean isScreenInRotation(String screenCode) {
        return this.rotationTasksByScreenCode.containsKey(screenCode);
    }
}
