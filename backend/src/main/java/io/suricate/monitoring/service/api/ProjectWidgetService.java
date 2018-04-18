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
import io.suricate.monitoring.model.dto.project.ProjectWidgetDto;
import io.suricate.monitoring.model.dto.project.ProjectWidgetPositionDto;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.repository.ProjectWidgetRepository;
import io.suricate.monitoring.utils.JavascriptUtils;
import io.suricate.monitoring.utils.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * The project service
     */
    private final ProjectService projectService;

    /**
     * The widget service
     */
    private final WidgetService widgetService;

    /**
     * The mustacheFactory
     */
    private final MustacheFactory mustacheFactory;

    /**
     * Constructor
     *
     * @param projectWidgetRepository The project widget repository
     * @param projectService The project service
     * @param widgetService The widget service
     * @param mustacheFactory The mustache factory (HTML template)
     */
    @Autowired
    public ProjectWidgetService(final ProjectWidgetRepository projectWidgetRepository,
                                final ProjectService projectService,
                                final WidgetService widgetService, final MustacheFactory mustacheFactory) {
        this.projectWidgetRepository = projectWidgetRepository;
        this.projectService = projectService;
        this.widgetService = widgetService;
        this.mustacheFactory = mustacheFactory;
    }

    /**
     * Treansform a project widget into a dto object
     *
     * @param projectWidget The project widget to transform
     * @return The related dto
     */
    public ProjectWidgetDto tranformIntoDto(final ProjectWidget projectWidget) {
        ProjectWidgetPositionDto projectWidgetPositionDto = new ProjectWidgetPositionDto();
        projectWidgetPositionDto.setRow(projectWidget.getRow());
        projectWidgetPositionDto.setCol(projectWidget.getCol());
        projectWidgetPositionDto.setHeight(projectWidget.getHeight());
        projectWidgetPositionDto.setWidth(projectWidget.getWidth());

        ProjectWidgetDto projectWidgetDto = new ProjectWidgetDto();
        projectWidgetDto.setId(projectWidget.getId());
        projectWidgetDto.setData(projectWidget.getData());
        projectWidgetDto.setWidgetPosition(projectWidgetPositionDto);
        projectWidgetDto.setCustomStyle(StringUtils.trimToEmpty(projectWidget.getCustomStyle()));
        projectWidgetDto.setBackendConfig(projectWidget.getBackendConfig());
        projectWidgetDto.setLog(projectWidget.getLog());
        projectWidgetDto.setLastExecutionDate(projectWidget.getLastExecutionDate());
        projectWidgetDto.setLastSuccessDate(projectWidget.getLastSuccessDate());
        projectWidgetDto.setState(projectWidget.getState());
        projectWidgetDto.setProject(projectService.toDTO(projectWidget.getProject(), false));
        projectWidgetDto.setWidget(widgetService.tranformIntoDto(projectWidget.getWidget()));


        return projectWidgetDto;
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
     * Get all by Project widget by project id and widget availability
     *
     * @param projectId The project id
     * @param widgetAvailabilityEnum The widget availability enum
     * @return The list of related project widget
     */
    public List<ProjectWidget> getAllByProjectIdAndWidgetAvailability(final Long projectId, final WidgetAvailabilityEnum widgetAvailabilityEnum) {
        return projectWidgetRepository.findByProjectIdAndWidget_WidgetAvailabilityOrderById(projectId, widgetAvailabilityEnum);
    }

    /**
     * Save and flush a project widget
     *
     * @param projectWidget The project widget to save
     * @return The project widget saved
     */
    public ProjectWidget saveAndFlush(ProjectWidget projectWidget) {
        return projectWidgetRepository.saveAndFlush(projectWidget);
    }

    /**
     * Reset the execution state of a project widget
     */
    public void resetProjectWidgetsState() {
        this.projectWidgetRepository.resetProjectWidgetsState();
    }

    /**
     * Method used to update application state
     * @param widgetState widget state
     * @param id project widget id
     */
    @Transactional
    public void updateState(WidgetState widgetState, Long id, Date date){
        projectWidgetRepository.updateState(widgetState, id, date);
    }

    /**
     * Method used to get widget response from a project widget
     * @param projectWidget the project widget
     * @return a widgetresponse object
     */
    @Transactional
    public ProjectWidgetDto instantiateProjectWidget(ProjectWidget projectWidget) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = null;
        Widget widget = projectWidget.getWidget();

        String instantiateHtml = widget.getHtmlContent();
        if (StringUtils.isNotEmpty(projectWidget.getData())) {
            try {
                map = objectMapper.readValue(projectWidget.getData(), new TypeReference<Map<String, Object>>() {});
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
            } catch (MustacheException me){
                LOGGER.error("Error with mustache template for widget {}", widget.getTechnicalName(), me);
            }
            stringWriter.flush();
            instantiateHtml = stringWriter.toString();
        }

        // Create the response
        ProjectWidgetDto projectWidgetDto = tranformIntoDto(projectWidget);
        projectWidgetDto.setInstantiateHtml(instantiateHtml);

        return projectWidgetDto;
    }
}
