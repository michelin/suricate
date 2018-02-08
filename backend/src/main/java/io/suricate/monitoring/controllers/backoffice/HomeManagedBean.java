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

import io.suricate.monitoring.repository.ConfigurationRepository;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.repository.WidgetRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Generated;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named(value = "HomeManagedBean")
public class HomeManagedBean extends AbstractManagedBean {

	@Autowired
	private transient UserRepository userRepository;

	@Autowired
	private transient ConfigurationRepository configurationRepository;

	@Autowired
	private transient WidgetRepository widgetRepository;

	@Autowired
	private transient ProjectRepository projectRepository;

	/**
	 * Method used to return the number of users
	 * 
	 * @return
	 */
	public long getNumberUser() {
		return userRepository.count();
	}

	/**
	 * Method used to return the number of configurations
	 * 
	 * @return the number of entity
	 */
	public long getNumberConfiguration() {
		return configurationRepository.count();
	}

	/**
	 * Method used to return the number of widgets
	 * 
	 * @return the number of entity
	 */
	public long getNumberWidget() {
		return widgetRepository.count();
	}

	/**
	 * Method used to return the number of projects
	 * 
	 * @return the number of entity
	 */
	public long getNumberProject() {
		return projectRepository.count();
	}

}
