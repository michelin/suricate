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

package io.suricate.monitoring.services.nashorn.tasks;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.nashorn.services.DashboardScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Slf4j
@Component
@Scope(value="prototype")
public class NashornRequestResultAsyncTask implements Callable<Void>{
    private static final int TIMEOUT = 60;

    public static final int MAX_RETRY = 10;

    private static final int MAX_BACK_OFF_PERIOD = 10000;

    private static final int MIN_BACK_OFF_PERIOD = 1000;

    @Autowired
    private DashboardScheduleService dashboardScheduleService;

    private final ScheduledFuture<NashornResponse> scheduledNashornRequestTask;

    private final NashornRequest nashornRequest;

    private final NashornRequestWidgetExecutionScheduler scheduler;

    private RetryTemplate retryTemplate;

    /**
     * Constructor
     * @param scheduledNashornRequestTask The scheduled asynchronous task which will execute the Nashorn request executing the widget
     * @param nashornRequest The Nashorn request itself
     * @param scheduler The Nashorn requests scheduler
     */
    public NashornRequestResultAsyncTask(ScheduledFuture<NashornResponse> scheduledNashornRequestTask,
                                         NashornRequest nashornRequest,
                                         NashornRequestWidgetExecutionScheduler scheduler) {
        this.scheduledNashornRequestTask = scheduledNashornRequestTask;
        this.nashornRequest = nashornRequest;
        this.scheduler = scheduler;

        this.initRetryTemplate();
    }

    /**
     * Method automatically called by the scheduler after the given delay.
     *
     * Compute a timeout duration to wait a response from the Nashorn widget execution request.
     *
     * Wait for a response from the Nashorn request task. We wait for the given amount of time.
     *
     * Update the widget from the Nashorn response and notify the Front-End. Perform some retries
     * on the widget update. If all the retries fail, then schedule a new Nashorn request execution.
     */
    @Override
    public Void call() {
        try {
            long nashornRequestExecutionTimeout = nashornRequest.getTimeout() == null || nashornRequest.getTimeout() < TIMEOUT ? TIMEOUT : nashornRequest.getTimeout();

            log.debug("Waiting for the response of the Nashorn request of the widget instance {} (until {} seconds before timeout)",
                    nashornRequest.getProjectWidgetId(), nashornRequestExecutionTimeout);

            // Wait for a response of the Nashorn request task
            NashornResponse nashornResponse = scheduledNashornRequestTask.get(nashornRequestExecutionTimeout, TimeUnit.SECONDS);

            retryTemplate.execute(retryContext -> {
                log.debug("Update the widget instance {} (try {}/{})", nashornResponse.getProjectWidgetId(), retryContext.getRetryCount(), MAX_RETRY);

                dashboardScheduleService.processNashornResponse(nashornResponse, scheduler);

                return null;
            }, context -> {
                log.error("Updating the widget instance {} failed after {} attempts", nashornRequest.getProjectWidgetId(), MAX_RETRY);

                scheduler.schedule(nashornRequest, false);

                return null;
            });
        } catch (InterruptedException ie) {
            log.error("Interrupted exception caught. Re-interrupting the thread for the widget instance {}",
                    nashornRequest.getProjectWidgetId());

            Thread.currentThread().interrupt();
        } catch (CancellationException cancellationException) {
            if (scheduledNashornRequestTask.isCancelled()) {
                log.debug("The Nashorn request has been canceled for the widget instance {}", nashornRequest.getProjectWidgetId());
            }
        } catch (Exception exception) {
            Throwable rootCause = ExceptionUtils.getRootCause(exception);

            String widgetLogs;

            // Handle the case when the Nashorn request exceeds the timeout define by the widget.
            // Set the widget logs and cancel the widget execution
            if (rootCause instanceof TimeoutException) {
                widgetLogs = "The Nashorn request exceeded the timeout defined by the widget";

                log.error("The Nashorn request exceeded the timeout defined by the widget instance {}. The Nashorn request is going to be cancelled.", nashornRequest.getProjectWidgetId());
            } else {
                widgetLogs = rootCause.toString();

                log.error("An error has occurred in the Nashorn request result task for the widget instance {}. The Nashorn request is going to be canceled.", nashornRequest.getProjectWidgetId(), exception);
            }

            scheduledNashornRequestTask.cancel(true);

            try {
                dashboardScheduleService.updateWidgetInstanceNoNashornResponse(widgetLogs, nashornRequest.getProjectWidgetId(), nashornRequest.getProjectId());
            } catch (Exception exception1) {
                log.error("Cannot update the widget instance {} with no Nashorn response cause of database issue. Rescheduling a new Nashorn request", nashornRequest.getProjectWidgetId(), exception1);

                scheduler.schedule(nashornRequest, false);
            }
        }

        return null;
    }

    /**
     * Init the Spring retry template which will perform some retries on the widget update
     */
    private void initRetryTemplate() {
        retryTemplate = new RetryTemplate();

        UniformRandomBackOffPolicy backOffPolicy = new UniformRandomBackOffPolicy();
        backOffPolicy.setMaxBackOffPeriod(MAX_BACK_OFF_PERIOD);
        backOffPolicy.setMinBackOffPeriod(MIN_BACK_OFF_PERIOD);

        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(MAX_RETRY));
        retryTemplate.setBackOffPolicy(backOffPolicy);
    }
}
