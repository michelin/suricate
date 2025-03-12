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
package com.michelin.suricate.service.api;

import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridRequestDto;
import com.michelin.suricate.model.dto.websocket.UpdateEvent;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.enumeration.UpdateType;
import com.michelin.suricate.repository.ProjectGridRepository;
import com.michelin.suricate.repository.ProjectRepository;
import com.michelin.suricate.service.js.scheduler.JsExecutionScheduler;
import com.michelin.suricate.service.websocket.DashboardWebSocketService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Project grid service. */
@Service
public class ProjectGridService {
    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private DashboardWebSocketService dashboardWebsocketService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectGridRepository projectGridRepository;

    /**
     * Get a project grid by the id.
     *
     * @param id The id of the project grid
     * @return The project grid associated
     */
    @Transactional(readOnly = true)
    public Optional<ProjectGrid> getOneById(Long id) {
        return projectGridRepository.findById(id);
    }

    /**
     * Find a grid by id and project token.
     *
     * @param id The ID
     * @param token The project token
     * @return The grid
     */
    @Transactional(readOnly = true)
    public Optional<ProjectGrid> findByIdAndProjectToken(Long id, String token) {
        return projectGridRepository.findByIdAndProjectToken(id, token);
    }

    /**
     * Persist a given project grid.
     *
     * @param projectGrid The grid
     */
    @Transactional
    public ProjectGrid create(ProjectGrid projectGrid) {
        return projectGridRepository.save(projectGrid);
    }

    /**
     * Persist a given list of project grids.
     *
     * @param project The project to update
     * @param projectGridRequestDto The new data as DTO
     */
    @Transactional
    public void updateAll(Project project, ProjectGridRequestDto projectGridRequestDto) {
        project.setDisplayProgressBar(projectGridRequestDto.isDisplayProgressBar());

        projectRepository.save(project);

        project.getGrids().forEach(projectGrid -> {
            Optional<ProjectGridRequestDto.GridRequestDto> gridRequestDtoOptional =
                    projectGridRequestDto.getGrids().stream()
                            .filter(dto -> dto.getId().equals(projectGrid.getId()))
                            .findFirst();

            gridRequestDtoOptional.ifPresent(gridRequestDto -> projectGrid.setTime(gridRequestDto.getTime()));
        });

        projectGridRepository.saveAll(project.getGrids());
    }

    /**
     * Delete a grid by project id and id.
     *
     * @param project The project
     * @param id The grid id
     */
    @Transactional
    public void deleteByProjectIdAndId(Project project, Long id) {
        Optional<ProjectGrid> projectGridOptional = getOneById(id);

        if (projectGridOptional.isPresent()) {
            ProjectGrid projectGrid = projectGridOptional.get();

            if (!projectGrid.getWidgets().isEmpty()) {
                ctx.getBean(JsExecutionScheduler.class).cancelWidgetsExecutionByGrid(projectGrid);
            }

            projectGridRepository.deleteByProjectIdAndId(project.getId(), id);

            UpdateEvent updateEvent =
                    UpdateEvent.builder().type(UpdateType.REFRESH_DASHBOARD).build();

            dashboardWebsocketService.sendEventToProjectSubscribers(project.getToken(), updateEvent);
        }
    }
}
