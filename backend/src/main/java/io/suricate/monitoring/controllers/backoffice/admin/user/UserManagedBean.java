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


import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.utils.jsf.LazyLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.IOException;

@ViewScoped
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Named(value = "UserManagedBean")
public class UserManagedBean extends AbstractManagedBean {

    /**
     * Lazy load
     */
    private LazyLoad<User, Long> lazyModel;

    /**
     * Selected User
     */
    private User selected;

    @Autowired
    private transient UserRepository userRepository;

    @PostConstruct
    public void init() {
        lazyModel = new LazyLoad<User, Long>(userRepository);
    }

    public LazyLoad<User, Long> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyLoad<User, Long> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public User getSelected() {
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    public void onRowSelect() throws IOException {
        if (selected != null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("show.xhtml?id=" + selected.getId());
        }
    }
}

