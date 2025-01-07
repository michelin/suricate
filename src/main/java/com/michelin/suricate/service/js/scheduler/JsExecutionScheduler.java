/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.service.js.scheduler;

import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.js.JsResultDto;
import com.michelin.suricate.model.dto.js.WidgetVariableResponseDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.enumeration.WidgetStateEnum;
import com.michelin.suricate.service.api.ProjectWidgetService;
import com.michelin.suricate.service.api.WidgetService;
import com.michelin.suricate.service.js.DashboardScheduleService;
import com.michelin.suricate.service.js.JsExecutionService;
import com.michelin.suricate.service.js.task.JsExecutionAsyncTask;
import com.michelin.suricate.service.js.task.JsResultAsyncTask;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

/**
 * Class used to schedule the Js executions.
 */
@Slf4j
@Service
public class JsExecutionScheduler {
    private static final int EXECUTOR_POOL_SIZE = 60;

    private static final long JS_IMMEDIATE_EXECUTION_DELAY = 1L;

    private final Map<Long, Pair<WeakReference<ScheduledFuture<JsResultDto>>,
        WeakReference<ScheduledFuture<Void>>>> jsTasksByProjectWidgetId = new ConcurrentHashMap<>();

    private ScheduledThreadPoolExecutor jsExecutionExecutor;

    private ScheduledThreadPoolExecutor jsResultExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    @Lazy
    @Autowired
    private ProjectWidgetService projectWidgetService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private JsExecutionService jsExecutionService;

    @Lazy
    @Autowired
    private DashboardScheduleService dashboardScheduleService;

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    private StringEncryptor stringEncryptor;

    /**
     * Init the Js executors.
     */
    @Transactional
    public void init() {
        log.debug("Initializing the JavaScript executors");

        if (jsExecutionExecutor != null) {
            jsExecutionExecutor.shutdownNow();
        }

        jsExecutionExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
        jsExecutionExecutor.setRemoveOnCancelPolicy(true);

        if (jsResultExecutor != null) {
            jsResultExecutor.shutdownNow();
        }

        jsResultExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
        jsResultExecutor.setRemoveOnCancelPolicy(true);

        jsTasksByProjectWidgetId.clear();

        projectWidgetService.resetProjectWidgetsState();
    }

    /**
     * Schedule a list of Js executions.
     *
     * @param jsExecutionDtos   The list of Js execution to schedule
     * @param startJsRequestNow Should the Js execution starts now or from the widget configured delay
     */
    public void scheduleJsRequests(final List<JsExecutionDto> jsExecutionDtos, boolean startJsRequestNow) {
        try {
            jsExecutionDtos.forEach(jsExecRequest -> schedule(jsExecRequest, startJsRequestNow));
        } catch (Exception e) {
            log.error("An error has occurred when scheduling a JavaScript request for a new project subscription", e);
        }
    }

