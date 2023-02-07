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

package com.michelin.suricate.services.nashorn.services;

import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectGrid;
import com.michelin.suricate.services.api.ProjectWidgetService;
import com.michelin.suricate.model.dto.nashorn.NashornRequest;
import com.michelin.suricate.model.entities.CategoryParameter;
import com.michelin.suricate.model.entities.ProjectWidget;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import com.michelin.suricate.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NashornService {
    @Lazy
    @Autowired
    private ProjectWidgetService projectWidgetService;

    /**
     * Get the list of related Nashorn requests for each widget of a project
     *
     * @param project The project
     * @return The list of related Nashorn requests
     */
    @Transactional
    public List<NashornRequest> getNashornRequestsByProject(final Project project) {
        return project.getGrids()
            .stream()
            .map(ProjectGrid::getWidgets)
            .flatMap(Collection::stream)
            .map(this::createNashornRequestByProjectWidget)
            .collect(Collectors.toList());
    }

    /**
     * Create a nashorn request by a project widget id
     *
     * @param projectWidgetId The project widget id
     * @return The related nashorn request
     */
    public NashornRequest getNashornRequestByProjectWidgetId(final Long projectWidgetId) {
        Optional<ProjectWidget> projectWidgetOptional = projectWidgetService.getOne(projectWidgetId);
        return createNashornRequestByProjectWidget(projectWidgetOptional.orElse(new ProjectWidget()));
    }

    /**
     * Create a Nashorn request by a project widget
     *
     * @param projectWidget The project widget
     * @return The related Nashorn request
     */
    private NashornRequest createNashornRequestByProjectWidget(final ProjectWidget projectWidget) {
        String properties = getProjectWidgetConfigurationsWithGlobalOne(projectWidget, projectWidget.getWidget().getCategory().getConfigurations());
        String script = projectWidget.getWidget().getBackendJs();
        String previousData = projectWidget.getData();
        Long projectId = projectWidget.getProjectGrid().getProject().getId();
        Long technicalId = projectWidget.getId();
        Long delay = projectWidget.getWidget().getDelay();
        Long timeout = projectWidget.getWidget().getTimeout();
        WidgetStateEnum state = projectWidget.getState();
        Date lastSuccess = projectWidget.getLastSuccessDate();

        return new NashornRequest(properties, script, previousData, projectId, technicalId, delay, timeout, state, lastSuccess);
    }

    /**
     * Check if the given Nashorn request can be executed
     *
     * @param nashornRequest The nashorn request to check
     * @return True is it is ok, false otherwise
     */
    public boolean isNashornRequestExecutable(final NashornRequest nashornRequest) {
        if (!StringUtils.isNotEmpty(nashornRequest.getScript())) {
            log.debug("The widget instance {} has no script. Stopping Nashorn request execution",
                    nashornRequest.getProjectWidgetId());
            return false;
        }

        if (!JsonUtils.isValid(nashornRequest.getPreviousData())) {
            log.debug("The widget instance {} has bad formed previous data. Stopping Nashorn request execution",
                    nashornRequest.getProjectWidgetId());
            return false;
        }

        if (nashornRequest.getDelay() == null || nashornRequest.getDelay() < 0) {
            log.debug("The widget instance {} has no delay or delay is < 0. Stopping Nashorn request execution",
                    nashornRequest.getProjectWidgetId());
            return false;
        }

        return true;
    }

    /**
     * Get the project widget configurations with the global ones
     *
     * @param projectWidget        The project widget
     * @param categoryParameters The global configurations
     * @return Get the full configuration for project widget
     */
    private String getProjectWidgetConfigurationsWithGlobalOne(final ProjectWidget projectWidget, final Set<CategoryParameter> categoryParameters) {
        StringBuilder builder = new StringBuilder(Objects.toString(projectWidget.getBackendConfig(), StringUtils.EMPTY));

        if (!categoryParameters.isEmpty()) {
            builder.append('\n');

            for (CategoryParameter categoryParameter : categoryParameters) {
                if (!projectWidget.getBackendConfig().contains(categoryParameter.getKey())) {
                    builder
                        .append(categoryParameter.getKey())
                        .append('=')
                        .append(categoryParameter.getValue())
                        .append('\n');
                }
            }
        }


        return builder.toString();
    }
}
