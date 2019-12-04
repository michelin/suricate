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

package io.suricate.monitoring.service.scheduler;

import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.enums.NashornErrorTypeEnum;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.service.mapper.ProjectWidgetMapper;
import io.suricate.monitoring.service.nashorn.NashornService;
import io.suricate.monitoring.service.websocket.DashboardWebSocketService;
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
     * The application context
     */
    private final ApplicationContext applicationContext;

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
     * Method used to handle nashorn process response
     *
     * @param nashornResponse nashorn response
     * @param callBack        the callback used schedule an other project
     */
    @Transactional
    public void handleResponse(NashornResponse nashornResponse, Schedulable callBack) {
        updateData(nashornResponse);
        // check if the request is isValid
        if (!nashornResponse.isValid()) {
            LOGGER.error("Error for widget instance: {}, log: {}, data: {}", nashornResponse.getProjectWidgetId(), nashornResponse.getLog(), nashornResponse);
        }
        // Stop execution if a fatal error is detected
        if (!nashornResponse.isFatal()) {
            LOGGER.debug("Schedule widget instance: {}", nashornResponse.getProjectWidgetId());
            NashornRequest newNashornRequest = nashornService.getNashornRequestByProjectWidgetId(nashornResponse.getProjectWidgetId());
            callBack.schedule(newNashornRequest, false, false);
        }
        notifyWidgetUpdate(nashornResponse.getProjectWidgetId(), nashornResponse.getProjectId());
    }


    /**
     * Method used to notify and update the widget on dashboard
     *
     * @param projectWidgetId project widget Id
     * @param projectId       project Id
     */
    private void notifyWidgetUpdate(Long projectWidgetId, Long projectId) {
        // Notify the dashboard
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
        applicationContext.getBean(NashornWidgetScheduler.class).cancelAndSchedule(nashornRequest);
    }

    /**
     * Method used to update logs
     *
     * @param exception       exception throw
     * @param projectWidgetId the widget instance id
     * @param projectId       the project id
     */
    @Transactional
    public void updateLogException(Exception exception, Long projectWidgetId, Long projectId) {
        projectWidgetService.updateLogExecution(new Date(), ExceptionUtils.getMessage(exception), projectWidgetId, WidgetState.STOPPED);
        notifyWidgetUpdate(projectWidgetId, projectId);
    }


    /**
     * Method used to update data after processing
     *
     * @param nashornResponse the update data returned by the nashorn script
     */
    private void updateData(NashornResponse nashornResponse) {
        if (nashornResponse.isValid()) {
            projectWidgetService.updateSuccessExecution(nashornResponse.getProjectWidgetId(), nashornResponse.getLaunchDate(), nashornResponse.getLog(), nashornResponse.getData(), WidgetState.RUNNING);
        } else {
            WidgetState state = nashornResponse.getError() == NashornErrorTypeEnum.FATAL ? WidgetState.STOPPED : WidgetState.WARNING;
            projectWidgetService.updateLogExecution(nashornResponse.getLaunchDate(), nashornResponse.getLog(), nashornResponse.getProjectWidgetId(), state);
        }
    }

}
