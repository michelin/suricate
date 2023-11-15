/*
 * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.services.api;

import com.michelin.suricate.model.dto.api.widget.WidgetRequestDto;
import com.michelin.suricate.model.dto.js.WidgetVariableResponseDto;
import com.michelin.suricate.model.entities.Category;
import com.michelin.suricate.model.entities.Library;
import com.michelin.suricate.model.entities.Repository;
import com.michelin.suricate.model.entities.Widget;
import com.michelin.suricate.model.entities.WidgetParam;
import com.michelin.suricate.model.entities.WidgetParamValue;
import com.michelin.suricate.model.enums.WidgetAvailabilityEnum;
import com.michelin.suricate.repositories.WidgetParamRepository;
import com.michelin.suricate.repositories.WidgetRepository;
import com.michelin.suricate.services.specifications.WidgetSearchSpecification;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Widget service.
 */
@Slf4j
@Service
public class WidgetService {
    @Autowired
    private AssetService assetService;

    @Autowired
    private WidgetRepository widgetRepository;

    @Autowired
    private WidgetParamRepository widgetParamRepository;

    @Autowired
    private CategoryService categoryService;

    /**
     * Find a widget by id.
     *
     * @param id The id
     * @return The widget
     */
    @Transactional(readOnly = true)
    public Optional<Widget> findOne(final Long id) {
        return widgetRepository.findById(id);
    }

    /**
     * Find a widget by technical name.
     *
     * @param technicalName The technical name
     * @return The widget
     */
    @Transactional(readOnly = true)
    public Optional<Widget> findOneByTechnicalName(final String technicalName) {
        return widgetRepository.findByTechnicalName(technicalName);
    }

    /**
     * Return every widgets order by category name.
     *
     * @return The list of widgets order by category name
     */
    @Transactional(readOnly = true)
    public Page<Widget> getAll(String search, Pageable pageable) {
        return widgetRepository.findAll(new WidgetSearchSpecification(search), pageable);
    }

