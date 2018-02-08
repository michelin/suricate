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

package io.suricate.monitoring.controllers.backoffice.admin.user;

import io.suricate.monitoring.config.security.token.TokenService;
import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.model.Project;
import io.suricate.monitoring.model.user.Role;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.repository.RoleRepository;
import io.suricate.monitoring.repository.UserRepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ViewScoped
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Named(value = "UserEditManagedBean")
public class UserEditManagedBean extends AbstractManagedBean {

	/**
	 * Selected user
	 */
	private User user;

	private List<Role> listRole;
	private List<Project> listProject;

	@Autowired
	private transient UserRepository userRepository;

	/**
	 * Role repository
	 */
	@Autowired
	private transient RoleRepository roleRepository;
	/**
	 * Project repository
	 */
	@Autowired
	private transient ProjectRepository projectRepository;

	/**
	 * Token service
	 */
	@Autowired
	private transient TokenService tokenService;

	/**
	 * Extract parameters
	 */
	@PostConstruct
	public void init() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		if (params.containsKey("id")) {
			String id = params.get("id");
			if (!NumberUtils.isNumber(id)) {
				redirectToIndex();
				return;
			}
			user = userRepository.findOne(Long.valueOf(id));
			if (user == null) {
				redirectToIndex();
				return;
			}
			listRole = roleRepository.findByUsers_Id(user.getId());
			listProject = projectRepository.findByUsers_Id(user.getId());
		} else {
			if (facesContext.getViewRoot().getViewId().endsWith("show.xhtml")) {
				redirectToIndex();
				return;
			}
			user = new User();
			listRole = new ArrayList<>();
			listProject = new ArrayList<>();
		}
	}

	public boolean isSelectedRole(Role role) {
		return listRole.contains(role);
	}

	public List<Role> getRoles() {
		return roleRepository.findAll();
	}

	public void toogleRole(Role role) {
		if (listRole.contains(role)) {
			listRole.remove(role);
		} else {
			listRole.add(role);
		}
	}

	public List<Role> getListRole() {
		return listRole;
	}

	public boolean isSelectedProject(Project project) {
		return listProject.contains(project);
	}

	public List<Project> getProjects() {
		return projectRepository.findAll();
	}

	public void toogleProject(Project project) {
		if (listProject.contains(project)) {
			listProject.remove(project);
		} else {
			listProject.add(project);
		}
	}

	public List<Project> getListProject() {
		return listProject;
	}

	public boolean isEditing() {
		return user != null && user.isAlreadyPersisted();
	}

	public User getuser() {
		return user;
	}

	/**
	 * Delete selected entity
	 * 
	 * @return redirect the user to the index page
	 */
	public String delete() {
		if (user != null) {
			userRepository.delete(user);
			addMessage(FacesMessage.SEVERITY_INFO, "app.admin.user.deleted", user.getExplicitName());
		}
		return "index.xhtml?faces-redirect=true";
	}

	/**
	 * Edit selected entity
	 * 
	 * @return redirect the user to the edit page
	 */
	public String edit() {
		return "edit.xhtml?id=" + user.getId() + "&amp;faces-redirect=true";
	}

	/**
	 * Save the entity
	 * 
	 * @return redirect the user to the index page
	 */
	public String save() {
		if (user != null) {
			boolean alreadyPersisted = user.isAlreadyPersisted();
			if (!alreadyPersisted && userRepository.findByUsername(user.getUsername()) != null) {
				addMessage(FacesMessage.SEVERITY_ERROR, "app.admin.user.field.username.exist", user.getUsername());
				return null;
			}
			user.setToken(tokenService.generateToken());
			user.setRoles(listRole);
			if (alreadyPersisted) {
				user.setProjects(listProject);
			}
			userRepository.save(user);
			if (alreadyPersisted) {
				addMessage(FacesMessage.SEVERITY_INFO, "app.admin.user.updated", user.getExplicitName());
			} else {
				addMessage(FacesMessage.SEVERITY_INFO,"app.admin.user.created", user.getExplicitName());
			}
		}
		return "index.xhtml?faces-redirect=true";
	}

}