    /**
     * Method used to schedule the Js execution updating the associated widget.
     * Checks if the given Js execution can be executed and set the widget in a pause state
     * if it cannot be.
     * If the widget was in a pause state from a previous execution, then set the widget in a running
     * state before executing the request.
     * Create an asynchronous task which will execute the Js execution and execute the widget. Schedule
     * this task according to the computed delay.
     * Create another asynchronous task which will wait for the result of the first task
     * (the result of the widget execution).
     * It waits during the whole duration set in the widget description as timeout.
     *
     * @param jsExecutionDto    The Js execution
     * @param startJsRequestNow Should the Js execution starts now or from the widget configured delay
     */
    public void schedule(final JsExecutionDto jsExecutionDto, final boolean startJsRequestNow) {
        if (jsExecutionDto == null || jsExecutionExecutor == null || jsResultExecutor == null) {
            return;
        }

        log.debug("Scheduling the JavaScript execution of the widget instance {}", jsExecutionDto.getProjectWidgetId());

        if (!jsExecutionService.isJsExecutable(jsExecutionDto)) {
            projectWidgetService.updateState(WidgetStateEnum.STOPPED, jsExecutionDto.getProjectWidgetId(), new Date());
            return;
        }

        if (WidgetStateEnum.STOPPED == jsExecutionDto.getWidgetState()) {
            log.debug(
                "The widget instance {} of the JavaScript execution was stopped. "
                    + "Setting the widget instance to running", jsExecutionDto.getProjectWidgetId());
            projectWidgetService.updateState(WidgetStateEnum.RUNNING, jsExecutionDto.getProjectWidgetId(), new Date());
        }

        ProjectWidget projectWidget = projectWidgetService
            .getOne(jsExecutionDto.getProjectWidgetId()).orElse(new ProjectWidget());

        List<WidgetVariableResponseDto> widgetParameters = widgetService
            .getWidgetParametersForJsExecution(projectWidget.getWidget());

        long jsRequestExecutionDelay = startJsRequestNow ? JS_IMMEDIATE_EXECUTION_DELAY : jsExecutionDto.getDelay();

        log.debug("The JavaScript execution of the widget instance {} will start in {} second(s)",
            jsExecutionDto.getProjectWidgetId(), jsRequestExecutionDelay);

        ScheduledFuture<JsResultDto> scheduledJsRequestTask = jsExecutionExecutor
            .schedule(new JsExecutionAsyncTask(jsExecutionDto, stringEncryptor, widgetParameters),
                jsRequestExecutionDelay,
                TimeUnit.SECONDS);

        JsResultAsyncTask jsResultAsyncTask = applicationContext
            .getBean(JsResultAsyncTask.class, scheduledJsRequestTask, jsExecutionDto, this, dashboardScheduleService);

        ScheduledFuture<Void> scheduledJsResponseTask = jsResultExecutor
            .schedule(jsResultAsyncTask, jsRequestExecutionDelay, TimeUnit.SECONDS);

        jsTasksByProjectWidgetId.put(jsExecutionDto.getProjectWidgetId(), ImmutablePair.of(
            new WeakReference<>(scheduledJsRequestTask),
            new WeakReference<>(scheduledJsResponseTask)
        ));
    }

    /**
     * Cancel the current widget execution and schedule a new Js execution for this widget.
     *
     * @param jsExecutionDto The new Js execution to schedule
     */
    public void cancelAndScheduleJsExecution(JsExecutionDto jsExecutionDto) {
        cancelWidgetExecution(jsExecutionDto.getProjectWidgetId());
        schedule(jsExecutionDto, true);
    }

    /**
     * Cancel all the widgets executions from a given project.
     *
     * @param project The project
     */
    public void cancelWidgetsExecutionByProject(final Project project) {
        project.getGrids()
            .stream()
            .map(ProjectGrid::getWidgets)
            .flatMap(Collection::stream)
            .toList()
            .forEach(projectWidget -> cancelWidgetExecution(projectWidget.getId()));
    }

    /**
     * Cancel all the widgets executions from a given project grid.
     *
     * @param projectGrid The project grid
     */
    public void cancelWidgetsExecutionByGrid(final ProjectGrid projectGrid) {
        projectGrid
            .getWidgets()
            .forEach(projectWidget -> cancelWidgetExecution(projectWidget.getId()));
    }

    /**
     * Cancel the widget execution by canceling both Js tasks.
     *
     * @param projectWidgetId the widget instance ID
     */
    public void cancelWidgetExecution(Long projectWidgetId) {
        Pair<WeakReference<ScheduledFuture<JsResultDto>>, WeakReference<ScheduledFuture<Void>>> pairOfJsFutureTasks =
            jsTasksByProjectWidgetId.get(projectWidgetId);

        if (pairOfJsFutureTasks != null) {
            cancelScheduledFutureTask(projectWidgetId, pairOfJsFutureTasks.getLeft());
            cancelScheduledFutureTask(projectWidgetId, pairOfJsFutureTasks.getRight());
        }

        projectWidgetService.updateState(WidgetStateEnum.STOPPED, projectWidgetId);
    }

    /**
     * Cancel a scheduled future task for a widget instance.
     *
     * @param projectWidgetId              The widget instance ID
     * @param scheduledFutureTaskReference The reference containing the future task
     */
    public void cancelScheduledFutureTask(Long projectWidgetId,
                                          WeakReference<? extends ScheduledFuture<?>> scheduledFutureTaskReference) {
        if (scheduledFutureTaskReference != null) {
            ScheduledFuture<?> scheduledFutureTask = scheduledFutureTaskReference.get();

            if (scheduledFutureTask != null && (!scheduledFutureTask.isDone() || !scheduledFutureTask.isCancelled())) {
                log.debug("Canceling the future JavaScript execution task for the widget instance {}", projectWidgetId);

                scheduledFutureTask.cancel(true);
            }
        }
    }
}
