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

import io.suricate.monitoring.model.ProjectWidget;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.model.dto.nashorn.NashornRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ProjectWidgetRepository extends JpaRepository<ProjectWidget, Long> {

    /**
     * Method used to get all nashorn request object from database
     * @return
     */
    @Query("SELECT new io.suricate.monitoring.model.dto.nashorn.NashornRequest(pw.backendConfig, w.backendJs, pw.data, pw.project.id, pw.id, w.delay, w.timeout, pw.state, pw.lastSuccessDate) " +
            "FROM ProjectWidget pw, Widget w " +
            "WHERE pw.widget.id = w.id")
    List<NashornRequest> getAll();


    /**
     * Method used to get the nashorn request object for a specific projet and widget
     * @param id the widget id
     * @return the nashorn request
     */
    @Query("SELECT new io.suricate.monitoring.model.dto.nashorn.NashornRequest(pw.backendConfig, w.backendJs, pw.data, pw.project.id, pw.id, w.delay, w.timeout, pw.state, pw.lastSuccessDate) " +
            "FROM ProjectWidget pw, Widget w " +
            "WHERE pw.widget.id = w.id " +
            "AND pw.id = :id ")
    NashornRequest getRequestByProjectWidgetId(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ProjectWidget SET row = :row, " +
            "col = :col, " +
            "width = :width, " +
            "height = :height " +
            "WHERE id = :id")
    int updateRowAndColAndWidthAndHeightById(@Param("row") int row,@Param("col") int col,@Param("width") int width,@Param("height") int height,@Param("id") Long id );

    @Modifying
    @Query("UPDATE ProjectWidget " +
            "SET lastExecutionDate = :lastExecutionDate, " +
            "lastSuccessDate = :lastExecutionDate," +
            "state = :state, " +
            "log = :log, " +
            "data = :data " +
            "WHERE id = :id")
    int updateSuccessExecution(@Param("lastExecutionDate")Date date, @Param("log") String log, @Param("data") String data, @Param("id") Long id, @Param("state") WidgetState widgetState);

    @Modifying
    @Query("UPDATE ProjectWidget " +
            "SET lastExecutionDate = :lastExecutionDate, " +
            "state = :state, " +
            "log = :log " +
            "WHERE id = :id")
    int updateExecutionLog(@Param("lastExecutionDate")Date date, @Param("log") String log, @Param("id") Long id, @Param("state") WidgetState widgetState);

    @Modifying
    @Query("UPDATE ProjectWidget " +
            "SET state = :state, " +
            "lastExecutionDate = :lastExecutionDate " +
            "WHERE id = :id")
    int updateState(@Param("state") WidgetState widgetState, @Param("id") Long id, @Param("lastExecutionDate") Date lastExecutionDate);

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
     * @param style style id
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

    /**
     * Method used to reset the state of a widget instance
     */
    @Modifying
    @Query("UPDATE ProjectWidget " +
            "SET lastSuccessDate = null, " +
            "log = null," +
            "state = 'STOPPED'")
    void resetWidgetState();

}
