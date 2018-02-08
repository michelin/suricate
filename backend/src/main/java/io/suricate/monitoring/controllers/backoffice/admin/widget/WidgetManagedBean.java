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

package io.suricate.monitoring.controllers.backoffice.admin.widget;


import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.model.Widget;
import io.suricate.monitoring.repository.WidgetRepository;
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
@Named(value = "WidgetManagedBean")
public class WidgetManagedBean extends AbstractManagedBean {

    /**
     * Lazy load
     */
    private LazyLoad<Widget, Long> lazyModel;

    /**
     * Selected widget
     */
    private Widget selected;

    @Autowired
    private transient WidgetRepository widgetRepository;

    @PostConstruct
    public void init() {
        lazyModel = new LazyLoad<Widget, Long>(widgetRepository);
    }

    public LazyLoad<Widget, Long> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyLoad<Widget, Long> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public Widget getSelected() {
        return selected;
    }

    public void setSelected(Widget selected) {
        this.selected = selected;
    }

    public void onRowSelect() throws IOException {
        if (selected != null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("show.xhtml?id=" + selected.getId());
        }
    }
}
