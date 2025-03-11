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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.js.WidgetVariableResponseDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.enumeration.WidgetStateEnum;
import com.michelin.suricate.service.api.ProjectWidgetService;
import com.michelin.suricate.service.api.WidgetService;
import com.michelin.suricate.service.js.JsExecutionService;
import com.michelin.suricate.service.js.task.JsResultAsyncTask;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class JsExecutionSchedulerTest {
    @Mock
    private JsExecutionService jsExecutionService;

    @Mock
    private ProjectWidgetService projectWidgetService;

    @Mock
    private WidgetService widgetService;

    @Mock
    private JsResultAsyncTask jsResultAsyncTask;

    @Mock
    private ApplicationContext applicationContext;

    @Spy
    @InjectMocks
    private JsExecutionScheduler scheduler;

    @Test
    void shouldNotScheduleNullRequest() {
        scheduler.schedule(null, true);

        verify(jsExecutionService, never()).isJsExecutable(any());
    }

    @Test
    void shouldNotScheduleNotExecutableRequest() {
        when(jsExecutionService.isJsExecutable(any())).thenReturn(false);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);

        scheduler.init();
        scheduler.schedule(jsExecutionDto, true);

        verify(jsExecutionService).isJsExecutable(jsExecutionDto);
        verify(projectWidgetService)
                .updateState(
                        argThat(WidgetStateEnum.STOPPED::equals),
                        argThat(projectWidgetId -> projectWidgetId.equals(1L)),
                        any());
    }

    @Test
    void shouldScheduleWidgetStoppedWithDelay() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(jsExecutionService.isJsExecutable(any())).thenReturn(true);
        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForJsExecution(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponseDto));
        when(applicationContext.getBean(eq(JsResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(jsResultAsyncTask);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.STOPPED);
        jsExecutionDto.setDelay(15L);

        scheduler.init();
        scheduler.schedule(jsExecutionDto, false);

        verify(jsExecutionService).isJsExecutable(jsExecutionDto);
        verify(projectWidgetService).getOne(1L);
        verify(widgetService).getWidgetParametersForJsExecution(widget);
        verify(projectWidgetService)
                .updateState(
                        argThat(WidgetStateEnum.RUNNING::equals),
                        argThat(projectWidgetId -> projectWidgetId.equals(1L)),
                        any());
    }

    @Test
    void shouldScheduleWidgetRunning() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(jsExecutionService.isJsExecutable(any())).thenReturn(true);
        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForJsExecution(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponseDto));
        when(applicationContext.getBean(eq(JsResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(jsResultAsyncTask);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.schedule(jsExecutionDto, true);

        verify(jsExecutionService).isJsExecutable(jsExecutionDto);
        verify(projectWidgetService).getOne(1L);
        verify(widgetService).getWidgetParametersForJsExecution(widget);
        verify(projectWidgetService, never()).updateState(any(), any(), any());
    }

    @Test
    void shouldScheduleJsExecRequests() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(jsExecutionService.isJsExecutable(any())).thenReturn(true);
        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForJsExecution(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponseDto));
        when(applicationContext.getBean(eq(JsResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(jsResultAsyncTask);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.scheduleJsRequests(Collections.singletonList(jsExecutionDto), true);

        verify(jsExecutionService).isJsExecutable(jsExecutionDto);
        verify(projectWidgetService).getOne(1L);
        verify(widgetService).getWidgetParametersForJsExecution(widget);
        verify(projectWidgetService, never()).updateState(any(), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenScheduleJsRequests() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.RUNNING);

        doThrow(new RuntimeException("error")).when(scheduler).schedule(jsExecutionDto, true);

        scheduler.scheduleJsRequests(Collections.singletonList(jsExecutionDto), true);

        verify(jsExecutionService, never()).isJsExecutable(any());
        verify(projectWidgetService, never()).getOne(any());
        verify(widgetService, never()).getWidgetParametersForJsExecution(any());
        verify(projectWidgetService, never()).updateState(any(), any(), any());
    }

    @Test
    void shouldCancelAndScheduleJsExecutionWhenPreviousTaskExist() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(jsExecutionService.isJsExecutable(any())).thenReturn(true);
        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForJsExecution(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponseDto));
        when(applicationContext.getBean(eq(JsResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(jsResultAsyncTask);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.schedule(jsExecutionDto, true);
        scheduler.cancelAndScheduleJsExecution(jsExecutionDto);

        verify(jsExecutionService, times(2)).isJsExecutable(jsExecutionDto);
        verify(projectWidgetService, times(2)).getOne(1L);
        verify(widgetService, times(2)).getWidgetParametersForJsExecution(widget);
        verify(scheduler, times(2)).cancelScheduledFutureTask(eq(1L), any());
        verify(projectWidgetService).updateState(WidgetStateEnum.STOPPED, 1L);
    }

    @Test
    void shouldCancelAndScheduleJsExecution() {
        WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
        widgetVariableResponseDto.setName("name");

        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setWidget(widget);

        when(jsExecutionService.isJsExecutable(any())).thenReturn(true);
        when(projectWidgetService.getOne(any())).thenReturn(Optional.of(projectWidget));
        when(widgetService.getWidgetParametersForJsExecution(any()))
                .thenReturn(Collections.singletonList(widgetVariableResponseDto));
        when(applicationContext.getBean(eq(JsResultAsyncTask.class), any(), any(), any(), any()))
                .thenReturn(jsResultAsyncTask);

        JsExecutionDto jsExecutionDto = new JsExecutionDto();
        jsExecutionDto.setProjectWidgetId(1L);
        jsExecutionDto.setWidgetState(WidgetStateEnum.RUNNING);

        scheduler.init();
        scheduler.cancelAndScheduleJsExecution(jsExecutionDto);

        verify(jsExecutionService).isJsExecutable(jsExecutionDto);
        verify(projectWidgetService).getOne(1L);
        verify(widgetService).getWidgetParametersForJsExecution(widget);
        verify(scheduler, never()).cancelScheduledFutureTask(eq(1L), any());
        verify(projectWidgetService).updateState(WidgetStateEnum.STOPPED, 1L);
    }

    @Test
    void shouldCancelWidgetsExecutionByProject() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setId(1L);
        project.setGrids(Collections.singleton(projectGrid));

        scheduler.init();
        scheduler.cancelWidgetsExecutionByProject(project);

        verify(projectWidgetService).updateState(WidgetStateEnum.STOPPED, 1L);
    }
}
