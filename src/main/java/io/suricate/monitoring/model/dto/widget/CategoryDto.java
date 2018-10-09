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

package io.suricate.monitoring.model.dto.widget;

import io.suricate.monitoring.model.dto.ConfigurationDto;
import io.suricate.monitoring.model.entity.Asset;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;


/**
 * Represent a cateogry used for communication with the clients via webservices
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@ApiModel(value = "Category", description = "Describe a widget category")
public class CategoryDto {
    /**
     * The category id
     */
    @ApiModelProperty(value = "The category id")
    private Long id;
    /**
     * The category name
     */
    @ApiModelProperty(value = "Category name")
    private String name;
    /**
     * The technical name of the category
     */
    @ApiModelProperty(value = "Category technical name, should be unique in table")
    private String technicalName;
    /**
     * The image related to this category
     */
    @ApiModelProperty(value = "Related category image")
    private Asset image;

    /**
     * The list of widgets related to this category
     */
    @ApiModelProperty(value = "List of related widgets", dataType = "java.util.List")
    private List<WidgetDto> widgets;

    /**
     * The associated categories for this configuration
     */
    @ApiModelProperty(value = "Related configurations for this category", dataType = "java.util.List")
    private List<ConfigurationDto> configurations;
}
