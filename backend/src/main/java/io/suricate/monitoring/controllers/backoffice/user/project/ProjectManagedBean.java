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


import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.model.Project;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.utils.SecurityUtils;
import io.suricate.monitoring.utils.jsf.LazyLoad;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JSF managed bean used to manage Project list screen
 */
@ViewScoped
@Named(value = "ProjectManagedBean")
public class ProjectManagedBean extends AbstractManagedBean {

    @Autowired
    private transient UserRepository userRepository;

    @Autowired
    private transient ProjectRepository projectRepository;

    /**
     * Lazy load
     */
    private LazyLoad<Project, Long> lazyModel;

    /**
     * Selected Project
     */
    private Project selected;

    @PostConstruct
    public void init() {
        Map<String, Object> filter = new HashMap<>();
        filter.put("users.id", SecurityUtils.getConnectedUser().getId());
        lazyModel = new LazyLoad<>(projectRepository, filter);
    }

    public LazyLoad<Project, Long> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyLoad<Project, Long> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public Project getSelected() {
        return selected;
    }

    public void setSelected(Project selected) {
        this.selected = selected;
    }

    public void onRowSelect() throws IOException {
        if (selected != null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("show.xhtml?id=" + selected.getId());
        }
    }
}
