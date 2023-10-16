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

import com.michelin.suricate.configuration.swagger.ApiPageable;
import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.dto.api.widget.WidgetRequestDto;
import com.michelin.suricate.model.dto.api.widget.WidgetResponseDto;
import com.michelin.suricate.model.entities.Widget;
import com.michelin.suricate.services.api.WidgetService;
import com.michelin.suricate.services.mapper.WidgetMapper;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import java.util.Optional;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Widget controller.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Widget", description = "Widget Controller")
public class WidgetController {
    @Autowired
    private WidgetService widgetService;

    @Autowired
    private WidgetMapper widgetMapper;

    /**
     * Get the list of widgets.
     *
     * @return The list of widgets
     */
    @Operation(summary = "Get the full list of widgets")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = {
            @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @ApiPageable
    @GetMapping(value = "/v1/widgets")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Page<WidgetResponseDto> getWidgets(@Parameter(name = "search", description = "Search keyword")
                                              @RequestParam(value = "search", required = false) String search,
                                              @ParameterObject Pageable pageable) {
        return widgetService.getAll(search, pageable)
            .map(widgetMapper::toWidgetWithoutCategoryParametersDto);
    }

    /**
     * Get a widget.
     *
     * @param widgetId The widget id to update
     */
    @Operation(summary = "Retrieve a widget by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = {
            @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Widget not found", content = {
            @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/widgets/{widgetId}")
    @PermitAll
    public ResponseEntity<WidgetResponseDto> getOneById(
        @Parameter(name = "widgetId", description = "The widget id", required = true, example = "1")
        @PathVariable("widgetId") Long widgetId) {
        Optional<Widget> widget = widgetService.findOne(widgetId);

        if (widget.isEmpty()) {
            throw new ObjectNotFoundException(Widget.class, widgetId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(widgetMapper.toWidgetDto(widget.get()));
    }

    /**
     * Update a widget.
     *
     * @param widgetId         The widget id to update
     * @param widgetRequestDto The object holding changes
     * @return The widget dto changed
     */
    @Operation(summary = "Update a widget by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Widget updated"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = {
            @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Widget not found", content = {
            @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PutMapping(value = "/v1/widgets/{widgetId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateWidget(
        @Parameter(name = "widgetId", description = "The widget id", required = true, example = "1")
        @PathVariable("widgetId") Long widgetId,
        @Parameter(name = "widgetRequestDto", description = "The widget with modifications", required = true)
        @RequestBody WidgetRequestDto widgetRequestDto) {
        Optional<Widget> widgetOptional = widgetService.updateWidget(widgetId, widgetRequestDto);

        if (widgetOptional.isEmpty()) {
            throw new ObjectNotFoundException(Widget.class, widgetId);
        }

        return ResponseEntity.noContent().build();
    }
}
