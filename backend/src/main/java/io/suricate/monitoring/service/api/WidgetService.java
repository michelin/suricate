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

package io.suricate.monitoring.service.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import io.suricate.monitoring.controllers.api.error.exception.ApiException;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.dto.widget.*;
import io.suricate.monitoring.model.entity.*;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.entity.widget.WidgetParam;
import io.suricate.monitoring.model.entity.widget.WidgetParamValue;
import io.suricate.monitoring.model.enums.*;
import io.suricate.monitoring.repository.*;
import io.suricate.monitoring.service.CacheService;
import io.suricate.monitoring.service.SocketService;
import io.suricate.monitoring.service.nashorn.NashornWidgetExecutor;
import io.suricate.monitoring.service.webSocket.DashboardWebSocketService;
import io.suricate.monitoring.service.WidgetExecutor;
import io.suricate.monitoring.service.search.SearchService;
import io.suricate.monitoring.utils.EntityUtils;
import io.suricate.monitoring.utils.JavascriptUtils;
import io.suricate.monitoring.utils.PropertiesUtils;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Widget service
 */
@Service
public class WidgetService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetService.class);

    /**
     * The mustacheFactory
     */
    private final MustacheFactory mustacheFactory;

    /**
     * Project widget repository
     */
    private final ProjectWidgetRepository projectWidgetRepository;

    /**
     * Widget repository
     */
    private final WidgetRepository widgetRepository;

    /**
     * Category repository
     */
    private final CategoryRepository categoryRepository;

    /**
     * Socket service
     */
    private final DashboardWebSocketService dashboardWebsocketService;

    /**
     * Cache service
     */
    private final CacheService cacheService;

    /**
     * Asset repository
     */
    private final AssetRepository assetRepository;

    /**
     * Search service
     */
    private final SearchService searchService;

    /**
     * Object Mapper
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * The application context
     */
    private final ApplicationContext ctx;

    /**
     * Constructor
     *
     * @param mustacheFactory The mustache factory (HTML template)
     * @param projectWidgetRepository The project widget repository
     * @param widgetRepository The widget repository
     * @param categoryRepository The category repository
     * @param dashboardWebsocketService The socket service
     * @param cacheService The cache service
     * @param ctx The application context
     * @param assetRepository The asset repository
     * @param searchService The search service
     */
    @Autowired
    public WidgetService(MustacheFactory mustacheFactory, ProjectWidgetRepository projectWidgetRepository, WidgetRepository widgetRepository, CategoryRepository categoryRepository, DashboardWebSocketService dashboardWebsocketService, CacheService cacheService, ApplicationContext ctx, AssetRepository assetRepository, SearchService searchService) {
        this.mustacheFactory = mustacheFactory;
        this.projectWidgetRepository = projectWidgetRepository;
        this.widgetRepository = widgetRepository;
        this.categoryRepository = categoryRepository;
        this.dashboardWebsocketService = dashboardWebsocketService;
        this.cacheService = cacheService;
        this.ctx = ctx;
        this.assetRepository = assetRepository;
        this.searchService = searchService;
    }

    /**
     * Tranform a list of Domain objects into a DTO objects
     * @param widgets The list of widgets to tranform
     * @return The list as DTO objects
     */
    private List<WidgetResponse> transformIntoDTO(List<Widget> widgets) {
        List<WidgetResponse> widgetResponses = new ArrayList<>();

        for(Widget widget : widgets) {
            WidgetResponse widgetResponse = new WidgetResponse();
            widgetResponse.setWidgetId(widget.getId());
            widgetResponse.setName(widget.getName());
            widgetResponse.setDescription(widget.getDescription());
            widgetResponse.setInfo(widget.getInfo());
            widgetResponse.setImage(widget.getImage());
            widgetResponse.getWidgetParams().addAll(extractWidgetParams(widget));

            widgetResponses.add(widgetResponse);
        }

        return widgetResponses;
    }

    /**
     * Extract the list of widget params of a widget
     *
     * @param widget The widget
     * @return The related list of params
     */
    private List<WidgetParamResponse> extractWidgetParams(Widget widget) {
        List<WidgetParamResponse> widgetParamResponses = new ArrayList<>();

        if(widget.getWidgetParams() != null && !widget.getWidgetParams().isEmpty()) {
            for (WidgetParam widgetParam: widget.getWidgetParams()) {
                WidgetParamResponse widgetParamResponse = new WidgetParamResponse();

                widgetParamResponse.setName(widgetParam.getName());
                widgetParamResponse.setDescription(widgetParam.getDescription());
                widgetParamResponse.setDefaultValue(widgetParam.getDefaultValue());
                widgetParamResponse.setType(widgetParam.getType());
                widgetParamResponse.setAcceptFileRegex(widgetParam.getAcceptFileRegex());
                widgetParamResponse.setUsageExample(widgetParam.getUsageExample());
                widgetParamResponse.setRequired(widgetParam.isRequired());

                if(widgetParam.getPossibleValuesMap() != null && !widgetParam.getPossibleValuesMap().isEmpty()) {
                    for(WidgetParamValue widgetParamValue : widgetParam.getPossibleValuesMap()) {
                        WidgetParamValueResponse widgetParamValueResponse = new WidgetParamValueResponse();

                        widgetParamValueResponse.setJsKey(widgetParamValue.getJsKey());
                        widgetParamValueResponse.setValue(widgetParamValue.getValue());

                        widgetParamResponse.getValues().add(widgetParamValueResponse);
                    }
                }

                widgetParamResponses.add(widgetParamResponse);
            }
        }

        return widgetParamResponses;
    }

    /**
     * Get every categories
     * @return The list of categories
     */
    @Transactional
    @Cacheable("widget-categories")
    public List<Category> getCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    /**
     * Get every widgets for a category
     *
     * @param categoryId The category id used for found widgets
     * @return The list of related widgets
     */
    @Transactional
    public List<WidgetResponse> getWidgetsByCategory(final Long categoryId) {
        return transformIntoDTO(widgetRepository.findAllByCategory_IdOrderByNameAsc(categoryId));
    }

    /**
     * Get the list of the variables for a widget
     *
     * @param widget The widget
     * @return The list of variables related
     */
    public List<WidgetVariableResponse> getWidgetVariables(final Widget widget) {
        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();

        for(WidgetParam widgetParam: widget.getWidgetParams()) {
            WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
            widgetVariableResponse.setName(widgetParam.getName());
            widgetVariableResponse.setDescription(widgetParam.getDescription());
            widgetVariableResponse.setType(widgetParam.getType());

            if(widgetVariableResponse.getType() != null) {
                switch(widgetVariableResponse.getType()) {
                    case COMBO:
                        widgetVariableResponse.setValues(getWidgetParamValuesAsMap(widgetParam.getPossibleValuesMap()));
                        break;

                    case MULTIPLE:
                        widgetVariableResponse.setValues(getWidgetParamValuesAsMap(widgetParam.getPossibleValuesMap()));
                        break;

                    default:
                        widgetVariableResponse.setData(StringUtils.trimToNull(widgetParam.getDefaultValue()));
                        break;
                }
            }

            widgetVariableResponses.add(widgetVariableResponse);
        }

        return widgetVariableResponses;
    }

    /**
     * Get the widget param list as a Map
     *
     * @param widgetParamValues The list of the widget param values
     * @return The list as a Map<String, String>
     */
    public Map<String, String> getWidgetParamValuesAsMap(List<WidgetParamValue> widgetParamValues) {
        return widgetParamValues
            .stream()
            .collect(Collectors.toMap(WidgetParamValue::getJsKey, WidgetParamValue::getValue));
    }







    /**
     * Method used to add project widget
     * @param projectWidget the project widget to save
     */
    @Transactional
    public ProjectWidget addprojectWidget(ProjectWidget projectWidget){
        projectWidget = projectWidgetRepository.save(projectWidget);
        scheduleWidget(projectWidget.getId());

        return projectWidget;
    }

    /**
     * Get a width with it's id
     * @param id the widget id
     * @return the widget object
     */
    @Transactional
    @Cacheable("widget-data")
    public Widget getwidget(Long id){
        return widgetRepository.findOne(id);
    }


    /**
     * Get every widgets for a project
     *
     * @param projectId The project id
     * @return The list of widgets for this project
     */
    @Transactional
    @LogExecutionTime
    public List<WidgetResponse> getWidgets(Long projectId){
        List<WidgetResponse> ret = new ArrayList<>();

        List<ProjectWidget> projectWidgets = projectWidgetRepository.findByProjectIdAndWidget_WidgetAvailabilityOrderById(projectId, WidgetAvailabilityEnum.ACTIVATED);
        for (ProjectWidget projectWidget: projectWidgets){
            ret.add(getWidgetResponse(projectWidget));
        }
        return ret;
    }

    /**
     * Method used to get widget response from a project widget
     * @param projectWidget the project widget
     * @return a widgetresponse object
     */
    @Transactional
    public WidgetResponse getWidgetResponse(ProjectWidget projectWidget) {
        Map<String, Object> map = null;
        Widget widget = projectWidget.getWidget();

        String content = widget.getHtmlContent();
        if (StringUtils.isNotEmpty(projectWidget.getData())) {
            try {
                map = objectMapper.readValue(projectWidget.getData(), new TypeReference<Map<String, Object>>() {});
                // Add backend config
                map.putAll(PropertiesUtils.getMap(projectWidget.getBackendConfig()));
                map.put(JavascriptUtils.INSTANCE_ID_VARIABLE, projectWidget.getId());
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }

            StringWriter stringWriter = new StringWriter();
            try {
                Mustache mustache = mustacheFactory.compile(new StringReader(content), widget.getTechnicalName());
                mustache.execute(stringWriter, map);
            } catch (MustacheException me){
                LOGGER.error("Error with mustache template for widget {}", widget.getTechnicalName(), me);
            }
            stringWriter.flush();
            content = stringWriter.toString();
        }

        // Create widget response
        WidgetResponse response = new WidgetResponse();
        response.setHeight(projectWidget.getHeight());
        response.setWidth(projectWidget.getWidth());
        response.setRow(projectWidget.getRow());
        response.setCol(projectWidget.getCol());
        response.setCss(StringUtils.trimToEmpty(widget.getCssContent()));
        response.setCustomCss(StringUtils.trimToEmpty(projectWidget.getCustomStyle()));
        response.setHtml(content);
        response.setImageId(EntityUtils.getProxiedId(widget.getImage()));
        response.setId(widget.getTechnicalName());
        response.setWidgetId(widget.getId());
        response.setProjectWidgetId(projectWidget.getId());
        response.setError(widget.getDelay() > 0 && WidgetState.STOPPED == projectWidget.getState());
        response.setWarning(WidgetState.WARNING == projectWidget.getState());

        return response;
    }

    /**
     * Method used to update all widgets positions for a current project
     * @param projectId the project id
     * @param positions lit of position
     * @param projetToken project token
     */
    @Transactional
    public void update(Long projectId, List<WidgetPosition> positions, String projetToken){
        List<ProjectWidget> projectWidgets = projectWidgetRepository.findByProjectIdAndWidget_WidgetAvailabilityOrderById(projectId, WidgetAvailabilityEnum.ACTIVATED);
        if (projectWidgets.size() != positions.size()) {
            throw new ApiException(ApiErrorEnum.PROJECT_INVALID_CONSTANCY);
        }

        int i = 0;
        for (ProjectWidget projectWidget : projectWidgets){
            projectWidgetRepository.updateRowAndColAndWidthAndHeightById(positions.get(i).getRow(),
                    positions.get(i).getCol(),
                    positions.get(i).getSizeX(),
                    positions.get(i).getSizeY(),
                    projectWidget.getId()
                    );
            i++;
        }
        projectWidgetRepository.flush();
        // notify clients
        dashboardWebsocketService.updateProjectScreen(projetToken, new UpdateEvent(UpdateType.POSITION));
    }

    /**
     * Method used to remove widget from the dashboard
     * @param projectId the project id
     * @param projectWidgetId the projectwidget id
     */
    @Transactional
    public void removeWidget(Long projectId, Long projectWidgetId){
        ctx.getBean(NashornWidgetExecutor.class).cancelWidgetInstance(projectWidgetId);
        projectWidgetRepository.deleteByProjectIdAndId(projectId, projectWidgetId);
        projectWidgetRepository.flush();
        // notify client
        dashboardWebsocketService.updateProjectScreen(projectId, new UpdateEvent(UpdateType.GRID));
    }

    /**
     * Method used to get all widget by category
     * @param widgetAvailability the availability of widgets
     * @return a map sorted by category which contain a list of widgets
     */
    @Transactional
    @Cacheable("widget-by-category")
    public Map<Category,List<Widget>> getAvailableWidget(WidgetAvailabilityEnum widgetAvailability, String search) {
        LOGGER.debug("Search widgets with terms '{}', for availability {}", search, widgetAvailability);
        Map<Category,List<Widget>> ret = new LinkedHashMap<>();
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();
        List<Widget> widgets = null;
        if (StringUtils.isNotBlank(search)){
            widgets = searchService.searchWidgets(widgetAvailability, search);
        } else {
            if (widgetAvailability == null) {
                widgets = widgetRepository.findAllByOrderByNameAsc();
            } else {
                widgets = widgetRepository.findAllByWidgetAvailabilityOrderByNameAsc(widgetAvailability);
            }
        }

        for (Category category : categories) {
            List<Widget> cateWidget = new ArrayList<>();
            for (Widget widget : widgets) {
                if (widget.getCategory() != null && category.getId().equals(widget.getCategory().getId())) {
                    cateWidget.add(widget);
                }
            }
            if (!cateWidget.isEmpty()) {
                widgets.removeAll(cateWidget);
                ret.put(category, cateWidget);
            }
        }

        return ret;
    }

    /**
     * Get widget by project widget id and project id
     * @param projectWidgetId the widget id
     * @param projectId project Id
     * @return the widget object
     */
    @Transactional
    public Widget getwidgetByProjectWidgetId(Long projectWidgetId, Long projectId){
        return widgetRepository.findByProjectWidgetId(projectWidgetId, projectId);
    }

    /**
     * Get widget by project widget id and project id
     * @param projectWidgetId the widget id
     * @param projectId project Id
     * @return the widget object
     */
    @Transactional
    public ProjectWidget getWidgetProject(Long projectWidgetId, Long projectId){
        return projectWidgetRepository.findByIdAndProject_Id(projectWidgetId, projectId);
    }


    /**
     * Method used to schedule a widget
     * @param projectWidgetId The project widget id
     */
    @Transactional
    public void scheduleWidget(Long projectWidgetId){
        ctx.getBean(NashornWidgetExecutor.class).cancelAndSchedule(projectWidgetRepository.getRequestByProjectWidgetId(projectWidgetId));
    }

    /**
     * Method used to update the configuration and custom css for project widget
     *
     * @param projectWidgetId The project widget id
     * @param style The custom css style
     * @param backendConfig The backend configuration
     *
     */
    @Transactional
    public void updateProjectWidget(Long projectWidgetId, String style, String backendConfig){
        projectWidgetRepository.updateConfig(projectWidgetId, style, backendConfig);
    }

    /**
     * Update categories and widgets in database with the new list
     *
     * @param list The list of categories + widgets
     * @param mapLibrary The libraries
     */
    @Transactional
    public void updateWidgetInDatabase(List<Category> list, Map<String, Library> mapLibrary){
        for (Category category : list){
            addOrUpdateCategory(category);
            // Create/update widgets
            addOrUpdateWidgets(category, category.getWidgets(), mapLibrary);
        }
        cacheService.clearAllCache();
    }

    /**
     * Add or update a list of widgets in database
     *
     * @param category The category
     * @param widgets The related widgets
     * @param mapLibrary The libraries
     */
    @Transactional
    public void addOrUpdateWidgets(Category category, List<Widget> widgets, Map<String, Library> mapLibrary){
        if (category == null || widgets == null) {
            return;
        }
        for (Widget widget : widgets){
            if (widget.getLibraries() != null && mapLibrary != null) {
                widget.getLibraries().replaceAll(x -> mapLibrary.get(x.getTechnicalName()));
            }

            // Find existing widget
            Widget currentWidget = widgetRepository.findByTechnicalName(widget.getTechnicalName());
            if (widget.getImage() != null){
                if (currentWidget != null && currentWidget.getImage() != null) {
                    widget.getImage().setId(currentWidget.getImage().getId());
                }
                assetRepository.save(widget.getImage());
            }

            //Replace The existing list of params and values by the new one
            if(widget.getWidgetParams() != null && !widget.getWidgetParams().isEmpty() &&
                    currentWidget != null && currentWidget.getWidgetParams() != null && !currentWidget.getWidgetParams().isEmpty()) {

                List<WidgetParam> currentWidgetParams = currentWidget.getWidgetParams();

                //List of params
                widget.getWidgetParams().forEach( widgetParam -> {
                    //Search in current list in DB
                    Optional<WidgetParam> widgetParamToFind = currentWidgetParams
                                                                    .stream()
                                                                    .filter(currentParam -> currentParam.getName().equals(widgetParam.getName()))
                                                                    .findAny();

                    widgetParamToFind.ifPresent(currentWidgetParamFound -> {
                        //Set the ID of the new object with the current one
                        widgetParam.setId(currentWidgetParamFound.getId());

                        //Search params with the current WidgetParam in DB
                        if(widgetParam.getPossibleValuesMap() != null && !widgetParam.getPossibleValuesMap().isEmpty() &&
                                currentWidgetParamFound.getPossibleValuesMap() != null && !currentWidgetParamFound.getPossibleValuesMap().isEmpty()) {

                            widgetParam.getPossibleValuesMap().forEach(possibleValueMap -> {
                                //Search the current widget possible values in DB
                                Optional<WidgetParamValue> possibleValueMapToFind = currentWidgetParamFound.getPossibleValuesMap()
                                                                                        .stream()
                                                                                        .filter(currentPossibleValueMap -> currentPossibleValueMap.getJsKey().equals(possibleValueMap.getJsKey()) )
                                                                                        .findAny();
                                //Set ID of the new object with the current one in DB
                                possibleValueMapToFind.ifPresent(possibleValueMapFound -> possibleValueMap.setId(possibleValueMapFound.getId()));
                            });
                        }
                    });
                });
            }

            // Set Id
            if (currentWidget != null) {
                widget.setWidgetAvailability(currentWidget.getWidgetAvailability()); // Keep the previous widget state
                widget.setId(currentWidget.getId());
            }
            // Set activated state by default
            if (widget.getWidgetAvailability() == null){
                widget.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);
            }

            // set category
            widget.setCategory(category);

            widgetRepository.save(widget);
        }
    }

    /**
     * Method used to add or update an category
     * @param category the category to add
     */
    @Transactional
    public void addOrUpdateCategory(Category category) {
        if (category == null){
            return;
        }
        // Find and existing category with the same id
        Category currentCateg = categoryRepository.findByTechnicalName(category.getTechnicalName());
        if (category.getImage() != null) {
            if (currentCateg != null && currentCateg.getImage() != null){
                category.getImage().setId(currentCateg.getImage().getId());
            }
            assetRepository.save(category.getImage());
        }

        if (currentCateg != null){
            category.setId(currentCateg.getId());
        }

        // Create/Update category
        categoryRepository.save(category);
    }
}
