/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.service.api;

import com.michelin.suricate.model.dto.api.widget.WidgetRequestDto;
import com.michelin.suricate.model.dto.js.WidgetVariableResponseDto;
import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.Library;
import com.michelin.suricate.model.entity.Repository;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.entity.WidgetParam;
import com.michelin.suricate.model.entity.WidgetParamValue;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import com.michelin.suricate.model.enumeration.RepositoryTypeEnum;
import com.michelin.suricate.model.enumeration.WidgetAvailabilityEnum;
import com.michelin.suricate.repository.WidgetParamRepository;
import com.michelin.suricate.repository.WidgetRepository;
import com.michelin.suricate.service.specification.WidgetSearchSpecification;
import io.jsonwebtoken.lang.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.internal.util.CollectionsUtils;
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

            if (widgetVariableResponseDto.getType() == DataTypeEnum.COMBO
                || widgetVariableResponseDto.getType() == DataTypeEnum.MULTIPLE) {
                widgetVariableResponseDto.setValues(
                    getWidgetParamValuesAsMap(widgetParameter.getPossibleValuesMap()));
            } else {
                widgetVariableResponseDto.setData(StringUtils.trimToNull(widgetParameter.getDefaultValue()));
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
            Optional<Widget> existingWidget = widgetRepository.findByTechnicalName(widget.getTechnicalName());

            if (!Collections.isEmpty(widget.getLibraries()) && !Collections.isEmpty(libraries)) {
                List<Library> widgetLibraries = new ArrayList<>(widget.getLibraries());

                widgetLibraries.replaceAll(widgetLibrary -> libraries
                    .stream()
                    .filter(library -> library.getTechnicalName().equals(widgetLibrary.getTechnicalName()))
                    .findFirst().orElse(null));

                widget.setLibraries(new HashSet<>(widgetLibraries));
            }

            if (widget.getImage() != null) {
                if (existingWidget.isPresent() && existingWidget.get().getImage() != null) {
                    widget.getImage().setId(existingWidget.get().getImage().getId());
                }

                assetService.save(widget.getImage());
            }

            // Replace the existing list of params and values by the new one
            if (!Collections.isEmpty(widget.getWidgetParams()) && existingWidget.isPresent()
                && !Collections.isEmpty(existingWidget.get().getWidgetParams())) {
                Set<WidgetParam> currentWidgetParams = existingWidget.get().getWidgetParams();

                widget.getWidgetParams().forEach(widgetParam -> {
                    Optional<WidgetParam> widgetParamToFind = currentWidgetParams
                        .stream()
                        .filter(currentParam -> currentParam.getName().equals(widgetParam.getName()))
                        .findAny();

                    widgetParamToFind.ifPresent(currentWidgetParamFound -> {
                        // Set the ID of the new object with the current one
                        widgetParam.setId(currentWidgetParamFound.getId());

                        // Search params with the current WidgetParam in DB
                        if (!Collections.isEmpty(widgetParam.getPossibleValuesMap())
                            && !Collections.isEmpty(currentWidgetParamFound.getPossibleValuesMap())) {

                            widgetParam.getPossibleValuesMap().forEach(possibleValueMap -> {
                                // Search the current widget possible values in DB
                                Optional<WidgetParamValue> possibleValueMapToFind =
                                    currentWidgetParamFound.getPossibleValuesMap()
                                        .stream()
                                        .filter(currentPossibleValueMap -> currentPossibleValueMap.getJsKey()
                                            .equals(possibleValueMap.getJsKey()))
                                        .findAny();
                                // Set ID of the new object with the current one in DB
                                possibleValueMapToFind.ifPresent(
                                    possibleValueMapFound -> possibleValueMap.setId(possibleValueMapFound.getId())
                                );
                            });
                        }
                    });
                });
            }

            // Set ID and remove parameters which are not present anymore
            if (existingWidget.isPresent()) {
                // Keep the previous widget state
                widget.setWidgetAvailability(existingWidget.get().getWidgetAvailability());
                widget.setId(existingWidget.get().getId());

                List<Long> idsToDelete = existingWidget.get().getWidgetParams()
                    .stream()
                    .filter(oldWidgetParameter -> widget.getWidgetParams()
                        .stream()
                        .noneMatch(newWidgetParam -> newWidgetParam.getName().equals(oldWidgetParameter.getName())))
                    .map(WidgetParam::getId)
                    .toList();

                if (!Collections.isEmpty(idsToDelete)) {
                    widgetParamRepository.deleteAllById(idsToDelete);
                }
            }

            if (widget.getWidgetAvailability() == null) {
                widget.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);
            }

            widget.setCategory(category);
            widget.setRepository(repository);

            widgetRepository.save(widget);

            String type = repository.getType().equals(RepositoryTypeEnum.LOCAL) ? "local path" : "branch";
            String where = repository.getType().equals(RepositoryTypeEnum.LOCAL) ? repository.getLocalPath() : repository.getBranch();
            log.info("Widget {} updated from the {} {} of the repository {}",
                widget.getTechnicalName(),
                type,
                where,
                widget.getRepository().getName()
            );
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
