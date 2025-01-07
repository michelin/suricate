/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.repository;

import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.enumeration.WidgetStateEnum;
import java.util.Date;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Project widget repository.
 */
@Repository
public interface ProjectWidgetRepository
    extends JpaRepository<ProjectWidget, Long>, JpaSpecificationExecutor<ProjectWidget> {
    /**
     * Find a widget instance by id.
     *
     * @param projectWidgetId The widget instance id
     * @return The widget instance
     */
    @NotNull
    @EntityGraph(attributePaths = {"projectGrid.project.users.roles", "widget.widgetParams.possibleValuesMap"})
    Optional<ProjectWidget> findById(@NotNull Long projectWidgetId);

    /**
     * Find a widget instance by id and grid id.
     *
     * @param id     The widget instance id
     * @param gridId The grid id
     * @return The widget instance
     */
    Optional<ProjectWidget> findByIdAndProjectGridId(Long id, Long gridId);

    /**
     * Method used to reset the state of every widget instances.
     */
    @Modifying
    @Query("UPDATE ProjectWidget SET lastSuccessDate = null, log = null, state = 'STOPPED'")
    void resetProjectWidgetsState();

    /**
     * Update the position in the grid of a widget.
     *
     * @param row    The new start row number
     * @param col    The new Start col number
     * @param width  The new number of columns taken by the widget
     * @param height The new number of rows taken by the widget
     * @param id     The project widget id
     */
    @Modifying
    @Query("UPDATE ProjectWidget SET gridRow = :row, gridColumn = :col, width = :width, "
        + "height = :height WHERE id = :id")
    void updateRowAndColAndWidthAndHeightById(@Param("row") int row, @Param("col") int col, @Param("width") int width,
                                              @Param("height") int height, @Param("id") Long id);

    /**
     * Update the state of a widget instance when Js execution ends successfully.
     *
     * @param executionDate The last execution date
     * @param log           The log of Js execution
     * @param data          The data returned by Js
     * @param id            The id of the project widget
     * @param widgetState   The widget state
     */
    @Modifying
    @Query("UPDATE ProjectWidget " + "SET lastExecutionDate = :lastExecutionDate, "
        + "lastSuccessDate = :lastExecutionDate, state = :state, log = :log, "
        + "data = :data WHERE id = :id")
    void updateSuccessExecution(@Param("lastExecutionDate") Date executionDate,
                                @Param("log") String log,
                                @Param("data") String data,
                                @Param("id") Long id,
                                @Param("state") WidgetStateEnum widgetState);

    /**
     * Update the state of a project widget when Js execution ends with errors.
     *
     * @param date        The last execution date
     * @param log         The logs of the execution
     * @param id          The project widget id
     * @param widgetState The widget state
     */
    @Modifying
    @Query("UPDATE ProjectWidget SET lastExecutionDate = :lastExecutionDate, "
        + "state = :state, log = :log WHERE id = :id")
    void updateLastExecutionDateAndStateAndLog(@Param("lastExecutionDate") Date date, @Param("log") String log,
                                               @Param("id") Long id, @Param("state") WidgetStateEnum widgetState);
}
