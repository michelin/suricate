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
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.dto.widget.WidgetResponse;
import io.suricate.monitoring.service.api.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/widgets")
public class WidgetController {

    private WidgetService widgetService;

    @Autowired
    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<CategoryDto> getCategories() {
        List<Category> categories = widgetService.getCategories();
        return categories.stream().map(category -> new CategoryDto(category)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/category/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<WidgetResponse> getWidgetByCategory(@PathVariable("id") Long id) {
        return widgetService.getWidgetsByCategory(id);
    }
}
