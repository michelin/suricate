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

import io.suricate.monitoring.model.dto.widget.CategoryDto;
import io.suricate.monitoring.model.dto.widget.WidgetDto;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.mapper.widget.CategoryMapper;
import io.suricate.monitoring.model.mapper.widget.WidgetMapper;
import io.suricate.monitoring.service.api.CategoryService;
import io.suricate.monitoring.service.api.WidgetService;
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

/**
 * The widget controller
 */
@RestController
@RequestMapping("/api/widgets")
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
     * @param widgetService Widget service to inject
     * @param categoryService The category service
     * @param categoryMapper The category mapper
     * @param widgetMapper The widget mapper
     */
    @Autowired
    public WidgetController(final WidgetService widgetService,
                            final CategoryService categoryService,
                            final CategoryMapper categoryMapper,
                            final WidgetMapper widgetMapper) {
        this.widgetService = widgetService;
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.widgetMapper = widgetMapper;
    }

    /**
     * Get the list of widget categories
     *
     * @return A list of category
     */
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        List<Category> categories = categoryService.getCategoriesOrderByName();
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(categoryMapper.toCategoryDtosDefault(categories));

    }

    /**
     * Get every widget for a category
     *
     * @param id The category id
     * @return The list of related widgets
     */
    @RequestMapping(value = "/category/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<WidgetDto>> getWidgetByCategory(@PathVariable("id") Long id) {
        Optional<List<Widget>> widgets = widgetService.getWidgetsByCategory(id);

        if(!widgets.isPresent()) {
            ResponseEntity
                .noContent()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(widgetMapper.toWidgetDtosDefault(widgets.get()));
    }
}
