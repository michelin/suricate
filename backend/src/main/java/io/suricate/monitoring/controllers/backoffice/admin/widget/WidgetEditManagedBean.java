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
import io.suricate.monitoring.model.Category;
import io.suricate.monitoring.model.Widget;
import io.suricate.monitoring.repository.CategoryRepository;
import io.suricate.monitoring.repository.WidgetRepository;
import io.suricate.monitoring.service.CacheService;
import io.suricate.monitoring.utils.EntityUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

@ViewScoped
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Named(value = "WidgetEditManagedBean")
public class WidgetEditManagedBean extends AbstractManagedBean {

	/**
	 * Class logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(WidgetEditManagedBean.class);

	/**
	 * Selected widget
	 */
	private Widget widget;

	@Autowired
	private transient WidgetRepository widgetRepository;

	@Autowired
	private transient CacheService cacheService;

	/**
	 * Category repository
	 */
	@Autowired
	private transient CategoryRepository categoryRepository;

	/**
	 * The selected entity Id
	 */
	private Long selectedCategory;

	public boolean isEditing() {
		return widget != null && widget.isAlreadyPersisted();
	}

	public Widget getwidget() {
		return widget;
	}

	/**
	 * Delete selected entity
	 * 
	 * @return redirect the widget to the index page
	 */
	public String delete() {
		if (widget != null) {
			widgetRepository.delete(widget);
			addMessage(FacesMessage.SEVERITY_INFO, "app.admin.widget.deleted",
					widget.getExplicitName());
		}
		return "index.xhtml?faces-redirect=true";
	}

	/**
	 * Edit selected entity
	 * 
	 * @return redirect the widget to the edit page
	 */
	public String edit() {
		return "edit.xhtml?id=" + widget.getId() + "&amp;faces-redirect=true";
	}

	public void setSelectedCategory(Long category) {
		selectedCategory = category;
	}

	public Long getSelectedCategory() {
		return selectedCategory;
	}

	/**
	 * Method used to upload image
	 * 
	 * @param event
	 *            file uploaded
	 */
	public void uploadImage(FileUploadEvent event) {
		//widget.setImage(event.getFile().getContents());
		LOGGER.debug("Image submitted :{}", event.getFile().getFileName());
	}

	public StreamedContent getImage() {
		return null;
	}

	public List<Category> getListCategory() {
		return categoryRepository.findAll();
	}

	/**
	 * Extract parameters
	 */
	@PostConstruct
	public void init() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if (params.containsKey("id")) {
			String id = params.get("id");
			if (!NumberUtils.isNumber(id)) {
				redirectToIndex();
				return;
			}
			widget = widgetRepository.findOne(Long.valueOf(id));
			if (widget == null) {
				redirectToIndex();
				return;
			}
			selectedCategory = EntityUtils.getProxiedId(widget.getCategory());
		} else {
			redirectToIndex();
			return;
		}
	}

	/**
	 * Save the entity
	 * 
	 * @return redirect the widget to the index page
	 */
	public String save() {
		if (widget != null) {
			boolean alreadyPersisted = widget.isAlreadyPersisted();
			if (!alreadyPersisted
					&& widgetRepository.findByTechnicalName(widget
							.getTechnicalName()) != null) {
				addMessage(FacesMessage.SEVERITY_ERROR,
						"app.admin.widget.field.technicalname.exist",
						widget.getTechnicalName());
				return null;
			}
			if (selectedCategory == null) {
				widget.setCategory(null);
			} else {
				Category category = new Category();
				category.setId(selectedCategory);
				widget.setCategory(category);
			}
			widgetRepository.save(widget);
			cacheService.clearAllCache();
			if (alreadyPersisted) {
				addMessage(FacesMessage.SEVERITY_INFO,
						"app.admin.widget.updated", widget.getExplicitName());
			} else {
				addMessage(FacesMessage.SEVERITY_INFO,
						"app.admin.widget.created", widget.getExplicitName());
			}
		}
		return "index.xhtml?faces-redirect=true";
	}
}
