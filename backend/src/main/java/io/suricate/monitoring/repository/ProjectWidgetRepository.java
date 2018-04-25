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

package io.suricate.monitoring.repository;

import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Repository used for request Project widget in database
 */
public interface ProjectWidgetRepository extends JpaRepository<ProjectWidget, Long> {

    /**
     * Method used to reset the state of every widget instances
     */
    @Modifying
    @Query("UPDATE ProjectWidget " +
                "SET    lastSuccessDate = null, " +
                        "log = null," +
                        "state = 'STOPPED'")
    void resetProjectWidgetsState();

    /**
     * Update the position in the grid of a widget
     *
     * @param row The new start row number
     * @param col The new Start col number
     * @param width The new number of columns taken by the widget
     * @param height The new number of rows taken by the widget
     * @param id The project widget id
     * @return State of the update
     */
    @Modifying
    @Query("UPDATE ProjectWidget SET row = :row, " +
            "col = :col, " +
            "width = :width, " +
            "height = :height " +
            "WHERE id = :id")
    int updateRowAndColAndWidthAndHeightById(@Param("row") int row,@Param("col") int col,@Param("width") int width,@Param("height") int height,@Param("id") Long id );

    /**
     * Update the state of a project widget when nashorn execution end by a success
     *
     * @param date The last execution date
     * @param log The log of nashorn execution
     * @param data The data returned by nashorn
     * @param id The id of the project widget
     * @param widgetState The widget state
     * @return State of the query
     */
    @Modifying
    @Query("UPDATE ProjectWidget " +
            "SET lastExecutionDate = :lastExecutionDate, " +
            "lastSuccessDate = :lastExecutionDate," +
            "state = :state, " +
            "log = :log, " +
            "data = :data " +
            "WHERE id = :id")
    int updateSuccessExecution(@Param("lastExecutionDate")Date date, @Param("log") String log, @Param("data") String data, @Param("id") Long id, @Param("state") WidgetState widgetState);

    /**
     * Update the state of a project widget when nashorn execution end with errors
     *
     * @param date The last execution date
     * @param log The logs of the execution
     * @param id The project widget id
     * @param widgetState The widget state
     * @return State of the query
     */
    @Modifying
    @Query("UPDATE ProjectWidget " +
            "SET lastExecutionDate = :lastExecutionDate, " +
            "state = :state, " +
            "log = :log " +
            "WHERE id = :id")
    int updateExecutionLog(@Param("lastExecutionDate")Date date, @Param("log") String log, @Param("id") Long id, @Param("state") WidgetState widgetState);

    /**
     * Method used to find all project and widget for a project
     * @param projectId project id to find
     * @param widgetAvailability widget availability
     * @return the list of widget instance
     */
    List<ProjectWidget> findByProjectIdAndWidget_WidgetAvailabilityOrderById(Long projectId, WidgetAvailabilityEnum widgetAvailability);

    /**
     * Method used to delete a widget instance by it's id and the project id
     * @param projectId the project is
     * @param id the widget instance id
     * @return the number of deleted rows
     */
    Long deleteByProjectIdAndId(Long projectId, Long id);

    /**
     * Get project Widget Id from id and projectId
     * @param projectWidgetId project widget Id
     * @param projectId project id
     * @return project widget id
     */
    ProjectWidget findByIdAndProject_Id(Long projectWidgetId, Long projectId);

    /**
     * Method used to update the config of a widget instance
     * @param projectWidgetId the widget id
     * @param style The custom css
     * @param backendConfig widget instance configuration
     * @return the number of row updated
     */
    @Modifying
    @Query("UPDATE ProjectWidget " +
            "SET customStyle = :customStyle, " +
            "backendConfig = :backendConfig, " +
            "lastSuccessDate = null, " +
            "log = null " +
            "WHERE id = :id")
    int updateConfig(@Param("id") Long projectWidgetId, @Param("customStyle") String style,@Param("backendConfig") String backendConfig);

}
