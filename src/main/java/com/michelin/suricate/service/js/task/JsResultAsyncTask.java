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
package com.michelin.suricate.service.js.task;

import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.js.JsResultDto;
import com.michelin.suricate.service.js.DashboardScheduleService;
import com.michelin.suricate.service.js.scheduler.JsExecutionScheduler;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/** Task that get the result of a Javascript script execution. */
@Slf4j
@Component
@Scope(value = "prototype")
public class JsResultAsyncTask implements Callable<Void> {
    public static final int MAX_RETRY = 10;
    private static final int TIMEOUT = 60;
    private static final int MAX_BACK_OFF_PERIOD = 10000;

    private static final int MIN_BACK_OFF_PERIOD = 1000;

    private final DashboardScheduleService dashboardScheduleService;

    private final ScheduledFuture<JsResultDto> scheduledJsExecutionTask;

    private final JsExecutionDto jsExecutionDto;

    private final JsExecutionScheduler scheduler;

    private RetryTemplate retryTemplate;

    /**
     * Constructor.
     *
     * @param scheduledJsExecutionTask The scheduled asynchronous task which will execute the Js execution
     * @param jsExecutionDto The Js execution itself
     * @param scheduler The Js execution scheduler
     * @param dashboardScheduleService The dashboard schedule service
     */
    public JsResultAsyncTask(
            ScheduledFuture<JsResultDto> scheduledJsExecutionTask,
            JsExecutionDto jsExecutionDto,
            JsExecutionScheduler scheduler,
            DashboardScheduleService dashboardScheduleService) {
        this.scheduledJsExecutionTask = scheduledJsExecutionTask;
        this.jsExecutionDto = jsExecutionDto;
        this.scheduler = scheduler;
        this.dashboardScheduleService = dashboardScheduleService;
        initRetryTemplate();
    }

    /**
     * Method automatically called by the scheduler after the given delay. Compute a timeout duration to wait a response
     * from the Js execution request. Wait for a response from the Js execution task. We wait for the given amount of
     * time. Update the widget from the Js result and notify the Front-End. Perform some retries on the widget update.
     * If all the retries fail, then schedule a new Js execution.
     */
    @Override
    public Void call() {
        try {
            long jsExecutionTimeout = jsExecutionDto.getTimeout() == null || jsExecutionDto.getTimeout() < TIMEOUT
                    ? TIMEOUT
                    : jsExecutionDto.getTimeout();

            log.debug(
                    "Waiting for the response of the JavaScript execution "
                            + "of the widget instance {} (until {} seconds before timeout)",
                    jsExecutionDto.getProjectWidgetId(),
                    jsExecutionTimeout);

            // Wait for a response of the Js execution task
            JsResultDto jsResultDto = scheduledJsExecutionTask.get(jsExecutionTimeout, TimeUnit.SECONDS);

            retryTemplate.execute(
                    retryContext -> {
                        log.debug(
                                "Update the widget instance {} (try {}/{})",
                                jsResultDto.getProjectWidgetId(),
                                retryContext.getRetryCount(),
                                MAX_RETRY);

                        dashboardScheduleService.processJsResult(jsResultDto, scheduler);

                        return null;
                    },
                    context -> {
                        log.error(
                                "Updating the widget instance {} failed after {} attempts",
                                jsExecutionDto.getProjectWidgetId(),
                                MAX_RETRY);

                        scheduler.schedule(jsExecutionDto, false);

                        return null;
                    });
        } catch (InterruptedException ie) {
            log.error(
                    "Interrupted exception caught. Re-interrupting the thread for the widget instance {}",
                    jsExecutionDto.getProjectWidgetId());

            Thread.currentThread().interrupt();
        } catch (CancellationException cancellationException) {
            if (scheduledJsExecutionTask.isCancelled()) {
                log.debug(
                        "The JavaScript execution has been canceled for the widget instance {}",
                        jsExecutionDto.getProjectWidgetId());
            }
        } catch (Exception exception) {
            Throwable rootCause = ExceptionUtils.getRootCause(exception);

            String widgetLogs;

            // Handle the case when the Js execution exceeds the timeout define by the widget.
            // Set the widget logs and cancel the widget execution
            if (rootCause instanceof TimeoutException) {
                widgetLogs = "The JavaScript execution exceeded the timeout defined by the widget";

                log.error(
                        "The JavaScript execution exceeded the timeout defined by the widget instance {}."
                                + " The JavaScript execution is going to be cancelled.",
                        jsExecutionDto.getProjectWidgetId());
            } else {
                widgetLogs = rootCause.toString();

                log.error(
                        "An error has occurred in the JavaScript result task for the widget instance {}."
                                + " The JavaScript execution is going to be canceled.",
                        jsExecutionDto.getProjectWidgetId(),
                        exception);
            }

            scheduledJsExecutionTask.cancel(true);

            try {
                dashboardScheduleService.updateWidgetInstanceNoJsResult(
                        widgetLogs, jsExecutionDto.getProjectWidgetId(), jsExecutionDto.getProjectId());
            } catch (Exception exception1) {
                log.error(
                        "Cannot update the widget instance {} with no JavaScript result cause of database issue. "
                                + "Rescheduling a new JavaScript execution",
                        jsExecutionDto.getProjectWidgetId(),
                        exception1);

                scheduler.schedule(jsExecutionDto, false);
            }
        }

        return null;
    }

    /** Init the Spring retry template which will perform some retries on the widget update. */
    private void initRetryTemplate() {
        retryTemplate = new RetryTemplate();

        UniformRandomBackOffPolicy backOffPolicy = new UniformRandomBackOffPolicy();
        backOffPolicy.setMaxBackOffPeriod(MAX_BACK_OFF_PERIOD);
        backOffPolicy.setMinBackOffPeriod(MIN_BACK_OFF_PERIOD);

        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(MAX_RETRY));
        retryTemplate.setBackOffPolicy(backOffPolicy);
    }
}
