/*
 * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetPositionRequestDto;
import com.michelin.suricate.model.dto.websocket.UpdateEvent;
import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectWidget;
import com.michelin.suricate.model.entities.Widget;
import com.michelin.suricate.model.entities.WidgetParam;
import com.michelin.suricate.model.enums.DataTypeEnum;
import com.michelin.suricate.model.enums.UpdateType;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import com.michelin.suricate.repositories.ProjectWidgetRepository;
import com.michelin.suricate.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import com.michelin.suricate.services.nashorn.services.DashboardScheduleService;
import com.michelin.suricate.services.websocket.DashboardWebSocketService;
import com.michelin.suricate.utils.JavaScriptUtils;
import com.michelin.suricate.utils.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
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

@Slf4j
@Service
public class ProjectWidgetService {
    @Autowired
    private ProjectWidgetRepository projectWidgetRepository;

    @Autowired
    private DashboardWebSocketService dashboardWebsocketService;

    @Lazy
    @Autowired
    private DashboardScheduleService dashboardScheduleService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private MustacheFactory mustacheFactory;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    private StringEncryptor stringEncryptor;

    /**
     * Get all the project widget in database
     * @return The list of project widget
     */
    @Transactional(readOnly = true)
    public List<ProjectWidget> getAll() {
        return projectWidgetRepository.findAll();
    }

    /**
     * Get the project widget by id
     * @param projectWidgetId The project widget id
     * @return The project widget
     */
    @Transactional(readOnly = true)
    public Optional<ProjectWidget> getOne(final Long projectWidgetId) {
        return projectWidgetRepository.findById(projectWidgetId);
    }

    /**
     * Get the project widget by id
     * @param id The project widget id
     * @param gridId The grid id
     * @return The project widget
     */
    @Transactional(readOnly = true)
    public Optional<ProjectWidget> findByIdAndProjectGridId(final Long id, final Long gridId) {
        return projectWidgetRepository.findByIdAndProjectGridId(id, gridId);
    }

    /**
     * Persist a given project widget
     * Encrypt the secret configuration of the widget instance before saving it
     *
     * @param projectWidget The project widget
     */
    @Transactional
    public ProjectWidget create(ProjectWidget projectWidget) {
        projectWidget.setBackendConfig(encryptSecretParamsIfNeeded(projectWidget.getWidget(), projectWidget.getBackendConfig()));
        return projectWidgetRepository.save(projectWidget);
    }

    /**
     * Add a new widget instance to a project
     * Encrypt the secret configuration of the widget instance then save it
     * @param projectWidget The widget instance
     */
    @Transactional
    public void createAndRefreshDashboards(ProjectWidget projectWidget) {
        projectWidget.setBackendConfig(encryptSecretParamsIfNeeded(projectWidget.getWidget(), projectWidget.getBackendConfig()));

        projectWidgetRepository.saveAndFlush(projectWidget);

        UpdateEvent updateEvent = UpdateEvent.builder()
                .type(UpdateType.REFRESH_DASHBOARD)
                .build();

        dashboardWebsocketService.sendEventToProjectSubscribers(projectWidget.getProjectGrid().getProject().getToken(), updateEvent);
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
        UpdateEvent updateEvent = UpdateEvent.builder()
                .type(UpdateType.REFRESH_DASHBOARD)
                .build();

        dashboardWebsocketService.sendEventToProjectSubscribers(project.getToken(), updateEvent);
    }

    /**
     * Method used to remove widget from the dashboard
     *
     * @param projectWidgetId the projectWidgetId id
     */
    @Transactional
    public void removeWidgetFromDashboard(Long projectWidgetId) {
        Optional<ProjectWidget> projectWidgetOptional = getOne(projectWidgetId);

        if (projectWidgetOptional.isPresent()) {
            ctx.getBean(NashornRequestWidgetExecutionScheduler.class).cancelWidgetExecution(projectWidgetId);

            projectWidgetRepository.deleteById(projectWidgetId);
            projectWidgetRepository.flush();

            // notify client
            UpdateEvent updateEvent = UpdateEvent.builder()
                    .type(UpdateType.REFRESH_DASHBOARD)
                    .build();

            dashboardWebsocketService.sendEventToProjectSubscribers(projectWidgetOptional.get().getProjectGrid().getProject().getToken(), updateEvent);
        }
    }


    /**
     * Reset the execution state of a project widget
     */
    public void resetProjectWidgetsState() {
        projectWidgetRepository.resetProjectWidgetsState();
    }

    /**
     * Update widget state
     * @param widgetState widget state
     * @param id          project widget id
     */
    @Transactional
    public void updateState(WidgetStateEnum widgetState, Long id) {
        updateState(widgetState, id, null);
    }

    /**
     * Update the state of a widget instance
     * @param widgetState The widget state
     * @param id          The project widget ID
     * @param date        The last execution date
     */
    @Transactional
    public void updateState(WidgetStateEnum widgetState, Long id, Date date) {
        Optional<ProjectWidget> projectWidgetOptional = getOne(id);

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
                map = objectMapper.readValue(projectWidget.getData(), new TypeReference<Map<String, Object>>() {});
                // Add backend config
                map.putAll(PropertiesUtils.convertAndDecodeStringWidgetPropertiesToMap(projectWidget.getBackendConfig()));
                map.put(JavaScriptUtils.WIDGET_INSTANCE_ID_VARIABLE, projectWidget.getId());

                // Add global variables if needed
                for (WidgetParam widgetParam : widgetService.getWidgetParametersWithCategoryParameters(projectWidget.getWidget())) {
                    if (!map.containsKey(widgetParam.getName()) && widgetParam.isRequired()) {
                        map.put(widgetParam.getName(), widgetParam.getDefaultValue());
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

            StringWriter stringWriter = new StringWriter();
            try {
                Mustache mustache = mustacheFactory.compile(new StringReader(instantiateHtml), widget.getTechnicalName());
                mustache.execute(stringWriter, map);
            } catch (MustacheException me) {
                log.error("Error with mustache template for widget {}", widget.getTechnicalName(), me);
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
            projectWidget.setBackendConfig(encryptSecretParamsIfNeeded(projectWidget.getWidget(), backendConfig));
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
    public String encryptSecretParamsIfNeeded(final Widget widget, String backendConfig) {
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
