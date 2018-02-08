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

package io.suricate.monitoring.controllers.backoffice.user;

import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.model.Project;
import io.suricate.monitoring.model.dto.UpdateEvent;
import io.suricate.monitoring.model.dto.update.UpdateType;
import io.suricate.monitoring.model.dto.widget.WidgetResponse;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.service.SocketService;
import io.suricate.monitoring.service.WidgetService;
import io.suricate.monitoring.utils.SecurityUtils;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.List;

/**
 * JSF Managed bean used to manage homepage
 *
 */
@ViewScoped
@Named(value = "HomeUserManagedBean")
public class HomeUserManagedBean extends AbstractManagedBean {

	@Autowired
	private transient ProjectRepository projectRepository;

	@Autowired
	private transient SocketService socketService;

	@Autowired
	private transient WidgetService widgetService;

	private Project selectedProject;

	private Long selectedId;

	private List<Project> projects;

	private List<WidgetResponse> widgetResponses;

	@PostConstruct
	public void init(){
		if (SecurityUtils.isAdmin()){
			projects = projectRepository.findAll();
		} else {
			projects = projectRepository.findByUsers_Id(SecurityUtils.getConnectedUser().getId());
		}
		if (projects == null) {
			return;
		}
		// Auto select project with his id
		String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("project");
		if (StringUtils.isNotBlank(id)) {
			for (Project project : projects) {
				if (id.equals(project.getId().toString())) {
					selectedProject = project;
					selectedId = selectedProject.getId();
					updateWidgetResponse();
				}
			}
		} else if (projects.size() == 1) { // Select the first if only on project is defined
			selectedProject = projects.get(0);
			selectedId = selectedProject.getId();
			updateWidgetResponse();
		}
	}

	/**
	 * Force screen reload
	 */
	@LogExecutionTime
	public void forceRefresh() {
		socketService.updateProjectScreen(selectedProject.getToken(), new UpdateEvent(UpdateType.GRID));
		updateWidgetResponse();
	}

	/**
	 * Method used to display selected project
	 */
	@LogExecutionTime
	public void updateSelectedProject() {
		for (Project project: projects){
			if (project.getId().equals(selectedId)){
				selectedProject = project;
				updateWidgetResponse();
				return;
			}
		}
		selectedProject = null;
		updateWidgetResponse();
	}

	@LogExecutionTime
	public void updateWidgetResponse(){
		if (selectedProject == null) {
			widgetResponses = null;
		} else {
			if (StringUtils.isEmpty(selectedProject.getCssStyle())){
				selectedProject.setCssStyle("body.grid{\n\n}");
			}
			widgetResponses = widgetService.getWidgets(selectedProject.getId());
		}
	}

	/**
	 * Method used to relaunch all widget in error
	 */
	@LogExecutionTime
	public void relaunchWidgets(){
		if (widgetResponses != null){
			widgetResponses.stream()
					.filter(WidgetResponse::isError)
					.forEach(widgetResponse -> widgetService.scheduleWidget(widgetResponse.getProjectWidgetId()));
			updateWidgetResponse();
		}
	}

	public Long getSelectedId() {
		return selectedId;
	}

	public void setSelectedId(Long selectedId) {
		this.selectedId = selectedId;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public Project getSelectedProject() {
		return selectedProject;
	}

	public void setSelectedProject(Project selectedProject) {
		this.selectedProject = selectedProject;
	}

	public List<WidgetResponse> getWidgetResponses() {
		return widgetResponses;
	}

	/**
	 * Method used to get the number of error widget
	 * @return the number of widget in error
	 */
	public long getErrorWidget() {
		if (widgetResponses != null) {
			return widgetResponses.stream().filter(WidgetResponse::isError).count();
		}
		return 0;
	}

}