    /**
     * Get every widget for a category.
     *
     * @param categoryId The category id used for found widgets
     * @return The list of related widgets
     */
    @Transactional
    public Optional<List<Widget>> getWidgetsByCategory(final Long categoryId) {
        List<Widget> widgets = widgetRepository.findAllByCategoryIdOrderByNameAsc(categoryId);

        if (widgets == null || widgets.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(widgets);
    }

    /**
     * Return the full list of parameters of a widget including the parameters of the widget
     * and the global parameters of the category.
     *
     * @param widget The widget
     * @return A list of parameters
     */
    @Transactional
    public List<WidgetParam> getWidgetParametersWithCategoryParameters(final Widget widget) {
        List<WidgetParam> widgetParameters = new ArrayList<>(widget.getWidgetParams());
        widgetParameters.addAll(categoryService.getCategoryParametersByWidget(widget));

        return widgetParameters;
    }

    /**
     * Get the list of widget parameters.
     *
     * @param widget The widget
     * @return The list of widget parameters
     */
    @Transactional
    public List<WidgetVariableResponseDto> getWidgetParametersForJsExecution(final Widget widget) {
        List<WidgetVariableResponseDto> widgetVariableResponseDtos = new ArrayList<>();

        List<WidgetParam> widgetParameters = getWidgetParametersWithCategoryParameters(widget);

        for (WidgetParam widgetParameter : widgetParameters) {
            WidgetVariableResponseDto widgetVariableResponseDto = new WidgetVariableResponseDto();
            widgetVariableResponseDto.setName(widgetParameter.getName());
            widgetVariableResponseDto.setDescription(widgetParameter.getDescription());
            widgetVariableResponseDto.setType(widgetParameter.getType());
            widgetVariableResponseDto.setDefaultValue(widgetParameter.getDefaultValue());

            if (widgetVariableResponseDto.getType() != null) {
                switch (widgetVariableResponseDto.getType()) {
                    case COMBO, MULTIPLE:
                        widgetVariableResponseDto.setValues(
                            getWidgetParamValuesAsMap(widgetParameter.getPossibleValuesMap()));
                        break;

                    default:
                        widgetVariableResponseDto.setData(StringUtils.trimToNull(widgetParameter.getDefaultValue()));
                        break;
                }
            }

            widgetVariableResponseDtos.add(widgetVariableResponseDto);
        }

        return widgetVariableResponseDtos;
    }

    /**
     * Update a widget.
     *
     * @param widgetId         The widget id to update
     * @param widgetRequestDto The object that holds changes
     * @return The widget update
     */
    @Transactional
    public Optional<Widget> updateWidget(final Long widgetId, final WidgetRequestDto widgetRequestDto) {
        Optional<Widget> widgetToBeModified = findOne(widgetId);
        if (widgetToBeModified.isPresent()) {
            widgetToBeModified.get().setWidgetAvailability(widgetRequestDto.getWidgetAvailability());
            return Optional.of(widgetRepository.save(widgetToBeModified.get()));
        }

        return Optional.empty();
    }

    /**
     * Add or update the given widgets from the given repository
     * Find the matching existing widget if it exists.
     * Update the libraries of the widget.
     * Update the image of the widget.
     * Update the parameters of the widget. If the new widget does not contain some
     * parameters anymore, then delete these parameters.
     * Set the activated state by default to the widget.
     * Set the category and the repository to the widget.
     *
     * @param category   The category
     * @param libraries  The libraries
     * @param repository The git repository
     */
    @Transactional
    public void addOrUpdateWidgets(Category category, List<Library> libraries, final Repository repository) {
        if (category == null || category.getWidgets() == null) {
            return;
        }

        for (Widget widget : category.getWidgets()) {
            Optional<Widget> currentWidget = widgetRepository.findByTechnicalName(widget.getTechnicalName());

            if (widget.getLibraries() != null && !widget.getLibraries().isEmpty() && libraries != null) {
                List<Library> widgetLibraries = new ArrayList<>(widget.getLibraries());

                widgetLibraries.replaceAll(widgetLibrary -> libraries
                    .stream()
                    .filter(library -> library.getTechnicalName().equals(widgetLibrary.getTechnicalName()))
                    .findFirst().orElse(null));

                widget.setLibraries(new HashSet<>(widgetLibraries));
            }

            if (widget.getImage() != null) {
                if (currentWidget.isPresent() && currentWidget.get().getImage() != null) {
                    widget.getImage().setId(currentWidget.get().getImage().getId());
                }

                assetService.save(widget.getImage());
            }

            // Replace the existing list of params and values by the new one
            if (widget.getWidgetParams() != null && !widget.getWidgetParams().isEmpty()
                && currentWidget.isPresent() && currentWidget.get().getWidgetParams() != null
                && !currentWidget.get().getWidgetParams().isEmpty()) {

                Set<WidgetParam> currentWidgetParams = currentWidget.get().getWidgetParams();

                widget.getWidgetParams().forEach(widgetParam -> {
                    Optional<WidgetParam> widgetParamToFind = currentWidgetParams
                        .stream()
                        .filter(currentParam -> currentParam.getName().equals(widgetParam.getName()))
                        .findAny();

                    widgetParamToFind.ifPresent(currentWidgetParamFound -> {
                        // Set the ID of the new object with the current one
                        widgetParam.setId(currentWidgetParamFound.getId());

                        // Search params with the current WidgetParam in DB
                        if (widgetParam.getPossibleValuesMap() != null
                            && !widgetParam.getPossibleValuesMap().isEmpty()
                            && currentWidgetParamFound.getPossibleValuesMap() != null
                            && !currentWidgetParamFound.getPossibleValuesMap().isEmpty()) {

                            widgetParam.getPossibleValuesMap().forEach(possibleValueMap -> {
                                //Search the current widget possible values in DB
                                Optional<WidgetParamValue> possibleValueMapToFind =
                                    currentWidgetParamFound.getPossibleValuesMap()
                                        .stream()
                                        .filter(currentPossibleValueMap -> currentPossibleValueMap.getJsKey()
                                            .equals(possibleValueMap.getJsKey()))
                                        .findAny();
                                //Set ID of the new object with the current one in DB
                                possibleValueMapToFind.ifPresent(
                                    possibleValueMapFound -> possibleValueMap.setId(possibleValueMapFound.getId()));
                            });
                        }
                    });
                });
            }

            // Set ID and remove parameters which are not present anymore
            if (currentWidget.isPresent()) {
                widget.setWidgetAvailability(
                    currentWidget.get().getWidgetAvailability()); // Keep the previous widget state
                widget.setId(currentWidget.get().getId());

                for (WidgetParam oldWidgetParameter : currentWidget.get().getWidgetParams()) {
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

            widgetRepository.save(widget);

            log.info("Widget {} updated from the branch {} of the repository {}", widget.getTechnicalName(),
                widget.getRepository().getBranch(), widget.getRepository().getName());
        }
    }

    /**
     * Get the widget param list as a Map.
     *
     * @param widgetParamValues The list of the widget param values
     * @return The list
     */
    public Map<String, String> getWidgetParamValuesAsMap(Set<WidgetParamValue> widgetParamValues) {
        return widgetParamValues
            .stream()
            .collect(Collectors.toMap(WidgetParamValue::getJsKey, WidgetParamValue::getValue));
    }
}
