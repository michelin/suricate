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

package com.michelin.suricate.controllers;

import com.michelin.suricate.model.dto.api.category.CategoryResponseDto;
import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.dto.api.widget.WidgetResponseDto;
import com.michelin.suricate.model.entities.Widget;
import com.michelin.suricate.services.api.CategoryService;
import com.michelin.suricate.services.api.WidgetService;
import com.michelin.suricate.services.mapper.CategoryMapper;
import com.michelin.suricate.services.mapper.WidgetMapper;
import com.michelin.suricate.utils.exceptions.NoContentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Category controller.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Category", description = "Category Controller")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private WidgetMapper widgetMapper;

    /**
     * Get the list of widget categories.
     *
     * @return A list of category
     */
    @Operation(summary = "Get the full list of widget categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "204", description = "No Content", content = {
            @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = {
            @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PageableAsQueryParam
    @GetMapping(value = "/v1/categories")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Page<CategoryResponseDto> getCategories(@Parameter(name = "search", description = "Search keyword")
                                                   @RequestParam(value = "search", required = false) String search,
                                                   @Parameter(hidden = true) Pageable pageable) {
        return categoryService.getAll(search, pageable).map(categoryMapper::toCategoryWithoutParametersDto);
    }

    /**
     * Get every widget for a category.
     *
     * @param categoryId The category id
     * @return The list of related widgets
     */
    @Operation(summary = "Get the list of widgets by category id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid"),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping(value = "/v1/categories/{categoryId}/widgets")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<WidgetResponseDto>> getWidgetByCategory(
        @Parameter(name = "categoryId", description = "The category id", required = true, example = "1")
        @PathVariable("categoryId") Long categoryId) {
        Optional<List<Widget>> widgetsOptional = widgetService.getWidgetsByCategory(categoryId);

        if (widgetsOptional.isEmpty()) {
            throw new NoContentException(Widget.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(widgetMapper.toWidgetsDtos(widgetsOptional.get()));
    }
}
