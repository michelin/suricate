/*
 *
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.controllers;

import io.suricate.monitoring.configuration.swagger.ApiPageable;
import io.suricate.monitoring.model.dto.api.category.CategoryResponseDto;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.widget.WidgetResponseDto;
import io.suricate.monitoring.model.entities.Widget;
import io.suricate.monitoring.services.api.CategoryService;
import io.suricate.monitoring.services.api.WidgetService;
import io.suricate.monitoring.services.mapper.CategoryMapper;
import io.suricate.monitoring.services.mapper.WidgetMapper;
import io.suricate.monitoring.utils.exceptions.NoContentException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * The widget controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Category Controller", tags = {"Categories"})
public class CategoryController {

    /**
     * The category service
     */
    private final CategoryService categoryService;

    /**
     * The category dto/object mapper
     */
    private final CategoryMapper categoryMapper;

    /**
     * The widget service to inject
     */
    private final WidgetService widgetService;

    /**
     * The widget mapper to inject
     */
    private final WidgetMapper widgetMapper;

    /**
     * Constructor
     *
     * @param categoryService            The category service to inject
     * @param categoryMapper             The category mapper to inject
     * @param widgetService              The widget service to inject
     * @param widgetMapper               The widget mapper to inject
     */
    @Autowired
    public CategoryController(final CategoryService categoryService,
                              final CategoryMapper categoryMapper,
                              final WidgetService widgetService,
                              final WidgetMapper widgetMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.widgetService = widgetService;
        this.widgetMapper = widgetMapper;
    }

    /**
     * Get the list of widget categories
     *
     * @return A list of category
     */
    @ApiOperation(value = "Get the full list of widget categories", response = CategoryResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = CategoryResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @ApiPageable
    @GetMapping(value = "/v1/categories")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Page<CategoryResponseDto> getCategories(@ApiParam(name = "search", value = "Search keyword")
                                                   @RequestParam(value = "search", required = false) String search,
                                                   Pageable pageable) {
        return this.categoryService.getAll(search, pageable).map(categoryMapper::toCategoryWithoutParametersDTO);
    }

    /**
     * Get every widget for a category
     *
     * @param categoryId The category id
     * @return The list of related widgets
     */
    @ApiOperation(value = "Get the list of widgets by category id", response = WidgetResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = WidgetResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Category not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/categories/{categoryId}/widgets")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<WidgetResponseDto>> getWidgetByCategory(@ApiParam(name = "categoryId", value = "The category id", required = true)
                                                                       @PathVariable("categoryId") Long categoryId) {
        Optional<List<Widget>> widgetsOptional = this.widgetService.getWidgetsByCategory(categoryId);

        if (!widgetsOptional.isPresent()) {
            throw new NoContentException(Widget.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(this.widgetMapper.toWidgetsDTOs(widgetsOptional.get()));
    }
}
