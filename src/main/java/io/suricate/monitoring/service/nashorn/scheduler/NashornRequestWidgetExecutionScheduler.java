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

package io.suricate.monitoring.service.nashorn.scheduler;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.dto.nashorn.WidgetVariableResponse;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.service.api.WidgetService;
import io.suricate.monitoring.service.nashorn.service.NashornService;
import io.suricate.monitoring.service.nashorn.task.NashornRequestWidgetExecutionResultAsyncTask;
import io.suricate.monitoring.service.nashorn.task.NashornRequestWidgetExecutionAsyncTask;
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
    private ApplicationContext ctx;

    /**
     * Thread scheduler scheduling the asynchronous task which will execute a Nashorn request
     */
    private ScheduledThreadPoolExecutor scheduleNashornRequestExecutionThread;
    private ScheduledThreadPoolExecutor scheduledExecutorServiceFuture;

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
     * Map containing all current scheduled jobs
     */
    private Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>> jobs = new ConcurrentHashMap<>();

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
        this.ctx = applicationContext;
        this.projectWidgetService = projectWidgetService;
        this.nashornService = nashornService;
        this.stringEncryptor = stringEncryptor;
    }

    /**
     * Method used to cancel a scheduled future for an widget instance
     *
     * @param projectWidgetId project widget Id
     * @param weakReference   weakReference containing the ScheduledFuture or null
     */
    private static void cancel(Long projectWidgetId, WeakReference<? extends ScheduledFuture> weakReference) {
        if (weakReference != null) {
            ScheduledFuture scheduledFuture = weakReference.get();
            if (scheduledFuture != null && (!scheduledFuture.isDone() || !scheduledFuture.isCancelled())) {
                LOGGER.debug("Cancel task for widget instance {} ({})", projectWidgetId, scheduledFuture);
                scheduledFuture.cancel(true);
            }
        }
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

        if (scheduledExecutorServiceFuture != null) {
            scheduledExecutorServiceFuture.shutdownNow();
        }

        scheduledExecutorServiceFuture = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
        scheduledExecutorServiceFuture.setRemoveOnCancelPolicy(true);

        jobs.clear();

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
     * Create another ansynchronous task which will
     *
     * @param nashornRequest The Nashorn request
     * @param start          Force the Nashorn request to start now
     * @param init           Force the Nashorn request to start randomly between START_DELAY_INCLUSIVE and END_DELAY_EXCLUSIVE
     */
    public void schedule(final NashornRequest nashornRequest, boolean start, boolean init) {
        if (nashornRequest == null || scheduleNashornRequestExecutionThread == null || scheduledExecutorServiceFuture == null) {
            return;
        }

        LOGGER.debug("Scheduling the Nashorn request of the widget instance {}", nashornRequest.getProjectWidgetId());

        // Get the beans inside schedule
        ProjectWidgetService projectWidgetServiceInjected = ctx.getBean(ProjectWidgetService.class);
        WidgetService widgetService = ctx.getBean(WidgetService.class);

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

        ScheduledFuture<NashornResponse> scheduledNashornRequestTask = scheduleNashornRequestExecutionThread
                .schedule(new NashornRequestWidgetExecutionAsyncTask(nashornRequest, stringEncryptor, widgetParameters),
                        nashornRequestExecutionDelay,
                        TimeUnit.SECONDS);

        ScheduledFuture<Void> scheduledNashornResultTask = scheduledExecutorServiceFuture
                .schedule(new NashornRequestWidgetExecutionResultAsyncTask(scheduledNashornRequestTask, nashornRequest, this),
                nashornRequestExecutionDelay,
                TimeUnit.SECONDS);

        // Update job
        jobs.put(nashornRequest.getProjectWidgetId(),
            new ImmutablePair<>(
                new WeakReference<>(scheduledNashornRequestTask),
                new WeakReference<>(scheduledNashornResultTask)
            ));

    }

    /**
     * Method used to cancelWidgetInstance the existing scheduled widget instance and launch a new instance
     *
     * @param nashornRequest the nashorn request to execute
     */
    public void cancelAndSchedule(NashornRequest nashornRequest) {
        cancelWidgetInstance(nashornRequest.getProjectWidgetId());
        schedule(nashornRequest, false, true);
    }

    /**
     * Method is used for cancelling the projectWidgets hold by a project
     *
     * @param project The project
     */
    public void cancelProjectScheduling(final Project project) {
        project
            .getWidgets()
            .forEach(projectWidget -> cancelWidgetInstance(projectWidget.getId()));
    }

    /**
     * Method used to cancelWidgetInstance the existing scheduled widget instance
     *
     * @param projectWidgetId the widget instance id
     */
    public void cancelWidgetInstance(Long projectWidgetId) {
        Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>> pair = jobs.get(projectWidgetId);
        if (pair != null) {
            cancel(projectWidgetId, pair.getLeft());
            cancel(projectWidgetId, pair.getRight());
        }
        projectWidgetService.updateState(WidgetState.STOPPED, projectWidgetId);
    }
}
