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

package io.suricate.monitoring.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetPositionRequestDto;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectWidget;
import io.suricate.monitoring.model.entities.Widget;
import io.suricate.monitoring.model.entities.WidgetParam;
import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.model.enums.WidgetStateEnum;
import io.suricate.monitoring.repositories.ProjectWidgetRepository;
import io.suricate.monitoring.services.mapper.ProjectMapper;
import io.suricate.monitoring.services.nashorn.services.DashboardScheduleService;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.JavaScriptUtils;
import io.suricate.monitoring.utils.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * The widget service
     */
    private final WidgetService widgetService;

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
     * The string encryptor
     */
    private StringEncryptor stringEncryptor;

    /**
     * Constructor
     *
     * @param projectWidgetRepository   The project widget repository
     * @param dashboardWebSocketService The dashboard websocket service
     * @param dashboardScheduleService  The dashboard scheduler
     * @param widgetService             The widget service
     * @param mustacheFactory           The mustache factory (HTML template)
     * @param projectMapper             The project mapper
     * @param ctx                       The application context
     * @param stringEncryptor           The string encryptor
     */
    @Autowired
    public ProjectWidgetService(final ProjectWidgetRepository projectWidgetRepository,
                                final MustacheFactory mustacheFactory,
                                final DashboardWebSocketService dashboardWebSocketService,
                                @Lazy final DashboardScheduleService dashboardScheduleService,
                                final WidgetService widgetService,
                                final ProjectMapper projectMapper,
                                final ApplicationContext ctx,
                                @Qualifier("jasyptStringEncryptor") final StringEncryptor stringEncryptor) {
        this.projectWidgetRepository = projectWidgetRepository;
        this.dashboardWebsocketService = dashboardWebSocketService;
        this.dashboardScheduleService = dashboardScheduleService;
        this.widgetService = widgetService;
        this.mustacheFactory = mustacheFactory;
        this.projectMapper = projectMapper;
        this.ctx = ctx;
        this.stringEncryptor = stringEncryptor;
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
    @Transactional(readOnly = true)
    public Optional<ProjectWidget> getOne(final Long projectWidgetId) {
        return projectWidgetRepository.findById(projectWidgetId);
    }

    /**
     * Add a new widget instance to a project
     *
     * Encrypt the secret configuration of the widget instance
     *
     * Save it
     *
     * Send an event to the subscribers to update the dashboards
     *
     * @param widgetInstance The widget instance
     */
    @Transactional
    public void addWidgetInstanceToProject(ProjectWidget widgetInstance) {
        widgetInstance.setBackendConfig(
            encryptSecretParamsIfNeeded(widgetInstance.getWidget(), widgetInstance.getBackendConfig())
        );

        widgetInstance = projectWidgetRepository.saveAndFlush(widgetInstance);

        UpdateEvent updateEvent = new UpdateEvent(UpdateType.GRID);
        updateEvent.setContent(projectMapper.toProjectDTO(widgetInstance.getProject()));
        dashboardWebsocketService.sendEventToProjectSubscribers(widgetInstance.getProject().getToken(), updateEvent);
    }

    /**
     * Update the position of a widget
     *
     * @param projectWidgetId The projectWidgetId
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
    public void updateWidgetPositionByProject(Project project, final List<ProjectWidgetPositionRequestDto> positions) {
        for (ProjectWidgetPositionRequestDto projectWidgetPositionRequestDto : positions) {
            updateWidgetPositionByProjectWidgetId(
                projectWidgetPositionRequestDto.getProjectWidgetId(),
                projectWidgetPositionRequestDto.getGridColumn(),
                projectWidgetPositionRequestDto.getGridRow(),
                projectWidgetPositionRequestDto.getHeight(),
                projectWidgetPositionRequestDto.getWidth()
            );
        }
        projectWidgetRepository.flush();
        // notify clients
        UpdateEvent updateEvent = new UpdateEvent(UpdateType.POSITION);
        updateEvent.setContent(projectMapper.toProjectDTO(project));
        dashboardWebsocketService.sendEventToProjectSubscribers(project.getToken(), updateEvent);
    }

    /**
     * Method used to remove widget from the dashboard
     *
     * @param projectWidgetId the projectWidgetId id
     */
    @Transactional
    public void removeWidgetFromDashboard(Long projectWidgetId) {
        Optional<ProjectWidget> projectWidgetOptional = this.getOne(projectWidgetId);

        if (projectWidgetOptional.isPresent()) {
            ctx.getBean(NashornRequestWidgetExecutionScheduler.class).cancelWidgetExecution(projectWidgetId);

            projectWidgetRepository.deleteByProjectIdAndId(projectWidgetOptional.get().getProject().getId(), projectWidgetId);
            projectWidgetRepository.flush();

            // notify client
            UpdateEvent updateEvent = new UpdateEvent(UpdateType.GRID);
            updateEvent.setContent(projectMapper.toProjectDTO(projectWidgetOptional.get().getProject()));
            dashboardWebsocketService.updateGlobalScreensByProjectId(projectWidgetOptional.get().getProject().getId(), updateEvent);
        }
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
    public void updateState(WidgetStateEnum widgetState, Long id) {
        updateState(widgetState, id, null);
    }

    /**
     * Update the state of a widget instance
     *
     * @param widgetState The widget state
     * @param id          The project widget ID
     * @param date        The last execution date
     */
    @Transactional
    public void updateState(WidgetStateEnum widgetState, Long id, Date date) {
        Optional<ProjectWidget> projectWidgetOptional = this.getOne(id);

        if (projectWidgetOptional.isPresent()) {
            projectWidgetOptional.get().setState(widgetState);

            if (date != null) {
                projectWidgetOptional.get().setLastExecutionDate(date);
            }

            projectWidgetRepository.saveAndFlush(projectWidgetOptional.get());
        }
    }

    /**
     * Instantiate the HTML of a widget with the data resulting from
     * the Nashorn execution.
     *
     * @param projectWidget the widget instance
     * @return The instantiated HTML
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
                map.putAll(PropertiesUtils.convertStringWidgetPropertiesToMap(projectWidget.getBackendConfig()));
                map.put(JavaScriptUtils.WIDGET_INSTANCE_ID_VARIABLE, projectWidget.getId());

                // Add global variables if needed
                for (WidgetParam widgetParam : widgetService.getWidgetParametersWithCategoryParameters(projectWidget.getWidget())) {
                    if (!map.containsKey(widgetParam.getName()) && widgetParam.isRequired()) {
                        map.put(widgetParam.getName(), widgetParam.getDefaultValue());
                    }
                }

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
     * Update the configuration and the custom CSS for a widget instance.
     * Then schedule a new Nashorn execution for the updated widget.
     *
     * @param projectWidget The widget instance
     * @param customStyle   The new CSS style
     * @param backendConfig The new configuration
     */
    @Transactional
    public void updateProjectWidget(ProjectWidget projectWidget, final String customStyle, final String backendConfig) {
        ctx.getBean(NashornRequestWidgetExecutionScheduler.class).cancelWidgetExecution(projectWidget.getId());

        if (customStyle != null) {
            projectWidget.setCustomStyle(customStyle);
        }

        if (backendConfig != null) {
            projectWidget.setBackendConfig(
                encryptSecretParamsIfNeeded(projectWidget.getWidget(), backendConfig)
            );
        }

        projectWidgetRepository.save(projectWidget);

        dashboardScheduleService.scheduleWidget(projectWidget.getId());
    }

    /**
     * Update the state of a widget instance when Nashorn execution ends with a failure
     *
     * @param executionDate   The execution date
     * @param log             The message to log
     * @param projectWidgetId The project widget id to update
     * @param widgetState     The widget sate
     */
    public void updateWidgetInstanceAfterFailedExecution(final Date executionDate, final String log, final Long projectWidgetId, final WidgetStateEnum widgetState) {
        projectWidgetRepository.updateLastExecutionDateAndStateAndLog(executionDate, log, projectWidgetId, widgetState);
    }

    /**
     * Update the state of a widget instance when Nashorn execution ends successfully
     *
     * @param executionDate The last execution date
     * @param executionLog  The log of nashorn execution
     * @param data          The data returned by nashorn
     * @param id            The id of the project widget
     * @param widgetState   The widget state
     */
    public void updateWidgetInstanceAfterSucceededExecution(final Date executionDate, final String executionLog, final String data, final Long projectWidgetId, final WidgetStateEnum widgetState) {
        projectWidgetRepository.updateSuccessExecution(executionDate, executionLog, data, projectWidgetId, widgetState);
    }

    /**
     * decrypt the secret params if exists
     *
     * @param widget        The widget related to project widget
     * @param backendConfig The related backend config
     * @return The list of param decrypted
     */
    public String decryptSecretParamsIfNeeded(final Widget widget, String backendConfig) {
        Map<String, String> backendConfigAsMap = PropertiesUtils.convertStringWidgetPropertiesToMap(backendConfig);

        List<WidgetParam> widgetParams = widgetService.getWidgetParametersWithCategoryParameters(widget);
        for (WidgetParam widgetParam : widgetParams) {
            if (widgetParam.getType() == DataTypeEnum.PASSWORD) {
                String valueToEncrypt = StringUtils.trimToNull(backendConfigAsMap.get(widgetParam.getName()));

                if (valueToEncrypt != null) {
                    backendConfigAsMap.put(widgetParam.getName(), stringEncryptor.decrypt(valueToEncrypt));
                }
            }
        }

        return backendConfigAsMap
            .entrySet()
            .stream()
            .filter(backendConfigEntrySet -> backendConfigEntrySet.getValue() != null)
            .map(backendConfigEntrySet -> backendConfigEntrySet.getKey() + "=" + backendConfigEntrySet.getValue())
            .collect(Collectors.joining("\n"));
    }

    /**
     * Method that encrypt secret params if need
     *
     * @param widget        The widget related to project widget
     * @param backendConfig The backend config of project widget
     * @return The backend config with the secret params encrypted
     */
    private String encryptSecretParamsIfNeeded(final Widget widget, String backendConfig) {
        Map<String, String> backendConfigAsMap = PropertiesUtils.convertStringWidgetPropertiesToMap(backendConfig);

        List<WidgetParam> widgetParams = widgetService.getWidgetParametersWithCategoryParameters(widget);
        for (WidgetParam widgetParam : widgetParams) {
            if (widgetParam.getType() == DataTypeEnum.PASSWORD) {
                String valueToEncrypt = backendConfigAsMap.get(widgetParam.getName());

                if (valueToEncrypt != null) {
                    backendConfigAsMap.put(widgetParam.getName(), stringEncryptor.encrypt(valueToEncrypt));
                }
            }
        }

        return backendConfigAsMap
            .entrySet()
            .stream()
            .map(backendConfigEntrySet -> backendConfigEntrySet.getKey() + "=" + backendConfigEntrySet.getValue())
            .collect(Collectors.joining("\n"));
    }
}
