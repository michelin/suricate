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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.js.JsResultDto;
import com.michelin.suricate.service.js.DashboardScheduleService;
import com.michelin.suricate.service.js.scheduler.JsExecutionScheduler;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JsResultAsyncTaskTest {
    @Mock
    private JsExecutionScheduler jsExecutionScheduler;

    @Mock
    private DashboardScheduleService dashboardScheduleService;

    @Mock
    private ScheduledFuture<JsResultDto> scheduledFuture;

    @Test
    void shouldSuccess() throws ExecutionException, InterruptedException, TimeoutException {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { return '{}'; }");

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);

        when(scheduledFuture.get(anyLong(), any())).thenReturn(jsResultDto);

        JsResultAsyncTask task = new JsResultAsyncTask(scheduledFuture,
            jsExecutionDto, jsExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(dashboardScheduleService)
            .processJsResult(jsResultDto, jsExecutionScheduler);
        verify(scheduledFuture)
            .get(60, TimeUnit.SECONDS);
    }

    @Test
    void shouldSuccessWithRetryOnce() throws ExecutionException, InterruptedException, TimeoutException {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setTimeout(120L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { return '{}'; }");

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);

        doThrow(new RuntimeException("Error"))
            .doNothing()
            .when(dashboardScheduleService).processJsResult(any(), any());
        when(scheduledFuture.get(anyLong(), any())).thenReturn(jsResultDto);

        JsResultAsyncTask task = new JsResultAsyncTask(scheduledFuture,
            jsExecutionDto, jsExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(dashboardScheduleService, times(2))
            .processJsResult(jsResultDto, jsExecutionScheduler);
        verify(scheduledFuture)
            .get(120, TimeUnit.SECONDS);
    }

    @Test
    void shouldRescheduleWhenAllRetriesPerformed() throws ExecutionException, InterruptedException, TimeoutException {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setTimeout(30L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { return '{}'; }");

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);

        doThrow(new RuntimeException("Error"))
            .when(dashboardScheduleService).processJsResult(any(), any());
        when(scheduledFuture.get(anyLong(), any())).thenReturn(jsResultDto);

        JsResultAsyncTask task = new JsResultAsyncTask(scheduledFuture,
            jsExecutionDto, jsExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(dashboardScheduleService, times(10))
            .processJsResult(jsResultDto, jsExecutionScheduler);
        verify(jsExecutionScheduler)
            .schedule(jsExecutionDto, false);
        verify(scheduledFuture)
            .get(60, TimeUnit.SECONDS);
    }

    @Test
    void shouldCatchInterruptedException() throws ExecutionException, InterruptedException, TimeoutException {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { return '{}'; }");

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);

        when(scheduledFuture.get(anyLong(), any())).thenThrow(new InterruptedException("error"));

        JsResultAsyncTask task = new JsResultAsyncTask(scheduledFuture,
            jsExecutionDto, jsExecutionScheduler, dashboardScheduleService);

        task.call();

        assertTrue(Thread.currentThread().isInterrupted());

        verify(scheduledFuture)
            .get(60, TimeUnit.SECONDS);
    }

    @Test
    void shouldCatchCancellationException() throws ExecutionException, InterruptedException, TimeoutException {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { return '{}'; }");

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);

        scheduledFuture.cancel(true);

        when(scheduledFuture.get(anyLong(), any()))
            .thenThrow(new CancellationException("error"));

        JsResultAsyncTask task = new JsResultAsyncTask(scheduledFuture,
            jsExecutionDto, jsExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(scheduledFuture)
            .get(60, TimeUnit.SECONDS);
    }

    @Test
    void shouldCatchCancellationExceptionAndCancelImmediately()
        throws ExecutionException, InterruptedException, TimeoutException {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { return '{}'; }");

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);

        scheduledFuture.cancel(true);

        when(scheduledFuture.isCancelled())
            .thenReturn(true);
        when(scheduledFuture.get(anyLong(), any()))
            .thenThrow(new CancellationException("error"));

        JsResultAsyncTask task = new JsResultAsyncTask(scheduledFuture,
            jsExecutionDto, jsExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(scheduledFuture)
            .get(60, TimeUnit.SECONDS);
    }

    @Test
    void shouldCatchTimeoutException() throws ExecutionException, InterruptedException, TimeoutException {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { return '{}'; }");

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);

        when(scheduledFuture.get(anyLong(), any())).thenThrow(new TimeoutException("error"));
        when(scheduledFuture.cancel(anyBoolean())).thenReturn(true);

        JsResultAsyncTask task = new JsResultAsyncTask(scheduledFuture,
            jsExecutionDto, jsExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(scheduledFuture)
            .get(60, TimeUnit.SECONDS);
        verify(scheduledFuture)
            .cancel(true);
        verify(dashboardScheduleService)
            .updateWidgetInstanceNoJsResult("The JavaScript execution exceeded the timeout defined by the widget", 1L,
                1L);
    }

    @Test
    void shouldCatchException() throws ExecutionException, InterruptedException, TimeoutException {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { return '{}'; }");

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);

        when(scheduledFuture.get(anyLong(), any())).thenThrow(new RuntimeException("Error"));
        when(scheduledFuture.cancel(anyBoolean())).thenReturn(true);

        JsResultAsyncTask task = new JsResultAsyncTask(scheduledFuture,
            jsExecutionDto, jsExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(scheduledFuture)
            .get(60, TimeUnit.SECONDS);
        verify(scheduledFuture)
            .cancel(true);
        verify(dashboardScheduleService)
            .updateWidgetInstanceNoJsResult("java.lang.RuntimeException: Error", 1L, 1L);
    }

    @Test
    void shouldRescheduleWhenExceptionOnUpdate() throws ExecutionException, InterruptedException, TimeoutException {
        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectId(1L);
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setDelay(0L);
        jsExecutionDto.setPreviousData(null);
        jsExecutionDto.setScript("function run() { return '{}'; }");

        JsResultDto jsResultDto = new JsResultDto();
        jsResultDto.setProjectId(1L);

        when(scheduledFuture.get(anyLong(), any())).thenThrow(new RuntimeException("Error"));
        when(scheduledFuture.cancel(anyBoolean())).thenReturn(true);
        doThrow(new RuntimeException()).when(dashboardScheduleService)
            .updateWidgetInstanceNoJsResult(any(), any(), any());

        JsResultAsyncTask task = new JsResultAsyncTask(scheduledFuture,
            jsExecutionDto, jsExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(scheduledFuture)
            .get(60, TimeUnit.SECONDS);
        verify(scheduledFuture)
            .cancel(true);
        verify(dashboardScheduleService)
            .updateWidgetInstanceNoJsResult("java.lang.RuntimeException: Error", 1L, 1L);
        verify(jsExecutionScheduler)
            .schedule(jsExecutionDto, false);
    }
}
