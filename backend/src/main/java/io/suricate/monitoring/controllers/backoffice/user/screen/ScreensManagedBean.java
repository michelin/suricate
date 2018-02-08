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

package io.suricate.monitoring.controllers.backoffice.user.screen;

import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.controllers.backoffice.user.HomeUserManagedBean;
import io.suricate.monitoring.model.dto.Client;
import io.suricate.monitoring.service.SocketService;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.Collection;

/**
 * JSF Managed bean used to manage screen tab on home user page
 *
 */
@ViewScoped
@Named(value = "ScreensManagedBean")
public class ScreensManagedBean extends AbstractManagedBean {

	@Autowired
	private transient SocketService socketService;

	@Autowired
	private transient HomeUserManagedBean homeUserManagedBean;

	private String screenCode;

	@Value("${server.context-path:}")
	private String contextPath;

	/**
	 * Method used to register a new screen with his code
	 */
	@LogExecutionTime
	public void registerScreen() {
		if (StringUtils.isNotEmpty(screenCode)) {
			socketService.notifyRegister(screenCode,contextPath+"/content/tv/index.xhtml?token=" + homeUserManagedBean.getSelectedProject().getToken());
			addMessage(FacesMessage.SEVERITY_INFO, "app.user.register.tv.info", homeUserManagedBean.getSelectedProject().getExplicitName());
			screenCode = null;
		} else {
			addMessage(FacesMessage.SEVERITY_ERROR, "app.user.field.code.required");
		}
	}

	/**
	 * Show on all clients connected to this dashboard the screen number
	 */
	@LogExecutionTime
	public void displayNumber(){
		socketService.displayUniqueNumber(homeUserManagedBean.getSelectedProject().getToken());
	}

	/**
	 * Detach a client from his current dashboard
	 * @param client
	 */
	@LogExecutionTime
	public void disconnectScreen(Client client){
		socketService.disconnectClient(client);
		addMessage(FacesMessage.SEVERITY_INFO, "app.user.client.disconnected");
	}

	/**
	 * Method used to get the number of client on this screen
	 * @return the number of client connected to the current dashboard
	 */
	public int getClientNumber(){
		return socketService.getClientNumber(homeUserManagedBean.getSelectedProject().getToken());
	}

	public String getScreenCode() {
		return screenCode;
	}

	public void setScreenCode(String screenCode) {
		this.screenCode = screenCode;
	}

	public Collection<Client> getClients() {
		return socketService.getClient(homeUserManagedBean.getSelectedProject().getToken());
	}

}
