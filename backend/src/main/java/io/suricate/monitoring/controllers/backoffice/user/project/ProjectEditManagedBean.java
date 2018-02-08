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

package io.suricate.monitoring.controllers.backoffice.user.project;


import io.suricate.monitoring.config.security.token.TokenService;
import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.model.Project;
import io.suricate.monitoring.model.dto.UpdateEvent;
import io.suricate.monitoring.model.dto.update.UpdateType;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.service.SocketService;
import io.suricate.monitoring.utils.SecurityUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;

@ViewScoped
@Named(value = "ProjectEditManagedBean")
public class ProjectEditManagedBean extends AbstractManagedBean {

    /**
     * Default max column
     */
    private static final int DEFAULT_MAX_COLUMN = 5;

    /**
     * Default widget height
     */
    private static final int DEFAULT_WIDGET_HEIGHT = 360;

    /**
     * Selected project
     */
    private Project project;

    @Autowired
    private transient TokenService tokenService;

    @Autowired
    private transient ProjectRepository projectRepository;

    @Autowired
    private transient UserRepository userRepository;

    @Autowired
    private transient SocketService socketService;

    @Value("${server.context-path:}")
    private String contextPath;

    /**
     * Extract parameters
     */
    @PostConstruct
    public void init(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        if (params.containsKey("id")) {
            String id = params.get("id");
            if (!NumberUtils.isNumber(id)) {
                redirectToIndex();
                return;
            }
            if (SecurityUtils.isAdmin()){
                project = projectRepository.findOne(Long.valueOf(id));
            } else {
                project = projectRepository.findByIdAndUsers_Id(Long.valueOf(id), SecurityUtils.getConnectedUser().getId());
            }
            if (project == null){
                redirectToIndex();
                return;
            }
        } else {
            if (facesContext.getViewRoot().getViewId().endsWith("show.xhtml")){
                redirectToIndex();
                return;
            }
            project = new Project();
            project.setMaxColumn(DEFAULT_MAX_COLUMN);
            project.setWidgetHeight(DEFAULT_WIDGET_HEIGHT);
        }
    }



    public boolean isEditing() {
        return project != null && project.isAlreadyPersisted();
    }

    public Project getproject() {
        return project;
    }

    /**
     * Delete selected entity
     * @return redirect the project to the index page
     */
    public String delete() {
        if (project != null) {
            projectRepository.delete(project);
            addMessage(FacesMessage.SEVERITY_INFO,"app.admin.project.deleted",project.getExplicitName());
            // notify clients
            socketService.updateProjectScreen(project.getToken(), new UpdateEvent(UpdateType.DISCONNECT));
        }
        return "index.xhtml?faces-redirect=true";
    }

    /**
     * Edit selected entity
     * @return redirect the project to the edit page
     */
    public String edit(){
        return "edit.xhtml?id="+project.getId()+"&amp;faces-redirect=true";
    }

    /**
     * Save the entity
     * @return redirect the project to the index page
     */
    public String save(){
        if (project != null){
            boolean alreadyPersisted = project.isAlreadyPersisted();
            if (!alreadyPersisted) {
                project.setToken(tokenService.generateToken());
                User user = new User();
                user.setId(SecurityUtils.getConnectedUser().getId());
                project.setUsers(Collections.singletonList(user));
            }
            // Save the project
            projectRepository.save(project);
            // Add message
            if (alreadyPersisted) {
                // notify clients
                socketService.updateProjectScreen(project.getToken(), new UpdateEvent(UpdateType.GRID));
                addMessage(FacesMessage.SEVERITY_INFO,"app.admin.project.updated",project.getExplicitName());
            } else {
                addMessage(FacesMessage.SEVERITY_INFO,"app.admin.project.created",project.getExplicitName());
            }
            return "/content/index.xhtml?faces-redirect=true&project="+project.getId();
        }
        return "/content/index.xhtml";
    }
}
