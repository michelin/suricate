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

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.dto.api.widget.WidgetRequestDto;
import io.suricate.monitoring.model.dto.nashorn.WidgetVariableResponse;
import io.suricate.monitoring.model.entities.*;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.suricate.monitoring.repositories.WidgetParamRepository;
import io.suricate.monitoring.repositories.WidgetRepository;
import io.suricate.monitoring.services.CacheService;
import io.suricate.monitoring.services.specifications.WidgetSearchSpecification;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Widget service
 */
@Service
public class WidgetService {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetService.class);

    /**
     * Cache service
     */
    private final CacheService cacheService;

    /**
     * Asset repository
     */
    private final AssetService assetService;

    /**
     * Widget repository
     */
    private final WidgetRepository widgetRepository;

    /**
     * Widget parameter repository
     */
    private final WidgetParamRepository widgetParamRepository;

    /**
     * Category service
     */
    private final CategoryService categoryService;

    /**
     * Constructor
     *
     * @param widgetRepository           The widget repository
     * @param widgetParamRepository      The widget param repository
     * @param categoryService            The category service
     * @param widgetParametersService The configuration service
     * @param cacheService               The cache service
     * @param assetService               The asset service
     */
    @Autowired
    public WidgetService(final WidgetRepository widgetRepository,
                         final WidgetParamRepository widgetParamRepository,
                         final CategoryService categoryService,
                         final CacheService cacheService,
                         final AssetService assetService) {
        this.widgetRepository = widgetRepository;
        this.widgetParamRepository = widgetParamRepository;
        this.categoryService = categoryService;
        this.cacheService = cacheService;
        this.assetService = assetService;
    }

    /**
     * Return every widgets order by category name
     *
     * @return The list of widgets order by category name
     */
    @Transactional
    public Page<Widget> getAll(String search, Pageable pageable) {
        return widgetRepository.findAll(new WidgetSearchSpecification(search), pageable);
    }

    /**
     * Find a widget by id
     *
     * @param id The widget id
     * @return The related widget
     */
    public Widget findOne(final Long id) {
        Optional<Widget> widgetOptional = widgetRepository.findById(id);
        return widgetOptional.orElse(null);
    }

    /**
     * Get every widgets for a category
     *
     * @param categoryId The category id used for found widgets
     * @return The list of related widgets
     */
    @Transactional
    public Optional<List<Widget>> getWidgetsByCategory(final Long categoryId) {
        List<Widget> widgets = widgetRepository.findAllByCategory_IdOrderByNameAsc(categoryId);

        if (widgets == null || widgets.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(widgets);
    }

    /**
     * Return the full list of parameters of a widget including the parameters of the widget
     * and the global parameters of the category
     *
     * @param widget The widget
     * @return A list of parameters
     */
    @Transactional
    public List<WidgetParam> getWidgetParametersWithCategoryParameters(final Widget widget) {
        List<WidgetParam> widgetParameters = new ArrayList<>(widget.getWidgetParams());
        widgetParameters.addAll(this.categoryService.getCategoryParametersByWidget(widget));

        return widgetParameters;
    }

    /**
     * Get the list of widget parameters
     *
     * @param widget The widget
     * @return The list of widget parameters
     */
    @Transactional
    public List<WidgetVariableResponse> getWidgetParametersForNashorn(final Widget widget) {
        List<WidgetVariableResponse> widgetVariableResponses = new ArrayList<>();

        List<WidgetParam> widgetParameters = getWidgetParametersWithCategoryParameters(widget);

        for (WidgetParam widgetParameter : widgetParameters) {
            WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
            widgetVariableResponse.setName(widgetParameter.getName());
            widgetVariableResponse.setDescription(widgetParameter.getDescription());
            widgetVariableResponse.setType(widgetParameter.getType());
            widgetVariableResponse.setDefaultValue(widgetParameter.getDefaultValue());

            if (widgetVariableResponse.getType() != null) {
                switch (widgetVariableResponse.getType()) {
                    case COMBO:

                    case MULTIPLE:
                        widgetVariableResponse.setValues(getWidgetParamValuesAsMap(widgetParameter.getPossibleValuesMap()));
                        break;

                    default:
                        widgetVariableResponse.setData(StringUtils.trimToNull(widgetParameter.getDefaultValue()));
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
     * Update a widget
     *
     * @param widgetId         The widget id to update
     * @param widgetRequestDto The object that holds changes
     * @return The widget update
     */
    public Optional<Widget> updateWidget(final Long widgetId, final WidgetRequestDto widgetRequestDto) {
        if (!widgetRepository.existsById(widgetId)) {
            return Optional.empty();
        }

        Widget widgetToBeModified = findOne(widgetId);
        widgetToBeModified.setWidgetAvailability(widgetRequestDto.getWidgetAvailability());

        return Optional.of(widgetRepository.save(widgetToBeModified));
    }


    /**
     * Update categories and widgets in database with the new list
     *
     * @param categories The list of categories with widgets
     * @param mapLibrary The libraries
     * @param repository The Git Repository
     */
    @Transactional
    public void updateWidgetInDatabase(List<Category> categories, Map<String, Library> mapLibrary, final Repository repository) {
        for (Category category : categories) {
            categoryService.addOrUpdateCategory(category);

            addOrUpdateWidgets(category, category.getWidgets(), mapLibrary, repository);
        }

        cacheService.clearAllCache();
    }

    /**
     * Add or update the given widgets from the given repository
     *
     * Find the matching existing widget if it exists.
     *
     * Update the libraries of the widget.
     *
     * Update the image of the widget.
     *
     * Update the parameters of the widget. If the new widget does not contain some
     * parameters anymore, then delete these parameters.
     *
     * Set the activated state by default to the widget.
     *
     * Set the category and the repository to the widget.
     *
     * @param category   The category
     * @param widgets    The related widgets
     * @param mapLibrary The libraries
     * @param repository The git repository
     */
    @Transactional
    public void addOrUpdateWidgets(Category category, List<Widget> widgets, Map<String, Library> mapLibrary, final Repository repository) {
        if (category == null || widgets == null) {
            return;
        }

        for (Widget widget : widgets) {
            Widget currentWidget = widgetRepository.findByTechnicalName(widget.getTechnicalName());

            if (widget.getLibraries() != null && mapLibrary != null) {
                widget.getLibraries()
                        .replaceAll(library -> mapLibrary.get(library.getTechnicalName()));
            }

            if (widget.getImage() != null) {
                if (currentWidget != null && currentWidget.getImage() != null) {
                    widget.getImage().setId(currentWidget.getImage().getId());
                }

                assetService.save(widget.getImage());
            }

            // Replace the existing list of params and values by the new one
            if (widget.getWidgetParams() != null && !widget.getWidgetParams().isEmpty() &&
                currentWidget != null && currentWidget.getWidgetParams() != null && !currentWidget.getWidgetParams().isEmpty()) {

                List<WidgetParam> currentWidgetParams = currentWidget.getWidgetParams();

                widget.getWidgetParams().forEach(widgetParam -> {
                    Optional<WidgetParam> widgetParamToFind = currentWidgetParams
                        .stream()
                        .filter(currentParam -> currentParam.getName().equals(widgetParam.getName()))
                        .findAny();

                    widgetParamToFind.ifPresent(currentWidgetParamFound -> {
                        // Set the ID of the new object with the current one
                        widgetParam.setId(currentWidgetParamFound.getId());

                        // Search params with the current WidgetParam in DB
                        if (widgetParam.getPossibleValuesMap() != null && !widgetParam.getPossibleValuesMap().isEmpty() &&
                            currentWidgetParamFound.getPossibleValuesMap() != null && !currentWidgetParamFound.getPossibleValuesMap().isEmpty()) {

                            widgetParam.getPossibleValuesMap().forEach(possibleValueMap -> {
                                //Search the current widget possible values in DB
                                Optional<WidgetParamValue> possibleValueMapToFind = currentWidgetParamFound.getPossibleValuesMap()
                                    .stream()
                                    .filter(currentPossibleValueMap -> currentPossibleValueMap.getJsKey().equals(possibleValueMap.getJsKey()))
                                    .findAny();
                                //Set ID of the new object with the current one in DB
                                possibleValueMapToFind.ifPresent(possibleValueMapFound -> possibleValueMap.setId(possibleValueMapFound.getId()));
                            });
                        }
                    });
                });
            }

            // Set ID and remove parameters which are not present anymore
            if (currentWidget != null) {
                widget.setWidgetAvailability(currentWidget.getWidgetAvailability()); // Keep the previous widget state
                widget.setId(currentWidget.getId());

                for (WidgetParam oldWidgetParameter : currentWidget.getWidgetParams()) {
                    if (!widget.getWidgetParams().contains(oldWidgetParameter)) {
                        widgetParamRepository.deleteById(oldWidgetParameter.getId());
                    }
                }
            }

            if (widget.getWidgetAvailability() == null) {
                widget.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);
            }

            widget.setCategory(category);
            widget.setRepository(repository);

            LOGGER.info("Widget {} updated from the branch {} of the repository {}", widget.getTechnicalName(),
                    widget.getRepository().getBranch(), widget.getRepository().getName());

            widgetRepository.save(widget);
        }
    }
}
