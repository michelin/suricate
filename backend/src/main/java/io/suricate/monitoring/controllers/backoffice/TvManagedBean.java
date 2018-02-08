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

package io.suricate.monitoring.controllers.backoffice;

import io.suricate.monitoring.model.Project;
import io.suricate.monitoring.model.dto.widget.WidgetResponse;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.service.LibraryService;
import io.suricate.monitoring.service.WidgetService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.security.SecureRandom;
import java.util.List;

@ViewScoped
@Named(value = "TvManagedBean")
public class TvManagedBean extends AbstractManagedBean {

	/**
	 * Max bound number
	 */
	private static final int MAX_BOUND = 9000;

	/**
	 * Min bound number
	 */
	private static final int MIN_BOUND = 1000;

	/**
	 * Secure random
	 */
	private static final SecureRandom RANDOM = new SecureRandom();

	@Autowired
	private transient WidgetService widgetService;

	@Autowired
	private transient LibraryService libraryService;

	@Autowired
	private transient ProjectRepository projectRepository;

	private List<WidgetResponse> widgetResponses;
	private List<String> librariesToken;

	private Project project;

	private String number;

	@PostConstruct
	private void init(){ // NOSONAR
		String token = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("token");
		if (StringUtils.isNotBlank(token)) {
			project = projectRepository.findByToken(token);
			if (project != null) {
				widgetResponses = widgetService.getWidgets(project.getId());
				librariesToken = libraryService.getLibraries(widgetResponses);
			}
		}
		number = String.valueOf(RANDOM.nextInt(MAX_BOUND)+ MIN_BOUND);
	}

	public List<WidgetResponse> getWidgets(){
		return widgetResponses;
	}

	public Project getProject() {
		return project;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public List<String> getLibrariesToken() {
		return librariesToken;
	}

	public void setLibrariesToken(List<String> librariesToken) {
		this.librariesToken = librariesToken;
	}
}
