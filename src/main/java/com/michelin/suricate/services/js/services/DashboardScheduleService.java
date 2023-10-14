/*
 *
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.michelin.suricate.services.js.services;

import com.michelin.suricate.services.api.ProjectWidgetService;
import com.michelin.suricate.services.mapper.ProjectWidgetMapper;
import com.michelin.suricate.services.js.scheduler.JsExecutionScheduler;
import com.michelin.suricate.services.websocket.DashboardWebSocketService;
import com.michelin.suricate.model.dto.js.JsExecutionDto;
import com.michelin.suricate.model.dto.js.JsResultDto;
import com.michelin.suricate.model.dto.websocket.UpdateEvent;
import com.michelin.suricate.model.entities.ProjectWidget;
import com.michelin.suricate.model.enums.JsExecutionErrorTypeEnum;
import com.michelin.suricate.model.enums.UpdateType;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import com.michelin.suricate.services.api.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class DashboardScheduleService {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DashboardWebSocketService dashboardWebSocketService;

    @Autowired
    private ProjectWidgetService projectWidgetService;

    @Autowired
    private ProjectWidgetMapper projectWidgetMapper;

    @Autowired
    private JsExecutionService jsExecutionService;

    @Autowired
    private ProjectService projectService;

    /**
     * Process the Js result
     *
     * Update the widget information.
     * If the Js execution is successful then update the data.
     * If the Js execution is failed, then just update the log.
     *
     * Schedule the next Js execution except if the current execution did not throw a fatal error
     *
     * @param jsResultDto The Js result
     * @param scheduler       The Js execution scheduler
     */
    @Transactional
    public void processJsResult(JsResultDto jsResultDto, JsExecutionScheduler scheduler) {
        if (jsResultDto.isValid()) {
            log.debug("The JavaScript result is valid for the widget instance: {}. Updating widget in database",
                    jsResultDto.getProjectWidgetId());

            projectWidgetService.updateWidgetInstanceAfterSucceededExecution(jsResultDto.getLaunchDate(),
                    jsResultDto.getLog(),
                    jsResultDto.getData(),
                    jsResultDto.getProjectWidgetId(),
                    WidgetStateEnum.RUNNING);
        } else {
            log.debug("The JavaScript result is not valid for the widget instance: {}. Logs: {}. Response data: {}",
                    jsResultDto.getProjectWidgetId(), jsResultDto.getLog(), jsResultDto);

            projectWidgetService.updateWidgetInstanceAfterFailedExecution(jsResultDto.getLaunchDate(),
                    jsResultDto.getLog(),
                    jsResultDto.getProjectWidgetId(),
                    jsResultDto.getError() == JsExecutionErrorTypeEnum.FATAL ? WidgetStateEnum.STOPPED : WidgetStateEnum.WARNING);
        }

        if (jsResultDto.isFatal()) {
            log.debug("The JavaScript result contains a fatal error for the widget instance: {}. Logs: {}. Response data: {}",
                    jsResultDto.getProjectWidgetId(), jsResultDto.getLog(), jsResultDto);
        } else {
            JsExecutionDto newJsExecutionDto = jsExecutionService.getJsExecutionByProjectWidgetId(jsResultDto.getProjectWidgetId());
            scheduler.schedule(newJsExecutionDto, false);
        }

        sendWidgetUpdateNotification(jsResultDto.getProjectWidgetId(), jsResultDto.getProjectId());
    }

    /**
     * Update the widget information when there is no Js result due to a failure
     *
     * @param widgetLogs      The exception message to log
     * @param projectWidgetId The widget instance id
     * @param projectId       The project id
     */
    @Transactional
    public void updateWidgetInstanceNoJsResult(String widgetLogs, Long projectWidgetId, Long projectId) {
        projectWidgetService.updateWidgetInstanceAfterFailedExecution(new Date(), widgetLogs, projectWidgetId, WidgetStateEnum.STOPPED);

        sendWidgetUpdateNotification(projectWidgetId, projectId);
    }

    /**
     * Create a new widget event which will be sent through the web sockets
     * to notify and update the widget on dashboard
     *
     * @param projectWidgetId The project widget ID
     * @param projectId       The project ID
     */
    public void sendWidgetUpdateNotification(Long projectWidgetId, Long projectId) {
        ProjectWidget projectWidget = projectWidgetService.getOne(projectWidgetId).orElse(null);

        UpdateEvent event = UpdateEvent.builder()
                .type(UpdateType.REFRESH_WIDGET)
                .content(projectWidgetMapper.toProjectWidgetDTO(projectWidget))
                .build();

        dashboardWebSocketService.sendEventToWidgetInstanceSubscribers(projectService.getTokenByProjectId(projectId), projectWidgetId, event);
    }

    /**
     * Schedule the execution of a given widget instance.
     * Prepare the Js execution then cancel the current request
     * and schedule a new request.
     *
     * @param projectWidgetId The widget instance ID
     */
    public void scheduleWidget(final Long projectWidgetId) {
        JsExecutionDto jsExecutionDto = jsExecutionService.getJsExecutionByProjectWidgetId(projectWidgetId);
        applicationContext.getBean(JsExecutionScheduler.class).cancelAndScheduleJsExecution(jsExecutionDto);
    }
}
