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

package io.suricate.monitoring.service;

import io.suricate.monitoring.model.enums.NashornErrorTypeEnum;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.model.dto.UpdateEvent;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.repository.ProjectWidgetRepository;
import io.suricate.monitoring.service.api.WidgetService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class ScheduleService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private ProjectWidgetRepository projectWidgetRepository;

    @Autowired
    private SocketService socketService;

    @Autowired
    private WidgetService widgetService;

    /**
     * Method used to handle nashorn process response
     * @param nashornResponse nashorn response
     * @param callBack the callback used schedule an other project
     */
    @Transactional
    public void handleResponse(NashornResponse nashornResponse, Schedulable callBack){
        updateData(nashornResponse);
        // check if the request is isValid
        if (!nashornResponse.isValid()){
            LOGGER.error("Error for widget instance: {}, log: {}, data: {}", nashornResponse.getProjectWidgetId(), nashornResponse.getLog(), nashornResponse);
        }
        // Stop execution if a fatal error is detected
        if (!nashornResponse.isFatal()) {
            LOGGER.debug("Schedule widget instance: {}", nashornResponse.getProjectWidgetId());
            callBack.schedule(projectWidgetRepository.getRequestByProjectWidgetId(nashornResponse.getProjectWidgetId()), false, false);
        }
        notifyWidgetUpdate(nashornResponse.getProjectWidgetId(), nashornResponse.getProjectId());
    }


    /**
     * Method used to notify and update the widget on dashboard
     * @param projetWidgetId project widget Id
     * @param projectId project Id
     */
    private void notifyWidgetUpdate(Long projetWidgetId, Long projectId) {
        // Notify the dashboard
        UpdateEvent event = new UpdateEvent(UpdateType.WIDGET);
        event.setContent(widgetService.getWidgetResponse(projectWidgetRepository.findOne(projetWidgetId)));
        socketService.updateProjectScreen(projectId, event);
    }

    /**
     * Method used to update logs
     * @param e exception throw
     * @param projectWidgetId the widget instance id
     * @param projectId the project id
     */
    @Transactional
    public void updateLogException(Exception e ,Long projectWidgetId, Long projectId){
        projectWidgetRepository.updateExecutionLog(new Date(), ExceptionUtils.getMessage(e), projectWidgetId, WidgetState.STOPPED);
        notifyWidgetUpdate(projectWidgetId, projectId);
    }


    /**
     * Method used to update data after processing
     * @param nashornResponse the update data returned by the nashorn script
     */
    private void updateData(NashornResponse nashornResponse){
        if (nashornResponse.isValid()) {
            projectWidgetRepository.updateSuccessExecution(nashornResponse.getLaunchDate(), nashornResponse.getLog(), nashornResponse.getData(), nashornResponse.getProjectWidgetId(), WidgetState.RUNNING);
        } else {
            WidgetState state = nashornResponse.getError() == NashornErrorTypeEnum.FATAL ? WidgetState.STOPPED : WidgetState.WARNING;
            projectWidgetRepository.updateExecutionLog(nashornResponse.getLaunchDate(), nashornResponse.getLog(), nashornResponse.getProjectWidgetId(), state);
        }
    }

}
