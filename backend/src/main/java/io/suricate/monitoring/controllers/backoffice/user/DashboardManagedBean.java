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

package io.suricate.monitoring.controllers.backoffice.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.model.*;
import io.suricate.monitoring.model.dto.UpdateEvent;
import io.suricate.monitoring.model.dto.update.UpdateType;
import io.suricate.monitoring.model.dto.widget.WidgetPosition;
import io.suricate.monitoring.model.dto.widget.WidgetVariableResponse;
import io.suricate.monitoring.model.WidgetVariableType;
import io.suricate.monitoring.service.SocketService;
import io.suricate.monitoring.service.WidgetService;
import io.suricate.monitoring.utils.JavascriptUtils;
import io.suricate.monitoring.utils.PropertiesUtils;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jasypt.encryption.StringEncryptor;
import org.omnifaces.util.Faces;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * JSF Managed bean used to manage homepage
 *
 */
@ViewScoped
@Named(value = "DashboardManagedBean")
public class DashboardManagedBean extends AbstractManagedBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(DashboardManagedBean.class.getName());

    /**
     * Key for custom css style
     */
    private static final String KEY_CUSTOM_CSS = "customCss";

    @Autowired
	public transient HomeUserManagedBean homeUserManagedBean;

	@Autowired
	private transient WidgetService widgetService;

	@Autowired
	@Qualifier("jasyptStringEncryptor")
	private transient StringEncryptor stringEncryptor;

	@Autowired
	private transient SocketService socketService;

	private Widget selectedWidget;

	private ProjectWidget selectedProjectWidget;

	private List<WidgetVariableResponse> widgetVariableResponses;

	private transient Map<String, Object> parameters = new TreeMap<>();

	/**
	 * List of category/widgets to display
	 */
	private transient List<Map.Entry<Category,List<Widget>>> categoryListMap;

	/**
	 * The user search to filter widget list
	 */
	private String searchWidget;

	@PostConstruct
	public void init(){
		categoryListMap = new ArrayList<>(getAvailableWidget().entrySet());
	}

	/**
	 * Method used to save widget configuration
	 */
	@LogExecutionTime
	public void saveWidgetConfiguration(){
		String customCss = null;
		StringBuilder builder = new StringBuilder();
		Map<String, String> map = Faces.getRequestParameterMap();
		for (WidgetVariableResponse widgetVariableResponse: widgetVariableResponses){

			String value = null;
			if (map.containsKey(widgetVariableResponse.getName())) {
				value = StringUtils.trim(toString(map.get(widgetVariableResponse.getName())));
			} else if (parameters.containsKey(widgetVariableResponse.getName())){
				value = StringUtils.trim(toString(parameters.get(widgetVariableResponse.getName())));
			}

			// Encrypt secret input
			if (WidgetVariableType.SECRET == widgetVariableResponse.getType()){
				value = stringEncryptor.encrypt(value);
			}

			// Create configutration
			builder.append(widgetVariableResponse.getName()).append('=');
			if (value != null) {
				builder.append(value);
			}
			builder.append('\n');
		}

		if (selectedProjectWidget == null) {
			addWidgetToProject(selectedWidget, homeUserManagedBean.getSelectedProject(), builder.toString());
		} else {
			if (parameters.containsKey(KEY_CUSTOM_CSS)){
				customCss = StringUtils.trimToNull((String) parameters.get(KEY_CUSTOM_CSS));
			}
			updateProjectWidget(selectedProjectWidget.getId(),homeUserManagedBean.getSelectedProject().getToken(), customCss, builder.toString());
			scheduleWidget();
		}
		homeUserManagedBean.updateWidgetResponse();
	}

	/**
	 * Method used to convert object to String
	 * @param object object to convert
	 * @return A string representing the object
	 */
	protected String toString(Object object){
		if (object == null){
			return null;
		}
		if (object instanceof String[]) {
			return String.join(",", (String[]) object);
		}
		return String.valueOf(object);
	}

	/**
	 * Add a widget to project
	 * @param widget the widget to add
	 * @param project the project to link
	 * @param backendConfig backend configuration
	 */
	@LogExecutionTime
	private void addWidgetToProject(Widget widget, Project project, String backendConfig){
		ProjectWidget projectWidget = new ProjectWidget();
		projectWidget.setCol(0);
		projectWidget.setRow(0);
		projectWidget.setWidth(1);
		projectWidget.setHeight(1);
		projectWidget.setData("{}");
		projectWidget.setBackendConfig(backendConfig);
		// Add widget
		projectWidget.setWidget(widget);
		// Add project
		projectWidget.setProject(project);

		// Add project widget
		widgetService.addprojectWidget(projectWidget);

		// Update grid
		socketService.updateProjectScreen(project.getToken(),  new UpdateEvent(UpdateType.GRID));
	}


	/**
	 * Method used to handle file upload and inline it
	 * @param event the upload event
	 */
	public void handleFileUpload(FileUploadEvent event) {
		String base64data = "data:"+event.getFile().getContentType()+";base64,"+ Base64.encodeBase64String(event.getFile().getContents());
		parameters.put(event.getComponent().getId(), base64data);
	}

	/**
	 * Method used to check if the selected file is an image
	 * @param key the key to check
	 * @return true if it's an image false otherwise
	 */
	public boolean isImage(String key) {
		Object value = parameters.get(key);
		return value instanceof String && StringUtils.startsWithIgnoreCase((String) value,"data:image");
	}

	/**
	 * Method used to check if the config exist
	 * @param key config name
	 * @return true if the config exist is uploaded or false otherwise
	 */
	public boolean isConfigExisting(String key){
		return parameters.containsKey(key);
	}

	/**
	 * Method used to get all available widgets by categories
	 * @return the list widget available
	 */
	private Map<Category,List<Widget>> getAvailableWidget(){
		return widgetService.getAvailableWidget(WidgetAvailability.ACTIVATED, searchWidget);
	}

	/**
	 * Method used to search widget and update widget list
	 */
	public void searchWidget(){
		categoryListMap.clear();
		categoryListMap.addAll(new ArrayList<>(getAvailableWidget().entrySet()));
	}

	/**
	 * Method used to edit a widget
	 * @param id the widget instance id to edit
	 */
	@LogExecutionTime
	public void editWidget(Long id){
		selectedWidget = widgetService.getwidgetByProjectWidgetId(id,homeUserManagedBean.getSelectedProject().getId());
		selectedProjectWidget = widgetService.getWidgetProject(id,homeUserManagedBean.getSelectedProject().getId());
		widgetVariableResponses = JavascriptUtils.extractVariables(selectedWidget.getBackendJs());
		parameters.clear();
		parameters.putAll(PropertiesUtils.getMap(selectedProjectWidget.getBackendConfig()));

		// Customize properties
		for (WidgetVariableResponse widgetVariableResponse : widgetVariableResponses){
			switch (widgetVariableResponse.getType()){
				case SECRET:
					try {
						parameters.put(widgetVariableResponse.getName(), stringEncryptor.decrypt((String)parameters.get(widgetVariableResponse.getName())));
					}catch (Exception e){
						LOGGER.error(ExceptionUtils.getMessage(e));
					}
					break;
				case MULTIPLE:
					parameters.put(widgetVariableResponse.getName(), ((String)parameters.get(widgetVariableResponse.getName())).split(","));
					break;
				default:
			}
		}

		// Add default style for widget
		String customStyle = selectedProjectWidget.getCustomStyle();
		if (StringUtils.isEmpty(customStyle)){
			customStyle = ".widget.widget-"+selectedProjectWidget.getId()+" {\n\n}";
		}
		parameters.put(KEY_CUSTOM_CSS, customStyle);
	}

	/**
	 * Method used to delete widget
	 * @param id the widget id
	 */
	@LogExecutionTime
	public void deleteWidget(Long id){
		widgetService.removeWidget(homeUserManagedBean.getSelectedProject().getId(), id);
		homeUserManagedBean.updateWidgetResponse();
	}

	/**
	 * Method used to update widget position on screen
	 */
	@LogExecutionTime
	public void updateScreen(){
		ObjectMapper objectMapper = new ObjectMapper();
		List<WidgetPosition> positions = null;
		String widgetPositions = Faces.getRequestParameter("widgetPositions");
		if (StringUtils.isNotEmpty(widgetPositions)) {
			try {
				positions = objectMapper.readValue(widgetPositions, new TypeReference<List<WidgetPosition>>() {
				});
				widgetService.update(homeUserManagedBean.getSelectedProject().getId(), positions, homeUserManagedBean.getSelectedProject().getToken());
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		homeUserManagedBean.updateWidgetResponse();
	}

	/**
	 * Method used to cancel the selected widget
	 */
	public void cancelWidget(){
		selectedWidget = null;
		selectedProjectWidget = null;
		widgetVariableResponses = null;
		// Update search
		searchWidget = null;
		searchWidget();
		// Clear parameters
		parameters.clear();
	}

	/**
	 * Method used to get widget
	 */
	@LogExecutionTime
	public void addWidget(){
		String id = Faces.getRequestParameter("id");
		if (NumberUtils.isNumber(id)) {
			selectedWidget = widgetService.getwidget(Long.valueOf(id));
			if (selectedWidget != null) {
				widgetVariableResponses = JavascriptUtils.extractVariables(selectedWidget.getBackendJs());
			}
		}
	}

	/**
	 * Method used to schedule a widget
	 */
	@LogExecutionTime
	public void scheduleWidget(){
		widgetService.scheduleWidget(selectedProjectWidget.getId());
		selectedProjectWidget = widgetService.getWidgetProject(selectedProjectWidget.getId(),homeUserManagedBean.getSelectedProject().getId());
		if (WidgetState.STOPPED == selectedProjectWidget.getState()) {
			addMessageForClient(FacesMessage.SEVERITY_ERROR,"logs", "app.user.widget.config.relaunch.error");
		} else {
			addMessageForClient(FacesMessage.SEVERITY_INFO,"logs", "app.user.widget.config.relaunch");
		}
		homeUserManagedBean.updateWidgetResponse();
	}

	/**
	 * Update project widget
	 *
	 * @param projectWidgetId projectwidget ID
	 * @param style the custom widget style
	 * @param backendConfig backend configuration
	 */
	@LogExecutionTime
	private void updateProjectWidget(Long projectWidgetId, String projectToken, String style, String backendConfig){
		widgetService.updateProjectWidget(projectWidgetId, style, backendConfig);
		// Update grid
		socketService.updateProjectScreen(projectToken,  new UpdateEvent(UpdateType.GRID));
	}

	/**
	 * Get the selected widget
	 * @return the selected widget
	 */
	public Widget getSelectedWidget() {
		return selectedWidget;
	}

	/**
	 * Method used to get all widget variables
	 * @return list of widget variable
	 */
	public List<WidgetVariableResponse> getWidgetVariableResponses() {
		return widgetVariableResponses;
	}
	/**
	 * Get the selected instance widget
	 * @return the selected instance widget
	 */
	public ProjectWidget getSelectedProjectWidget() {
		return selectedProjectWidget;
	}

	/**
	 * Map of parameters used to edit the widget instance configuration
	 * @return the map of parameters
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * Method used to define parameters
	 * @param parameters parameters
	 */
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getSearchWidget() {
		return searchWidget;
	}

	public void setSearchWidget(String searchWidget) {
		this.searchWidget = searchWidget;
	}


	public List<Map.Entry<Category, List<Widget>>> getCategoryListMap() {
		return categoryListMap;
	}

	public void setCategoryListMap(List<Map.Entry<Category, List<Widget>>> categoryListMap) {
		this.categoryListMap = categoryListMap;
	}
}
