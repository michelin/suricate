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
import io.suricate.monitoring.service.api.CategoryService;
import io.suricate.monitoring.service.api.WidgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    private WidgetService widgetService;

    /**
     * Category service
     */
    private CategoryService categoryService;

    /**
     * Constructor
     *
     * @param widgetService Widget service to inject
     */
    @Autowired
    public WidgetController(final WidgetService widgetService, final CategoryService categoryService) {
        this.widgetService = widgetService;
        this.categoryService = categoryService;
    }

    /**
     * Get the list of widget categories
     *
     * @return A list of category
     */
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<CategoryDto> getCategories() {
        List<Category> categories = categoryService.getCategoriesOrderByName();
        return categories.stream().map(CategoryDto::new).collect(Collectors.toList());
    }

    /**
     * Get every widget for a category
     *
     * @param id The category id
     * @return The list of related widgets
     */
    @RequestMapping(value = "/category/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<WidgetDto> getWidgetByCategory(@PathVariable("id") Long id) {
        List<Widget> widgets = widgetService.getWidgetsByCategory(id);

        return widgetService.transformIntoDTOs(widgets);
    }
}
