/*
 *
 *  * Copyright 2012-2018 the original author or authors.
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

package io.suricate.monitoring.services.nashorn.services;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entities.ProjectWidget;
import io.suricate.monitoring.model.enums.NashornErrorTypeEnum;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.model.enums.WidgetStateEnum;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.api.ProjectWidgetService;
import io.suricate.monitoring.services.mapper.ProjectWidgetMapper;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class DashboardScheduleService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardScheduleService.class);

    /**
     * The application context
     */
    private final ApplicationContext applicationContext;

    /**
     * The dashboard websocket service
     */
    private final DashboardWebSocketService dashboardWebSocketService;

    /**
     * The project widget service
     */
    private final ProjectWidgetService projectWidgetService;

    /**
     * The project widget mapper
     */
    private final ProjectWidgetMapper projectWidgetMapper;

    /**
     * The nashorn service
     */
    private final NashornService nashornService;

    /**
     * The project service
     */
    private final ProjectService projectService;

    /**
     * Constructor
     *
     * @param dashboardWebSocketService The dashboard websocket service
     * @param projectService            The project service
     * @param projectWidgetService      The project widget service
     * @param projectWidgetMapper       The project widget mapper
     * @param nashornService            The nashorn service to inject
     * @param applicationContext        The application context to inject
     */
    @Autowired
    public DashboardScheduleService(final DashboardWebSocketService dashboardWebSocketService,
                                    final ProjectService projectService,
                                    final ProjectWidgetService projectWidgetService,
                                    final ProjectWidgetMapper projectWidgetMapper,
                                    final NashornService nashornService,
                                    final ApplicationContext applicationContext) {
        this.dashboardWebSocketService = dashboardWebSocketService;
        this.projectService = projectService;
        this.projectWidgetService = projectWidgetService;
        this.projectWidgetMapper = projectWidgetMapper;
        this.nashornService = nashornService;
        this.applicationContext = applicationContext;
    }

    /**
     * Process the Nashorn response
     *
     * Update the widget information.
     * If the Nashorn execution is successful then update the data.
     * If the Nashorn execution is failed, then just update the log.
     *
     * Schedule the next Nashorn execution except if the current execution did not throw a fatal error
     *
     * @param nashornResponse The Nashorn response
     * @param scheduler       The Nashorn requests scheduler
     */
    @Transactional
    public void processNashornRequestResponse(NashornResponse nashornResponse,
                                              NashornRequestWidgetExecutionScheduler scheduler) {
        if (nashornResponse.isValid()) {
            LOGGER.debug("The Nashorn response is valid for the widget instance: {}. Updating widget in database",
                    nashornResponse.getProjectWidgetId());

            projectWidgetService.updateWidgetInstanceAfterSucceededExecution(nashornResponse.getLaunchDate(),
                    nashornResponse.getLog(),
                    nashornResponse.getData(),
                    nashornResponse.getProjectWidgetId(),
                    WidgetStateEnum.RUNNING);
        } else {
            LOGGER.debug("The Nashorn response is not valid for the widget instance: {}. Logs: {}. Response data: {}",
                    nashornResponse.getProjectWidgetId(), nashornResponse.getLog(), nashornResponse);

            projectWidgetService.updateWidgetInstanceAfterFailedExecution(nashornResponse.getLaunchDate(),
                    nashornResponse.getLog(),
                    nashornResponse.getProjectWidgetId(),
                    nashornResponse.getError() == NashornErrorTypeEnum.FATAL ? WidgetStateEnum.STOPPED : WidgetStateEnum.WARNING);
        }

        if (nashornResponse.isFatal()) {
            LOGGER.debug("The Nashorn response contains a fatal error for the widget instance: {}. Logs: {}. Response data: {}",
                    nashornResponse.getProjectWidgetId(), nashornResponse.getLog(), nashornResponse);
        } else {
            NashornRequest newNashornRequest = nashornService.getNashornRequestByProjectWidgetId(nashornResponse.getProjectWidgetId());
            scheduler.schedule(newNashornRequest, false);
        }

        sendWidgetUpdateNotification(nashornResponse.getProjectWidgetId(), nashornResponse.getProjectId());
    }

    /**
     * Update the widget information when there is no Nashorn response due to a failure
     *
     * @param widgetLogs      The exception message to log
     * @param projectWidgetId The widget instance id
     * @param projectId       The project id
     */
    @Transactional
    public void updateWidgetInstanceNoNashornResponse(String widgetLogs, Long projectWidgetId, Long projectId) {
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
                .type(UpdateType.WIDGET)
                .content(this.projectWidgetMapper.toProjectWidgetDTO(projectWidget))
                .build();

        dashboardWebSocketService.sendEventToWidgetInstanceSubscribers(projectService.getTokenByProjectId(projectId), projectWidgetId, event);
    }

    /**
     * Schedule the execution of a given widget instance.
     * Prepare the Nashorn request then cancel the current request
     * and schedule a new request.
     *
     * @param projectWidgetId The widget instance ID
     */
    public void scheduleWidget(final Long projectWidgetId) {
        NashornRequest nashornRequest = nashornService.getNashornRequestByProjectWidgetId(projectWidgetId);
        applicationContext.getBean(NashornRequestWidgetExecutionScheduler.class).cancelAndScheduleNashornRequest(nashornRequest);
    }
}
