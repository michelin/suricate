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

package io.suricate.monitoring.model.dto.api.category;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.dto.api.asset.AssetResponseDto;
import io.suricate.monitoring.model.dto.api.widget.WidgetResponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 * Category response DTO
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CategoryResponse", description = "Describe a widget category response")
public class CategoryResponseDto extends AbstractDto {
    /**
     * The category id
     */
    @ApiModelProperty(value = "The category id", example = "1")
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
     * The image token related to this category
     */
    @ApiModelProperty(value = "Asset token")
    private String assetToken;

    /**
     * The image
     */
    @ApiModelProperty(value = "The image")
    private AssetResponseDto image;

    /**
     * The category parameters
     */
    @ApiModelProperty(value = "Category parameters")
    private List<CategoryParameterResponseDto> categoryParameters;

    /**
     * The widgets
     */
    @ApiModelProperty(value = "The widgets", required = true)
    private List<WidgetResponseDto> widgets = new ArrayList<>();
}
