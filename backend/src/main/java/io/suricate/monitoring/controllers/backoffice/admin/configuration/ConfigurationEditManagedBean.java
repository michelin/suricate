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
import io.suricate.monitoring.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.Map;

@ViewScoped
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Named(value = "ConfigurationEditManagedBean")
public class ConfigurationEditManagedBean extends AbstractManagedBean {

    /**
     * Selected configuration
     */
    private Configuration configuration;

    @Autowired
    private transient CacheService cacheService;

    @Autowired
    private transient ConfigurationRepository configurationRepository;

    /**
     * Extract parameters
     */
    @PostConstruct
    public void init(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        if (params.containsKey("id")) {
            String id = params.get("id");
            configuration = configurationRepository.findOne(String.valueOf(id));
            if (configuration == null){
                redirectToIndex();
                return;
            }
        } else {
            if (facesContext.getViewRoot().getViewId().endsWith("show.xhtml")){
                redirectToIndex();
                return;
            }
            configuration = new Configuration();
        }
    }






    public boolean isEditing() {
        return configuration != null && configuration.isAlreadyPersisted();
    }

    /**
     * Method used to get the configuration
     * @return the current configuration
     */
    public Configuration getconfiguration() {
        return configuration;
    }

    /**
     * Delete selected entity
     * @return redirect the configuration to the index page
     */
    public String delete() {
        if (configuration != null) {
            configurationRepository.delete(configuration);
            cacheService.clearCache("configuration");
            addMessage(FacesMessage.SEVERITY_INFO,"app.admin.configuration.deleted",configuration.getExplicitName());
        }
        return "index.xhtml?faces-redirect=true";
    }

    /**
     * Edit selected entity
     * @return redirect the configuration to the edit page
     */
    public String edit(){
        return "edit.xhtml?id="+configuration.getId()+"&amp;faces-redirect=true";
    }

    /**
     * Save the entity
     * @return redirect the configuration to the index page
     */
    public String save(){
        if (configuration != null){
            boolean alreadyPersisted = configuration.isAlreadyPersisted();
             // Check duplicate key
            if (!alreadyPersisted && configurationRepository.exists(configuration.getId())){
                addMessage(FacesMessage.SEVERITY_ERROR,"app.admin.configuration.field.key.exist",configuration.getKey());
                return null;
            }

            // Save the configuration
            configurationRepository.save(configuration);
            // Add message
            if (alreadyPersisted) {
                addMessage(FacesMessage.SEVERITY_INFO,"app.admin.configuration.updated",configuration.getExplicitName());
            } else {
                addMessage(FacesMessage.SEVERITY_INFO,"app.admin.configuration.created",configuration.getExplicitName());
            }
            cacheService.clearCache("configuration");
        }
        return "index.xhtml?faces-redirect=true";
    }
}
