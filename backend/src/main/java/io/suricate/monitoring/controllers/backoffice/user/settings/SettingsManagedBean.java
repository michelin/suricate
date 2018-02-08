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

package io.suricate.monitoring.controllers.backoffice.user.settings;

import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.controllers.backoffice.user.HomeUserManagedBean;
import io.suricate.monitoring.model.dto.user.UserDto;
import io.suricate.monitoring.service.ProjectService;
import io.suricate.monitoring.service.UserService;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.omnifaces.util.Faces;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * JSF Managed bean used to manage settings tab on home user page
 *
 */
@ViewScoped
@Named(value = "SettingsManagedBean")
public class SettingsManagedBean extends AbstractManagedBean {

	@Autowired
	private transient HomeUserManagedBean homeUserManagedBean;

	@Autowired
	private transient UserService userService;

	@Autowired
	private transient ProjectService projectService;

	/**
	 * Search user
	 */
	private UserDto searchUser;

	/**
	 * The new project name
	 */
	private String projectName;

	/**
	 * Project ID
	 */
	private Long projectId;

	/**
	 * Method used to get user dto
	 * @return the list of users
	 */
	@LogExecutionTime
	public List<UserDto> getUser(){
		return userService.getUserForProject(homeUserManagedBean.getSelectedProject().getId());
	}

    /**
     * Method sued to update all project change
     */
    @Transactional
	public void updateProject(){
		updateProject(false);
    }

	/**
	 * Method used to update current project
	 * @param reloadPage boolean to force the reload of the current dashboard
	 */
	private void updateProject(boolean reloadPage){
		homeUserManagedBean.getSelectedProject().setCssStyle(
				Faces.getRequestParameterMap().entrySet()
						.stream()
						.filter(e -> e.getKey().contains("projectCss"))
						.map(Map.Entry::getValue)
						.findFirst()
						.orElse(null));
		if (homeUserManagedBean.getSelectedProject() != null) {
			projectService.updateProject(homeUserManagedBean.getSelectedProject(), projectName);
			addMessage(FacesMessage.SEVERITY_INFO,"app.user.settings.dashboard.updated", homeUserManagedBean.getSelectedProject().getExplicitName());
		}
		if (reloadPage){
			RequestContext.getCurrentInstance().update("dashboard-form");
		}
	}

	/**
	 * Method sued to update all project change
	 */
	@Transactional
	public void updateProjectName(){
		updateProject(true);
	}

	/**
	 * Remove user for this dashboard
	 * @param id user Id
	 */
	@Transactional
	public void removeUser(Long id){
		userService.removeUser(homeUserManagedBean.getSelectedProject().getId(), id);
		addMessageForClient(FacesMessage.SEVERITY_INFO,"user-settings-message", "app.user.settings.dashboard.user.removed");
	}

	/**
	 * Add user to this dashboard
	 */
	@Transactional
	public void addUser() {
		if (searchUser != null) {
			userService.addUserToProject(searchUser.getUsername(), homeUserManagedBean.getSelectedProject().getId());
			addMessageForClient(FacesMessage.SEVERITY_INFO, "user-settings-message", "app.user.settings.dashboard.user.new.added");
			searchUser = null;
		}
	}

	/**
	 * Delete selected dashboard
	 * @return redirect the index page
	 */
	public String delete() {
		if (homeUserManagedBean.getSelectedProject() != null) {
			projectService.deleteProject(homeUserManagedBean.getSelectedProject().getId());
			addMessage(FacesMessage.SEVERITY_INFO,"app.user.settings.dashboard.deleted", homeUserManagedBean.getSelectedProject().getExplicitName());
		}
		return "index.xhtml?faces-redirect=true";
	}

	/**
	 * Method used to search user with his username (ex: f373599)
	 * @param query the query to find user
	 * @return the list of user
	 */
	public List<UserDto> completeUser(String query){
		return userService.searchUser(query);
	}


	/**
	 * Getter for HomeUser managed bean
	 * @return the autowired managed bean
	 */
    public HomeUserManagedBean getHomeUserManagedBean() {
        return homeUserManagedBean;
    }

	/**
	 * Getter for project name
	 * @return the project name
	 */
	public String getProjectName() {
		if (projectId == null || !projectId.equals(homeUserManagedBean.getSelectedProject().getId())){
			projectId = homeUserManagedBean.getSelectedProject().getId();
			projectName = homeUserManagedBean.getSelectedProject().getName();
		}
		return projectName;
	}

	/**
	 * Setter for project name
	 * @param projectName the new name to define
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public UserDto getSearchUser() {
		return searchUser;
	}

	public void setSearchUser(UserDto searchUser) {
		this.searchUser = searchUser;
	}
}
