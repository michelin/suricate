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

import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.repository.ProjectWidgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Project widget service
 */
@Service
public class ProjectWidgetService {

    /**
     * The project widget repository
     */
    private final ProjectWidgetRepository projectWidgetRepository;

    /**
     * Constructor
     *
     * @param projectWidgetRepository The project widget repository
     */
    @Autowired
    public ProjectWidgetService(final ProjectWidgetRepository projectWidgetRepository) {
        this.projectWidgetRepository = projectWidgetRepository;
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
}
