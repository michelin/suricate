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

package io.suricate.monitoring.services.nashorn.scheduler;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.dto.nashorn.WidgetVariableResponse;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectWidget;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.services.api.ProjectWidgetService;
import io.suricate.monitoring.services.api.WidgetService;
import io.suricate.monitoring.services.nashorn.services.NashornService;
import io.suricate.monitoring.services.nashorn.tasks.NashornRequestResultAsyncTask;
import io.suricate.monitoring.services.nashorn.tasks.NashornRequestWidgetExecutionAsyncTask;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class NashornRequestWidgetExecutionScheduler {
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NashornRequestWidgetExecutionScheduler.class.getName());

    /**
     * The number of executor
     */
    private static final int EXECUTOR_POOL_SIZE = 60;

    /**
     * Very short delay to execute a Nashorn request
     */
    private static final long SHORT_DELAY = 2L;

    /**
     * An inclusive starting delay to execute a Nashorn request
     */
    private static final int START_DELAY_INCLUSIVE = 30;

    /**
     * An exclusive ending delay to execute a Nashorn request
     */
    private static final int END_DELAY_EXCLUSIVE = 120;

    /**
     * The Spring boot application context
     */
    private ApplicationContext applicationContext;

    /**
     * Thread scheduler scheduling the asynchronous task which will execute a Nashorn request
     */
    private ScheduledThreadPoolExecutor scheduleNashornRequestExecutionThread;

    /**
     * Thread scheduler scheduling the asynchronous task which will wait for the Nashorn response
     */
    private ScheduledThreadPoolExecutor scheduleNashornRequestResponseThread;

    /**
     * For each widget instance, this map stores both Nashorn tasks : the task which will execute the widget
     * and the task which will wait for the response
     */
    private Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>> nashornTasksByProjectWidgetId = new ConcurrentHashMap<>();

    /**
     * The project widget service
     */
    private ProjectWidgetService projectWidgetService;

    /**
     * The nashorn service
     */
    private NashornService nashornService;

    /**
     * The string encryptor used to encrypt/decrypt the encrypted/decrypted secret properties
     */
    private StringEncryptor stringEncryptor;

    /**
     * Constructor
     *
     * @param applicationContext         The application context to inject
     * @param projectWidgetService       The project widget service to inject
     * @param nashornService             The nashorn service to inject
     * @param stringEncryptor            The string encryptor to inject
     */
    @Autowired
    public NashornRequestWidgetExecutionScheduler(final ApplicationContext applicationContext,
                                                  @Lazy final ProjectWidgetService projectWidgetService,
                                                  final NashornService nashornService,
                                                  @Qualifier("jasyptStringEncryptor") final StringEncryptor stringEncryptor) {
        this.applicationContext = applicationContext;
        this.projectWidgetService = projectWidgetService;
        this.nashornService = nashornService;
        this.stringEncryptor = stringEncryptor;
    }

    /**
     * Init the Nashorn scheduler
     */
    @Transactional
    public void init() {
        LOGGER.debug("Initializing the Nashorn scheduler");

        if (scheduleNashornRequestExecutionThread != null) {
            scheduleNashornRequestExecutionThread.shutdownNow();
        }

        scheduleNashornRequestExecutionThread = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
        scheduleNashornRequestExecutionThread.setRemoveOnCancelPolicy(true);

        if (scheduleNashornRequestResponseThread != null) {
            scheduleNashornRequestResponseThread.shutdownNow();
        }

        scheduleNashornRequestResponseThread = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
        scheduleNashornRequestResponseThread.setRemoveOnCancelPolicy(true);

        nashornTasksByProjectWidgetId.clear();

        projectWidgetService.resetProjectWidgetsState();
    }

    /**
     * Schedule a list of Nashorn requests
     *
     * @param nashornRequests The list of nashorn requests to schedule
     * @param start           If the scheduling should start now
     * @param init            If it's an initialisation of the scheduling
     */
    public void scheduleNashornRequests(final List<NashornRequest> nashornRequests, boolean start, boolean init) {
        try {
            nashornRequests
                    .forEach(nashornRequest -> schedule(nashornRequest, start, init));
        } catch (Exception e) {
            LOGGER.error("An error has occurred when scheduling a Nashorn request for a new project subscription", e);
        }
    }

    /**
     * Method used to schedule the Nashorn request updating the associated widget.
     *
     * Checks if the given Nashorn request can be executed and set the widget in a pause state
     * if it cannot be.
     *
     * If the widget was in a pause state from a previous execution, then set the widget in a running
     * state before executing the request.
     *
     * Compute the Nashorn request execution delay
     *
     * Create an asynchronous task which will execute the Nashorn request and execute the widget. Schedule
     * this task according to the computed delay.
     *
     * Create another asynchronous task which will wait for the result of the first task (the result of the widget execution).
     * It waits during the whole duration set in the widget description as timeout.
     *
     * @param nashornRequest The Nashorn request
     * @param start          Force the Nashorn request to start now
     * @param init           Force the Nashorn request to start randomly between START_DELAY_INCLUSIVE and END_DELAY_EXCLUSIVE
     */
    public void schedule(final NashornRequest nashornRequest, boolean start, boolean init) {
        if (nashornRequest == null || scheduleNashornRequestExecutionThread == null || scheduleNashornRequestResponseThread == null) {
            return;
        }

        LOGGER.debug("Scheduling the Nashorn request of the widget instance {}", nashornRequest.getProjectWidgetId());

        // Get the beans inside schedule
        ProjectWidgetService projectWidgetServiceInjected = applicationContext.getBean(ProjectWidgetService.class);
        WidgetService widgetService = applicationContext.getBean(WidgetService.class);

        if (!nashornService.isNashornRequestExecutable(nashornRequest)) {
            LOGGER.debug("The Nashorn request of the widget instance {} is not valid. Stopping the widget", nashornRequest.getProjectWidgetId());
            projectWidgetServiceInjected.updateState(WidgetState.STOPPED, nashornRequest.getProjectWidgetId(), new Date());
            return;
        }

        if (WidgetState.STOPPED == nashornRequest.getWidgetState()) {
            LOGGER.debug("The widget instance {} of the Nashorn request was stopped. Setting the widget instance to running", nashornRequest.getProjectWidgetId());
            projectWidgetServiceInjected.updateState(WidgetState.RUNNING, nashornRequest.getProjectWidgetId(), new Date());
        }

        Long nashornRequestExecutionDelay = nashornRequest.getDelay();
        if (start) {
            nashornRequestExecutionDelay = RandomUtils.nextLong(START_DELAY_INCLUSIVE, END_DELAY_EXCLUSIVE);
        } else if (init) {
            nashornRequestExecutionDelay = SHORT_DELAY;
        }

        ProjectWidget projectWidget = projectWidgetServiceInjected
                .getOne(nashornRequest.getProjectWidgetId()).orElse(new ProjectWidget());

        List<WidgetVariableResponse> widgetParameters = widgetService
                .getWidgetParametersForNashorn(projectWidget.getWidget());

        LOGGER.debug("The Nashorn request of the widget instance {} will start in {} seconds", nashornRequest.getProjectWidgetId(), nashornRequestExecutionDelay);

        ScheduledFuture<NashornResponse> scheduledNashornRequestExecutionTask = scheduleNashornRequestExecutionThread
                .schedule(new NashornRequestWidgetExecutionAsyncTask(nashornRequest, stringEncryptor, widgetParameters),
                        nashornRequestExecutionDelay,
                        TimeUnit.SECONDS);

        NashornRequestResultAsyncTask nashornRequestResultAsyncTask = applicationContext
                .getBean(NashornRequestResultAsyncTask.class, scheduledNashornRequestExecutionTask, nashornRequest, this);

        ScheduledFuture<Void> scheduledNashornRequestResponseTask = scheduleNashornRequestResponseThread
                .schedule(nashornRequestResultAsyncTask, nashornRequestExecutionDelay, TimeUnit.SECONDS);

        nashornTasksByProjectWidgetId.put(nashornRequest.getProjectWidgetId(), ImmutablePair.of(
                new WeakReference<>(scheduledNashornRequestExecutionTask),
                new WeakReference<>(scheduledNashornRequestResponseTask)
            ));

    }

    /**
     * Cancel the current widget execution and schedule a new Nashorn request for this widget
     *
     * @param nashornRequest The new Nashorn request to schedule
     */
    public void cancelAndScheduleNashornRequest(NashornRequest nashornRequest) {
        cancelWidgetExecution(nashornRequest.getProjectWidgetId());
        schedule(nashornRequest, false, true);
    }

    /**
     * Cancel all the widgets executions from a given project
     *
     * @param project The project
     */
    public void cancelWidgetsExecutionByProject(final Project project) {
        project
            .getWidgets()
            .forEach(projectWidget -> cancelWidgetExecution(projectWidget.getId()));
    }

    /**
     * Cancel the widget execution by canceling both Nashorn tasks
     *
     * @param projectWidgetId the widget instance ID
     */
    public void cancelWidgetExecution(Long projectWidgetId) {
        Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>> pairOfNashornFutureTasks = nashornTasksByProjectWidgetId.get(projectWidgetId);

        if (pairOfNashornFutureTasks != null) {
            this.cancelScheduledFutureTask(projectWidgetId, pairOfNashornFutureTasks.getLeft());
            this.cancelScheduledFutureTask(projectWidgetId, pairOfNashornFutureTasks.getRight());
        }

        projectWidgetService.updateState(WidgetState.STOPPED, projectWidgetId);
    }

    /**
     * Cancel a scheduled future task for a widget instance
     *
     * @param projectWidgetId The widget instance ID
     * @param scheduledFutureTaskReference The reference containing the future task
     */
    private void cancelScheduledFutureTask(Long projectWidgetId, WeakReference<? extends ScheduledFuture<?>> scheduledFutureTaskReference) {
        if (scheduledFutureTaskReference != null) {
            ScheduledFuture<?> scheduledFutureTask = scheduledFutureTaskReference.get();

            if (scheduledFutureTask != null && (!scheduledFutureTask.isDone() || !scheduledFutureTask.isCancelled())) {
                LOGGER.debug("Canceling the future task for the widget instance {} ({})", projectWidgetId, scheduledFutureTask);
                scheduledFutureTask.cancel(true);
            }
        }
    }
}
