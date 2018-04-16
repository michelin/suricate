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

package io.suricate.monitoring.service.nashorn.task;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.service.Schedulable;
import io.suricate.monitoring.service.DashboardScheduleService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Scope(value="prototype")
public class NashornResultAsyncTask implements Callable<Void>{

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NashornResultAsyncTask.class.getName());

    /**
     * Task timeout 60 seconds
     */
    private static final int TIMEOUT = 60;

    /**
     * Number max of retry
     */
    public static final int MAX_RETRY = 10;

    /**
     * Max backoff period
     */
    private static final int MAX_BACK_OFF_PERIOD = 10000;

    /**
     * Min backoff period
     */
    private static final int MIN_BACK_OFF_PERIOD = 1000;

    @Autowired
    private DashboardScheduleService dashboardScheduleService;

    private ScheduledFuture<NashornResponse> future;

    private NashornRequest request;

    private Schedulable callBack;

    private RetryTemplate retryTemplate;

    public NashornResultAsyncTask(ScheduledFuture<NashornResponse> future, NashornRequest request, Schedulable callback) {
        this.future = future;
        this.request = request;
        this.callBack = callback;

        // Init retry template
        retryTemplate = new RetryTemplate();
        UniformRandomBackOffPolicy backOffPolicy = new UniformRandomBackOffPolicy();
        backOffPolicy.setMaxBackOffPeriod(MAX_BACK_OFF_PERIOD);
        backOffPolicy.setMinBackOffPeriod(MIN_BACK_OFF_PERIOD);
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(MAX_RETRY));
        retryTemplate.setBackOffPolicy(backOffPolicy);
    }

    @Override
    public Void call() throws Exception {
        try {
            long timeout = request.getTimeout() == null || request.getTimeout() < TIMEOUT ? TIMEOUT : request.getTimeout();
            LOGGER.debug("Widget instance {} wait {} seconds", request.getProjectWidgetId(), timeout);
            // Wait to for the widget response
            NashornResponse nashornResponse = future.get(timeout, TimeUnit.SECONDS);

            // Handle response with retry
            retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
                LOGGER.debug("Trying {}/{} to update widgets instance {}", context.getRetryCount(), MAX_RETRY, nashornResponse.getProjectWidgetId());
                dashboardScheduleService.handleResponse( nashornResponse, callBack);
                return null;
            }, context -> {
                LOGGER.error("Update data failed after {} attempts for widget instance:{}", MAX_RETRY, request.getProjectWidgetId());
                // On max retry re-schedule widget update
                callBack.schedule(request, false, false);
                return null;
            });

        } catch (CancellationException ce) {
            LOGGER.debug("Widget instance {} execution canceled ({}) - {}", request.getProjectWidgetId(), future.toString(), ce.getMessage(), ce);
        } catch (Exception e) {
            LOGGER.error("Error {} for widget instance:{}",ExceptionUtils.getMessage(e), request.getProjectWidgetId(), e);
            future.cancel(true);
            try {
                dashboardScheduleService.updateLogException(e, request.getProjectWidgetId(), request.getProjectId());
            }catch (Exception e1){
                LOGGER.error("Database issue, reschedule instance:{} - error {}", request.getProjectWidgetId(), ExceptionUtils.getMessage(e1));
                callBack.schedule(request, false, false);
            }
        }
        return null;
    }
}
