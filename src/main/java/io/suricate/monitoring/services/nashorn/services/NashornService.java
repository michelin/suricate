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
import io.suricate.monitoring.model.entities.CategoryParameter;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectWidget;
import io.suricate.monitoring.model.enums.WidgetStateEnum;
import io.suricate.monitoring.services.api.ProjectWidgetService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The nashorn service
 */
@Service
public class NashornService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NashornService.class.getName());

    /**
     * The project widget service
     */
    private final ProjectWidgetService projectWidgetService;

    /**
     * Constructor
     *
     * @param projectWidgetService The project widget service
     */
    @Autowired
    public NashornService(@Lazy final ProjectWidgetService projectWidgetService) {
        this.projectWidgetService = projectWidgetService;
    }

    /**
     * Get the list of related Nashorn requests for each widget of a project
     *
     * @param project The project
     * @return The list of related Nashorn requests
     */
    @Transactional
    public List<NashornRequest> getNashornRequestsByProject(final Project project) {
        return project
            .getWidgets()
            .stream()
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
        Long projectId = projectWidget.getProject().getId();
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
        if (!nashornRequest.isValid()) {
            LOGGER.debug("The Nashorn request is not valid for the widget instance: {}", nashornRequest.getProjectWidgetId());
            return false;
        }

        if (nashornRequest.getDelay() < 0) {
            LOGGER.debug("The Nashorn request has a delay < 0 for widget instance: {}", nashornRequest.getProjectWidgetId());
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
    private String getProjectWidgetConfigurationsWithGlobalOne(final ProjectWidget projectWidget, final List<CategoryParameter> categoryParameters) {
        StringBuilder builder = new StringBuilder(Objects.toString(projectWidget.getBackendConfig(), StringUtils.EMPTY));

        if (categoryParameters != null && !categoryParameters.isEmpty()) {
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
