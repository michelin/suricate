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

package com.michelin.suricate.services.nashorn.scheduler;

import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectGrid;
import com.michelin.suricate.services.api.ProjectWidgetService;
import com.michelin.suricate.services.nashorn.tasks.NashornRequestWidgetExecutionAsyncTask;
import com.michelin.suricate.model.dto.nashorn.NashornRequest;
import com.michelin.suricate.model.dto.nashorn.NashornResponse;
import com.michelin.suricate.model.dto.nashorn.WidgetVariableResponse;
import com.michelin.suricate.model.entities.ProjectWidget;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import com.michelin.suricate.services.api.WidgetService;
import com.michelin.suricate.services.nashorn.services.DashboardScheduleService;
import com.michelin.suricate.services.nashorn.services.NashornService;
import com.michelin.suricate.services.nashorn.tasks.NashornRequestResultAsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NashornRequestWidgetExecutionScheduler {
    private static final int EXECUTOR_POOL_SIZE = 60;

    private static final long NASHORN_IMMEDIATE_EXECUTION_DELAY = 1L;

    private ScheduledThreadPoolExecutor nashornRequestExecutor;

    private ScheduledThreadPoolExecutor nashornRequestResponseExecutor;

    private final Map<Long, Pair<WeakReference<ScheduledFuture<NashornResponse>>, WeakReference<ScheduledFuture<Void>>>> nashornTasksByProjectWidgetId = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Lazy
    @Autowired
    private ProjectWidgetService projectWidgetService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private NashornService nashornService;

    @Lazy
    @Autowired
    private DashboardScheduleService dashboardScheduleService;

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    private StringEncryptor stringEncryptor;

    /**
     * Init the Nashorn scheduler
     */
    @Transactional
    public void init() {
        log.debug("Initializing the Nashorn scheduler");

        if (nashornRequestExecutor != null) {
            nashornRequestExecutor.shutdownNow();
        }

        nashornRequestExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
        nashornRequestExecutor.setRemoveOnCancelPolicy(true);

        if (nashornRequestResponseExecutor != null) {
            nashornRequestResponseExecutor.shutdownNow();
        }

        nashornRequestResponseExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
        nashornRequestResponseExecutor.setRemoveOnCancelPolicy(true);

        nashornTasksByProjectWidgetId.clear();

        projectWidgetService.resetProjectWidgetsState();
    }

    /**
     * Schedule a list of Nashorn requests
     *
     * @param nashornRequests           The list of nashorn requests to schedule
     * @param startNashornRequestNow    Should the Nashorn request starts now or from the widget configured delay
     */
    public void scheduleNashornRequests(final List<NashornRequest> nashornRequests, boolean startNashornRequestNow) {
        try {
            nashornRequests.forEach(nashornRequest -> schedule(nashornRequest, startNashornRequestNow));
        } catch (Exception e) {
            log.error("An error has occurred when scheduling a Nashorn request for a new project subscription", e);
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
     * Create an asynchronous task which will execute the Nashorn request and execute the widget. Schedule
     * this task according to the computed delay.
     *
     * Create another asynchronous task which will wait for the result of the first task (the result of the widget execution).
     * It waits during the whole duration set in the widget description as timeout.
     *
     * @param nashornRequest         The Nashorn request
     * @param startNashornRequestNow Should the Nashorn request starts now or from the widget configured delay
     */
    public void schedule(final NashornRequest nashornRequest, final boolean startNashornRequestNow) {
        if (nashornRequest == null || nashornRequestExecutor == null || nashornRequestResponseExecutor == null) {
            return;
        }

        log.debug("Scheduling the Nashorn request of the widget instance {}", nashornRequest.getProjectWidgetId());

        if (!nashornService.isNashornRequestExecutable(nashornRequest)) {
            projectWidgetService.updateState(WidgetStateEnum.STOPPED, nashornRequest.getProjectWidgetId(), new Date());
            return;
        }

        if (WidgetStateEnum.STOPPED == nashornRequest.getWidgetState()) {
            log.debug("The widget instance {} of the Nashorn request was stopped. Setting the widget instance to running", nashornRequest.getProjectWidgetId());
            projectWidgetService.updateState(WidgetStateEnum.RUNNING, nashornRequest.getProjectWidgetId(), new Date());
        }

        ProjectWidget projectWidget = projectWidgetService
                .getOne(nashornRequest.getProjectWidgetId()).orElse(new ProjectWidget());

        List<WidgetVariableResponse> widgetParameters = widgetService
                .getWidgetParametersForNashorn(projectWidget.getWidget());

        long nashornRequestExecutionDelay = startNashornRequestNow ? NASHORN_IMMEDIATE_EXECUTION_DELAY : nashornRequest.getDelay();

        log.debug("The Nashorn request of the widget instance {} will start in {} second(s)", nashornRequest.getProjectWidgetId(), nashornRequestExecutionDelay);

        ScheduledFuture<NashornResponse> scheduledNashornRequestExecutionTask = nashornRequestExecutor
                .schedule(new NashornRequestWidgetExecutionAsyncTask(nashornRequest, stringEncryptor, widgetParameters),
                        nashornRequestExecutionDelay,
                        TimeUnit.SECONDS);

        NashornRequestResultAsyncTask nashornRequestResultAsyncTask = applicationContext
                .getBean(NashornRequestResultAsyncTask.class, scheduledNashornRequestExecutionTask, nashornRequest, this, dashboardScheduleService);

        ScheduledFuture<Void> scheduledNashornRequestResponseTask = nashornRequestResponseExecutor
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
       schedule(nashornRequest, true);
    }

    /**
     * Cancel all the widgets executions from a given project
     *
     * @param project The project
     */
    public void cancelWidgetsExecutionByProject(final Project project) {
        project.getGrids()
            .stream()
            .map(ProjectGrid::getWidgets)
            .flatMap(Collection::stream)
            .collect(Collectors.toList())
            .forEach(projectWidget -> cancelWidgetExecution(projectWidget.getId()));
    }

    /**
     * Cancel all the widgets executions from a given project grid
     *
     * @param projectGrid The project grid
     */
    public void cancelWidgetsExecutionByGrid(final ProjectGrid projectGrid) {
        projectGrid
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
            cancelScheduledFutureTask(projectWidgetId, pairOfNashornFutureTasks.getLeft());
            cancelScheduledFutureTask(projectWidgetId, pairOfNashornFutureTasks.getRight());
        }

        projectWidgetService.updateState(WidgetStateEnum.STOPPED, projectWidgetId);
    }

    /**
     * Cancel a scheduled future task for a widget instance
     *
     * @param projectWidgetId The widget instance ID
     * @param scheduledFutureTaskReference The reference containing the future task
     */
    public void cancelScheduledFutureTask(Long projectWidgetId, WeakReference<? extends ScheduledFuture<?>> scheduledFutureTaskReference) {
        if (scheduledFutureTaskReference != null) {
            ScheduledFuture<?> scheduledFutureTask = scheduledFutureTaskReference.get();

            if (scheduledFutureTask != null && (!scheduledFutureTask.isDone() || !scheduledFutureTask.isCancelled())) {
                log.debug("Canceling the future Nashorn execution task for the widget instance {}", projectWidgetId);

                scheduledFutureTask.cancel(true);
            }
        }
    }
}
