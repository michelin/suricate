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
import io.suricate.monitoring.model.enums.WidgetState;
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

import javax.transaction.Transactional;
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
     * Constructor
     *
     * @param dashboardWebSocketService The dashboard websocket service
     * @param projectWidgetService      The project widget service
     * @param projectWidgetMapper       The project widget mapper
     * @param nashornService            The nashorn service to inject
     * @param applicationContext        The application context to inject
     */
    @Autowired
    public DashboardScheduleService(final DashboardWebSocketService dashboardWebSocketService,
                                    final ProjectWidgetService projectWidgetService,
                                    final ProjectWidgetMapper projectWidgetMapper,
                                    final NashornService nashornService,
                                    final ApplicationContext applicationContext) {
        this.dashboardWebSocketService = dashboardWebSocketService;
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
     * Schedule the next Nashorn execution except if the current execution threw a fatal error
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
                    WidgetState.RUNNING);
        } else {
            LOGGER.debug("The Nashorn response is not valid for the widget instance: {}. Logs: {}. Response data: {}",
                    nashornResponse.getProjectWidgetId(), nashornResponse.getLog(), nashornResponse);

            projectWidgetService.updateWidgetInstanceAfterFailedExecution(nashornResponse.getLaunchDate(),
                    nashornResponse.getLog(),
                    nashornResponse.getProjectWidgetId(),
                    nashornResponse.getError() == NashornErrorTypeEnum.FATAL ? WidgetState.STOPPED : WidgetState.WARNING);
        }

        if (nashornResponse.isFatal()) {
            LOGGER.debug("The Nashorn response contains a fatal error for the widget instance: {}. Logs: {}. Response data: {}",
                    nashornResponse.getProjectWidgetId(), nashornResponse.getLog(), nashornResponse);
        } else {
            NashornRequest newNashornRequest = nashornService.getNashornRequestByProjectWidgetId(nashornResponse.getProjectWidgetId());
            scheduler.schedule(newNashornRequest, false, false);
        }

        sendWidgetUpdateNotification(nashornResponse.getProjectWidgetId(), nashornResponse.getProjectId());
    }

    /**
     * Update the widget information when there is no Nashorn response due to a failure
     *
     * @param exception       The exception thrown
     * @param projectWidgetId The widget instance id
     * @param projectId       The project id
     */
    @Transactional
    public void updateWidgetInstanceNoNashornResponse(Exception exception, Long projectWidgetId, Long projectId) {
        projectWidgetService.updateWidgetInstanceAfterFailedExecution(new Date(), ExceptionUtils.getMessage(exception), projectWidgetId, WidgetState.STOPPED);

        sendWidgetUpdateNotification(projectWidgetId, projectId);
    }

    /**
     * Create a new widget event which will be sent through the web sockets
     * to notify and update the widget on dashboard
     *
     * @param projectWidgetId The project widget ID
     * @param projectId       The project ID
     */
    private void sendWidgetUpdateNotification(Long projectWidgetId, Long projectId) {
        UpdateEvent event = new UpdateEvent(UpdateType.WIDGET);
        ProjectWidget projectWidget = projectWidgetService.getOne(projectWidgetId).orElse(null);
        event.setContent(projectWidgetMapper.toProjectWidgetDtoDefault(projectWidget));

        dashboardWebSocketService.updateGlobalScreensByIdAndProjectWidgetId(projectId, projectWidgetId, event);
    }

    /**
     * Method used to schedule a widget
     *
     * @param projectWidgetId The project widget id
     */
    @Transactional
    public void scheduleWidget(final Long projectWidgetId) {
        NashornRequest nashornRequest = nashornService.getNashornRequestByProjectWidgetId(projectWidgetId);
        applicationContext.getBean(NashornRequestWidgetExecutionScheduler.class).cancelAndScheduleNashornRequest(nashornRequest);
    }
}
