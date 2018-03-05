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

package io.suricate.monitoring.service;

import io.suricate.monitoring.model.dto.project.ProjectDto;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.suricate.monitoring.model.dto.UpdateEvent;
import io.suricate.monitoring.model.dto.project.ProjectResponse;
import io.suricate.monitoring.model.dto.project.ProjectWidgetRequest;
import io.suricate.monitoring.model.dto.update.UpdateType;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.repository.ProjectWidgetRepository;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.repository.WidgetRepository;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Service used to manage projects
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectWidgetRepository projectWidgetRepository;

    @Autowired
    private WidgetRepository widgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocketService socketService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private transient LibraryService libraryService;

    /**
     * Transforme a model object into a DTO object
     *
     * @param project The project model object
     * @return The associated DTO object
     */
    public ProjectResponse toDTO(Project project) {
        ProjectResponse projectResponse = new ProjectResponse();

        projectResponse.setId(project.getId());
        projectResponse.setName(project.getName());
        projectResponse.setToken(project.getToken());
        projectResponse.setWidgetHeight(project.getWidgetHeight());
        projectResponse.setMaxColumn(project.getMaxColumn());
        projectResponse.setCssStyle(project.getCssStyle());

        List<ProjectWidget> projectWidgets = projectWidgetRepository.findByProjectIdAndWidget_WidgetAvailabilityOrderById(project.getId(), WidgetAvailabilityEnum.ACTIVATED);
        for (ProjectWidget projectWidget: projectWidgets){
            projectResponse.getWidgets().add(widgetService.getWidgetResponse(projectWidget));
        }

        projectResponse.getLibrariesToken().addAll(libraryService.getLibraries(projectResponse.getWidgets()));

        return projectResponse;
    }

    /**
     * Tranforme a model object into a DTO object
     *
     * @param projectDto The DTO project object
     * @return The associated model object
     */
    public Project toModel(ProjectDto projectDto) {
        Project project = new Project();

        project.setId(projectDto.getId());
        project.setName(projectDto.getName());
        project.setMaxColumn(projectDto.getMaxColumn());
        project.setWidgetHeight(projectDto.getWidgetHeight());
        project.setCssStyle(projectDto.getCssStyle());
        project.setToken("");

        return project;
    }

    /**
     * Retrieve all the project for a user
     *
     * @param user The user
     * @return The project list associated to the user
     */
    public List<ProjectResponse> getAllByUser(User user) {
        List<ProjectResponse> projectsResponse = new ArrayList<>();

        List<Project> projects = projectRepository.findByUsers_Id(user.getId());
        for(Project project : projects) {
            projectsResponse.add(toDTO(project));
        }

        return projectsResponse;
    }

    /**
     * Get a project by the project id
     *
     * @param id The id of the project
     * @return The project associated
     */
    @LogExecutionTime
    public ProjectResponse getOneById(Long id){
        return toDTO(projectRepository.findOne(id));
    }

    /**
     * Create a new project for a user
     *
     * @param user The user how create the project
     * @param project The project to instantiate
     * @return The project instantiate
     */
    @Transactional
    public Project addProject(User user, Project project) {
        project.getUsers().add(user);
        return projectRepository.save(project);
    }

    @Transactional
    public ProjectWidget addWidgetToProject(ProjectWidgetRequest projectWidgetRequest) {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setCol(0);
        projectWidget.setRow(0);
        projectWidget.setWidth(1);
        projectWidget.setHeight(1);
        projectWidget.setData("{}");
        projectWidget.setBackendConfig(projectWidgetRequest.getBackendConfig());
        projectWidget.setWidget(widgetRepository.findOne(projectWidgetRequest.getWidgetId()));
        projectWidget.setProject(projectRepository.findOne(projectWidgetRequest.getProjectId()));

        // Add project widget
        projectWidget = projectWidgetRepository.saveAndFlush(projectWidget);
        widgetService.scheduleWidget(projectWidget.getId());

        // Update grid
        socketService.updateProjectScreen(projectWidget.getProject().getToken(),  new UpdateEvent(UpdateType.GRID));

        return projectWidget;
    }

    /**
     * Method used to update a project
     * @param project the project to update
     * @param newName the new name
     */
    @Transactional
    public void updateProject(Project project, String newName) {
        project.setName(newName);
        projectRepository.save(project);
        // Update grid
        socketService.updateProjectScreen(project.getToken(), new UpdateEvent(UpdateType.GRID));
    }

    /**
     * Method used to delete a project with his ID
     * @param id the project ID
     */
    public void deleteProject(Long id){
        // notify clients
        socketService.updateProjectScreen(id, new UpdateEvent(UpdateType.DISCONNECT));
        // delete project
        projectRepository.delete(id);
    }

}
