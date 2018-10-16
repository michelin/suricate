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

package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.model.dto.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.widget.CategoryDto;
import io.suricate.monitoring.model.dto.widget.WidgetDto;
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.entity.widget.WidgetParam;
import io.suricate.monitoring.model.enums.ApiActionEnum;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.model.enums.WidgetVariableType;
import io.suricate.monitoring.model.mapper.widget.CategoryMapper;
import io.suricate.monitoring.model.mapper.widget.WidgetMapper;
import io.suricate.monitoring.service.GitService;
import io.suricate.monitoring.service.api.CategoryService;
import io.suricate.monitoring.service.api.ConfigurationService;
import io.suricate.monitoring.service.api.WidgetService;
import io.suricate.monitoring.utils.exception.ApiException;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * The widget controller
 */
@RestController
@RequestMapping("/api/widgets")
@Api(value = "Widget Controller", tags = {"Widget"})
public class WidgetController {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(WidgetController.class);

    /**
     * Widget service
     */
    private final WidgetService widgetService;

    /**
     * Category service
     */
    private final CategoryService categoryService;

    /**
     * Configuration service
     */
    private final ConfigurationService configurationService;

    /**
     * The GIT service
     */
    private final GitService gitService;

    /**
     * Mapper domain/DTO for categories
     */
    private final CategoryMapper categoryMapper;

    /**
     * The widget mapper
     */
    private final WidgetMapper widgetMapper;

    /**
     * Constructor
     *
     * @param widgetService   Widget service to inject
     * @param categoryService The category service
     * @param gitService      The git service
     * @param categoryMapper  The category mapper
     * @param widgetMapper    The widget mapper
     */
    @Autowired
    public WidgetController(final WidgetService widgetService,
                            final CategoryService categoryService,
                            final GitService gitService,
                            final CategoryMapper categoryMapper,
                            final WidgetMapper widgetMapper,
                            final ConfigurationService configurationService) {
        this.widgetService = widgetService;
        this.categoryService = categoryService;
        this.gitService = gitService;
        this.categoryMapper = categoryMapper;
        this.widgetMapper = widgetMapper;
        this.configurationService = configurationService;
    }

    /**
     * Get the list of widgets
     *
     * @return The list of widgets
     */
    @ApiOperation(value = "Get the full list of widgets", response = WidgetDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = WidgetDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<WidgetDto>> getWidgets(@ApiParam(name = "action", value = "REFRESH if we have to refresh widgets from GIT Repository", allowableValues = "refresh")
                                                      @RequestParam(value = "action", required = false) String action) {
        if (ApiActionEnum.REFRESH.name().equalsIgnoreCase(action)) {
            Future<Boolean> isDone = this.gitService.updateWidgetFromEnabledGitRepositories();

            try {
                if (!isDone.get()) {
                    throw new ApiException("Error while retrieving widgets from repository", ApiErrorEnum.INTERNAL_SERVER_ERROR);
                }
            } catch (InterruptedException e) {
                throw new ApiException("Execution interrupted while retrieving widgets from repository", ApiErrorEnum.INTERNAL_SERVER_ERROR);
            } catch (ExecutionException e) {
                throw new ApiException("Unknown execution error while retrieving widgets from repository", ApiErrorEnum.INTERNAL_SERVER_ERROR);
            }
        }

        Optional<List<Widget>> widgets = widgetService.getAll();
        if (!widgets.isPresent()) {
            throw new NoContentException(Widget.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(widgetMapper.toWidgetDtosDefault(widgets.get()));
    }

    /**
     * Update a widget
     *
     * @param widgetId  The widget id to update
     * @param widgetDto The object holding changes
     * @return The widget dto changed
     */
    @ApiOperation(value = "Update a widget by id", response = WidgetDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = WidgetDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Widget not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{widgetId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<WidgetDto> updateWidget(@ApiParam(name = "widgetId", value = "The widget id", required = true)
                                                  @PathVariable("widgetId") Long widgetId,
                                                  @ApiParam(name = "widgetDto", value = "The widget with modifications", required = true)
                                                  @RequestBody WidgetDto widgetDto) {
        Optional<Widget> widgetOpt = widgetService.updateWidget(widgetId, widgetDto);

        if (!widgetOpt.isPresent()) {
            throw new ObjectNotFoundException(Widget.class, widgetId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(widgetMapper.toWidgetDtoDefault(widgetOpt.get()));
    }

    /**
     * Get the list of widget categories
     *
     * @return A list of category
     */
    @ApiOperation(value = "Get the full list of widget categories", response = CategoryDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = CategoryDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        List<Category> categories = categoryService.getCategoriesOrderByName();

        if (categories == null || categories.isEmpty()) {
            throw new NoContentException(Category.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(categoryMapper.toCategoryDtosDefault(categories));

    }

    /**
     * Get every widget for a category
     *
     * @param categoryId The category id
     * @return The list of related widgets
     */
    @ApiOperation(value = "Get the list of widgets by category id", response = WidgetDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = WidgetDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Category not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/category/{categoryId}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<WidgetDto>> getWidgetByCategory(@ApiParam(name = "categoryId", value = "The category id", required = true)
                                                               @PathVariable("categoryId") Long categoryId) {
        if (!this.categoryService.isCategoryExists(categoryId)) {
            throw new ObjectNotFoundException(Category.class, categoryId);
        }

        Optional<List<Widget>> widgets = widgetService.getWidgetsByCategory(categoryId);
        if (!widgets.isPresent()) {
            throw new NoContentException(Widget.class);
        }

        // Also add global configuration for each widget
        List<WidgetParam> confs = configurationService.getConfigurationForWidgets().stream().filter(c -> c.getCategory().getId() == categoryId).map(ConfigurationService::initParamFromConfiguration).collect(Collectors.toList());
        widgets.get().stream().forEach(w -> w.getWidgetParams().addAll(confs));

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(widgetMapper.toWidgetDtosDefault(widgets.get()));
    }
}
