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

package io.suricate.monitoring.controllers.backoffice.admin.configuration;


import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.model.Configuration;
import io.suricate.monitoring.repository.ConfigurationRepository;
import io.suricate.monitoring.service.GitService;
import io.suricate.monitoring.utils.jsf.LazyLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.IOException;

@ViewScoped
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Named(value = "ConfigurationManagedBean")
public class ConfigurationManagedBean extends AbstractManagedBean {

    /**
     * Lazy load
     */
    private LazyLoad<Configuration, Long> lazyModel;

    /**
     * Selected Configuration
     */
    private Configuration selected;


    @Autowired
    private transient GitService gitService;

    @Autowired
    private transient ConfigurationRepository configurationRepository;

    @PostConstruct
    public void init() { // NOSONAR
        lazyModel = new LazyLoad<Configuration, Long>(configurationRepository);
    }

    public LazyLoad<Configuration, Long> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyLoad<Configuration, Long> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public Configuration getSelected() {
        return selected;
    }

    public void setSelected(Configuration selected) {
        this.selected = selected;
    }

    /**
     * On row selected in datatable
     * @throws IOException exception
     */
    public void onRowSelect() throws IOException {
        if (selected != null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("show.xhtml?id=" + selected.getId());
        }
    }

    /**
     * Force reload widgets from git repository
     */
    public void reload() {
        gitService.updateWidgetFromGit();
        addMessage(FacesMessage.SEVERITY_INFO, "app.admin.widget.reloading");
    }
}
