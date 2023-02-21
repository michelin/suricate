package com.michelin.suricate.services.nashorn.tasks;

import com.michelin.suricate.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import com.michelin.suricate.services.nashorn.services.DashboardScheduleService;
import com.michelin.suricate.model.dto.nashorn.NashornRequest;
import com.michelin.suricate.model.dto.nashorn.NashornResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NashornRequestResultAsyncTaskTest {
    @Mock
    private NashornRequestWidgetExecutionScheduler nashornRequestWidgetExecutionScheduler;

    @Mock
    private DashboardScheduleService dashboardScheduleService;

    @Mock
    private ScheduledFuture<NashornResponse> scheduledFuture;

    @Test
    void shouldSuccess() throws ExecutionException, InterruptedException, TimeoutException {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setDelay(0L);
        nashornRequest.setPreviousData(null);
        nashornRequest.setScript("function run() { return '{}'; }");

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);

        when(scheduledFuture.get(anyLong(), any())).thenReturn(nashornResponse);

        NashornRequestResultAsyncTask task = new NashornRequestResultAsyncTask(scheduledFuture,
                nashornRequest, nashornRequestWidgetExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(dashboardScheduleService)
                .processNashornResponse(nashornResponse, nashornRequestWidgetExecutionScheduler);
        verify(scheduledFuture)
                .get(60, TimeUnit.SECONDS);
    }

    @Test
    void shouldSuccessWithRetryOnce() throws ExecutionException, InterruptedException, TimeoutException {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setDelay(0L);
        nashornRequest.setTimeout(120L);
        nashornRequest.setPreviousData(null);
        nashornRequest.setScript("function run() { return '{}'; }");

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);

        doThrow(new RuntimeException("Error"))
                .doNothing()
                .when(dashboardScheduleService).processNashornResponse(any(), any());
        when(scheduledFuture.get(anyLong(), any())).thenReturn(nashornResponse);

        NashornRequestResultAsyncTask task = new NashornRequestResultAsyncTask(scheduledFuture,
                nashornRequest, nashornRequestWidgetExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(dashboardScheduleService, times(2))
                .processNashornResponse(nashornResponse, nashornRequestWidgetExecutionScheduler);
        verify(scheduledFuture)
                .get(120, TimeUnit.SECONDS);
    }

    @Test
    void shouldRescheduleWhenAllRetriesPerformed() throws ExecutionException, InterruptedException, TimeoutException {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setDelay(0L);
        nashornRequest.setTimeout(30L);
        nashornRequest.setPreviousData(null);
        nashornRequest.setScript("function run() { return '{}'; }");

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);

        doThrow(new RuntimeException("Error"))
                .when(dashboardScheduleService).processNashornResponse(any(), any());
        when(scheduledFuture.get(anyLong(), any())).thenReturn(nashornResponse);

        NashornRequestResultAsyncTask task = new NashornRequestResultAsyncTask(scheduledFuture,
                nashornRequest, nashornRequestWidgetExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(dashboardScheduleService, times(10))
                .processNashornResponse(nashornResponse, nashornRequestWidgetExecutionScheduler);
        verify(nashornRequestWidgetExecutionScheduler)
                .schedule(nashornRequest, false);
        verify(scheduledFuture)
                .get(60, TimeUnit.SECONDS);
    }

    @Test
    void shouldCatchInterruptedException() throws ExecutionException, InterruptedException, TimeoutException {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setDelay(0L);
        nashornRequest.setPreviousData(null);
        nashornRequest.setScript("function run() { return '{}'; }");

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);

        when(scheduledFuture.get(anyLong(), any())).thenThrow(new InterruptedException("error"));

        NashornRequestResultAsyncTask task = new NashornRequestResultAsyncTask(scheduledFuture,
                nashornRequest, nashornRequestWidgetExecutionScheduler, dashboardScheduleService);

        task.call();

        assertThat(Thread.currentThread().isInterrupted()).isTrue();

        verify(scheduledFuture)
                .get(60, TimeUnit.SECONDS);
    }

    @Test
    void shouldCatchCancellationException() throws ExecutionException, InterruptedException, TimeoutException {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setDelay(0L);
        nashornRequest.setPreviousData(null);
        nashornRequest.setScript("function run() { return '{}'; }");

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);

        scheduledFuture.cancel(true);

        when(scheduledFuture.get(anyLong(), any()))
                .thenThrow(new CancellationException("error"));

        NashornRequestResultAsyncTask task = new NashornRequestResultAsyncTask(scheduledFuture,
                nashornRequest, nashornRequestWidgetExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(scheduledFuture)
                .get(60, TimeUnit.SECONDS);
    }

    @Test
    void shouldCatchCancellationExceptionAndCancelImmediately() throws ExecutionException, InterruptedException, TimeoutException {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setDelay(0L);
        nashornRequest.setPreviousData(null);
        nashornRequest.setScript("function run() { return '{}'; }");

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);

        scheduledFuture.cancel(true);

        when(scheduledFuture.isCancelled())
                .thenReturn(true);
        when(scheduledFuture.get(anyLong(), any()))
                .thenThrow(new CancellationException("error"));

        NashornRequestResultAsyncTask task = new NashornRequestResultAsyncTask(scheduledFuture,
                nashornRequest, nashornRequestWidgetExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(scheduledFuture)
                .get(60, TimeUnit.SECONDS);
    }

    @Test
    void shouldCatchTimeoutException() throws ExecutionException, InterruptedException, TimeoutException {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setDelay(0L);
        nashornRequest.setPreviousData(null);
        nashornRequest.setScript("function run() { return '{}'; }");

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);

        when(scheduledFuture.get(anyLong(), any())).thenThrow(new TimeoutException("error"));
        when(scheduledFuture.cancel(anyBoolean())).thenReturn(true);

        NashornRequestResultAsyncTask task = new NashornRequestResultAsyncTask(scheduledFuture,
                nashornRequest, nashornRequestWidgetExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(scheduledFuture)
                .get(60, TimeUnit.SECONDS);
        verify(scheduledFuture)
                .cancel(true);
        verify(dashboardScheduleService)
                .updateWidgetInstanceNoNashornResponse("The Nashorn request exceeded the timeout defined by the widget", 1L, 1L);
    }

    @Test
    void shouldCatchException() throws ExecutionException, InterruptedException, TimeoutException {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setDelay(0L);
        nashornRequest.setPreviousData(null);
        nashornRequest.setScript("function run() { return '{}'; }");

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);

        when(scheduledFuture.get(anyLong(), any())).thenThrow(new RuntimeException("Error"));
        when(scheduledFuture.cancel(anyBoolean())).thenReturn(true);

        NashornRequestResultAsyncTask task = new NashornRequestResultAsyncTask(scheduledFuture,
                nashornRequest, nashornRequestWidgetExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(scheduledFuture)
                .get(60, TimeUnit.SECONDS);
        verify(scheduledFuture)
                .cancel(true);
        verify(dashboardScheduleService)
                .updateWidgetInstanceNoNashornResponse("java.lang.RuntimeException: Error", 1L, 1L);
    }

    @Test
    void shouldRescheduleWhenExceptionOnUpdate() throws ExecutionException, InterruptedException, TimeoutException {
        NashornRequest nashornRequest = new NashornRequest();
        nashornRequest.setProjectId(1L);
        nashornRequest.setProjectWidgetId(1L);
        nashornRequest.setDelay(0L);
        nashornRequest.setPreviousData(null);
        nashornRequest.setScript("function run() { return '{}'; }");

        NashornResponse nashornResponse = new NashornResponse();
        nashornResponse.setProjectId(1L);

        when(scheduledFuture.get(anyLong(), any())).thenThrow(new RuntimeException("Error"));
        when(scheduledFuture.cancel(anyBoolean())).thenReturn(true);
        doThrow(new RuntimeException()).when(dashboardScheduleService).updateWidgetInstanceNoNashornResponse(any(), any(), any());

        NashornRequestResultAsyncTask task = new NashornRequestResultAsyncTask(scheduledFuture,
                nashornRequest, nashornRequestWidgetExecutionScheduler, dashboardScheduleService);

        task.call();

        verify(scheduledFuture)
                .get(60, TimeUnit.SECONDS);
        verify(scheduledFuture)
                .cancel(true);
        verify(dashboardScheduleService)
                .updateWidgetInstanceNoNashornResponse("java.lang.RuntimeException: Error", 1L, 1L);
        verify(nashornRequestWidgetExecutionScheduler)
                .schedule(nashornRequest, false);
    }
}
