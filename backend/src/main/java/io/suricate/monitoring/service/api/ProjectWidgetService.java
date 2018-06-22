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

package io.suricate.monitoring.service.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import io.suricate.monitoring.model.dto.project.ProjectWidgetPositionDto;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.model.mapper.project.ProjectMapper;
import io.suricate.monitoring.model.mapper.project.ProjectWidgetMapper;
import io.suricate.monitoring.repository.ProjectWidgetRepository;
import io.suricate.monitoring.service.scheduler.DashboardScheduleService;
import io.suricate.monitoring.service.scheduler.NashornWidgetScheduler;
import io.suricate.monitoring.service.webSocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.JavascriptUtils;
import io.suricate.monitoring.utils.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Project widget service
 */
@Service
public class ProjectWidgetService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectWidgetService.class);

    /**
     * The project widget repository
     */
    private final ProjectWidgetRepository projectWidgetRepository;

    /**
     * The dashboard websocket service
     */
    private final DashboardWebSocketService dashboardWebsocketService;

    /**
     * The dashboard schedule service
     */
    private final DashboardScheduleService dashboardScheduleService;

    /**
     * The mustacheFactory
     */
    private final MustacheFactory mustacheFactory;

    /**
     * The project mapper used for manage model/dto object
     */
    private final ProjectMapper projectMapper;

    /**
     * The application context
     */
    private final ApplicationContext ctx;

    /**
     * Constructor
     *
     * @param projectWidgetRepository   The project widget repository
     * @param dashboardWebSocketService The dashboard websocket service
     * @param dashboardScheduleService  The dashboard scheduler
     * @param mustacheFactory           The mustache factory (HTML template)
     * @param projectMapper             The project mapper
     * @param ctx                       The application context
     */
    @Autowired
    public ProjectWidgetService(final ProjectWidgetRepository projectWidgetRepository,
                                final MustacheFactory mustacheFactory,
                                final DashboardWebSocketService dashboardWebSocketService,
                                @Lazy final DashboardScheduleService dashboardScheduleService,
                                final ProjectMapper projectMapper,
                                final ApplicationContext ctx) {
        this.projectWidgetRepository = projectWidgetRepository;
        this.dashboardWebsocketService = dashboardWebSocketService;
        this.dashboardScheduleService = dashboardScheduleService;
        this.mustacheFactory = mustacheFactory;
        this.projectMapper = projectMapper;
        this.ctx = ctx;
    }

    /**
     * Get all the project widget in database
     *
     * @return The list of project widget
     */
    public List<ProjectWidget> getAll() {
        return this.projectWidgetRepository.findAll();
    }

    /**
     * Get the project widget by id
     *
     * @param projectWidgetId The project widget id
     * @return The project widget
     */
    public ProjectWidget getOne(final Long projectWidgetId) {
        return projectWidgetRepository.getOne(projectWidgetId);
    }

    /**
     * Find a project widget by the project id and the project widget id
     *
     * @param projectId       The project id
     * @param projectWidgetId The project widget id
     * @return The project widget as Optional
     */
    public Optional<ProjectWidget> findByProjectIdAndProjectWidgetId(final Long projectId, final Long projectWidgetId) {
        return projectWidgetRepository.findByIdAndProject_Id(projectWidgetId, projectId);
    }

    /**
     * Add a new project widget
     *
     * @param projectWidget The project widget to add
     * @return The projectWidget instantiate
     */
    @Transactional
    public ProjectWidget addProjectWidget(ProjectWidget projectWidget) {

        // Add project widget
        projectWidget = projectWidgetRepository.saveAndFlush(projectWidget);
        dashboardScheduleService.scheduleWidget(projectWidget.getId());

        // Update grid
        UpdateEvent updateEvent = new UpdateEvent(UpdateType.GRID);
        updateEvent.setContent(projectMapper.toProjectDtoDefault(projectWidget.getProject()));
        dashboardWebsocketService.updateGlobalScreensByProjectToken(projectWidget.getProject().getToken(), updateEvent);

        return projectWidget;
    }

    /**
     * Update the position of a widget
     *
     * @param projectWidgetId The projectWidget id
     * @param startCol        The new start col
     * @param startRow        The new start row
     * @param height          The new Height
     * @param width           The new width
     */
    public void updateWidgetPositionByProjectWidgetId(final Long projectWidgetId, final int startCol, final int startRow, final int height, final int width) {
        projectWidgetRepository.updateRowAndColAndWidthAndHeightById(startRow, startCol, width, height, projectWidgetId);
    }

    /**
     * Method used to update all widgets positions for a current project
     *
     * @param project   the project to update
     * @param positions lit of position
     */
    @Transactional
    public void updateWidgetPositionByProject(Project project, final List<ProjectWidgetPositionDto> positions) {
        for (ProjectWidgetPositionDto projectWidgetPositionDto : positions) {
            updateWidgetPositionByProjectWidgetId(
                projectWidgetPositionDto.getProjectWidgetId(),
                projectWidgetPositionDto.getCol(),
                projectWidgetPositionDto.getRow(),
                projectWidgetPositionDto.getHeight(),
                projectWidgetPositionDto.getWidth()
            );
        }
        projectWidgetRepository.flush();
        // notify clients
        UpdateEvent updateEvent = new UpdateEvent(UpdateType.POSITION);
        updateEvent.setContent(projectMapper.toProjectDtoDefault(project));
        dashboardWebsocketService.updateGlobalScreensByProjectToken(project.getToken(), updateEvent);
    }

    /**
     * Method used to remove widget from the dashboard
     *
     * @param project         the project
     * @param projectWidgetId the projectwidget id
     */
    @Transactional
    public void removeWidgetFromDashboard(Project project, Long projectWidgetId) {
        ctx.getBean(NashornWidgetScheduler.class).cancelWidgetInstance(projectWidgetId);
        projectWidgetRepository.deleteByProjectIdAndId(project.getId(), projectWidgetId);
        projectWidgetRepository.flush();

        // notify client
        UpdateEvent updateEvent = new UpdateEvent(UpdateType.GRID);
        updateEvent.setContent(projectMapper.toProjectDtoDefault(project));
        dashboardWebsocketService.updateGlobalScreensByProjectId(project.getId(), updateEvent);
    }


    /**
     * Reset the execution state of a project widget
     */
    public void resetProjectWidgetsState() {
        this.projectWidgetRepository.resetProjectWidgetsState();
    }

    /**
     * Method used to update application state
     *
     * @param widgetState widget state
     * @param id          project widget id
     */
    @Transactional
    public void updateState(WidgetState widgetState, Long id) {
        updateState(widgetState, id, null);
    }

    /**
     * Method used to update application state
     *
     * @param widgetState widget state
     * @param id          project widget id
     * @param date        The last execution date
     */
    @Transactional
    public void updateState(WidgetState widgetState, Long id, Date date) {
        ProjectWidget projectWidget = getOne(id);
        projectWidget.setState(widgetState);

        if (date != null) {
            projectWidget.setLastExecutionDate(date);
        }

        projectWidgetRepository.saveAndFlush(projectWidget);
    }

    /**
     * Method used to get instantiate html for a projectWidget
     * Call inside {@link ProjectWidgetMapper}
     *
     * @param projectWidget the project widget
     * @return The html instantiate
     */
    @Transactional
    public String instantiateProjectWidgetHtml(ProjectWidget projectWidget) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = null;
        Widget widget = projectWidget.getWidget();

        String instantiateHtml = widget.getHtmlContent();
        if (StringUtils.isNotEmpty(projectWidget.getData())) {
            try {
                map = objectMapper.readValue(
                    projectWidget.getData(),
                    new TypeReference<Map<String, Object>>() {
                    }
                );
                // Add backend config
                map.putAll(PropertiesUtils.getMap(projectWidget.getBackendConfig()));
                map.put(JavascriptUtils.INSTANCE_ID_VARIABLE, projectWidget.getId());
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }

            StringWriter stringWriter = new StringWriter();
            try {
                Mustache mustache = mustacheFactory.compile(new StringReader(instantiateHtml), widget.getTechnicalName());
                mustache.execute(stringWriter, map);
            } catch (MustacheException me) {
                LOGGER.error("Error with mustache template for widget {}", widget.getTechnicalName(), me);
            }
            stringWriter.flush();
            instantiateHtml = stringWriter.toString();
        }

        return instantiateHtml;
    }

    /**
     * Method used to update the configuration and custom css for project widget
     *
     * @param projectWidget The project widget id
     * @param customStyle   The new css style
     * @param backendConfig The new config
     */
    @Transactional
    public void updateProjectWidget(ProjectWidget projectWidget, final String customStyle, final String backendConfig) {
        ctx.getBean(NashornWidgetScheduler.class).cancelWidgetInstance(projectWidget.getId());

        projectWidget.setCustomStyle(customStyle);
        projectWidget.setBackendConfig(backendConfig);
        projectWidgetRepository.save(projectWidget);

        dashboardScheduleService.scheduleWidget(projectWidget.getId());

        // notify client
        UpdateEvent updateEvent = new UpdateEvent(UpdateType.GRID);
        updateEvent.setContent(projectMapper.toProjectDtoDefault(projectWidget.getProject()));
        dashboardWebsocketService.updateGlobalScreensByProjectId(projectWidget.getProject().getId(), updateEvent);
    }

    /**
     * Update nashorn execution log
     *
     * @param executionDate   The execution date
     * @param log             The message to log
     * @param projectWidgetId The project widget id to update
     * @param widgetState     The widget sate
     */
    public void updateLogExecution(final Date executionDate, final String log, final Long projectWidgetId, final WidgetState widgetState) {
        projectWidgetRepository.updateExecutionLog(executionDate, log, projectWidgetId, widgetState);
    }

    /**
     * Update project widget when nashorn execution is a success
     *
     * @param projectWidgetId The projectWidget id
     * @param executionDate   The execution date
     * @param executionLog    The execution log
     * @param data            The data return by the execution
     * @param widgetState     The state of the widget
     */
    public void updateSuccessExecution(final Long projectWidgetId, final Date executionDate, final String executionLog, final String data, final WidgetState widgetState) {
        projectWidgetRepository.updateSuccessExecution(executionDate, executionLog, data, projectWidgetId, widgetState);
    }
}
