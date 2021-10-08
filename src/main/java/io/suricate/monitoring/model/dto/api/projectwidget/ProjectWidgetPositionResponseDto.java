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

package io.suricate.monitoring.model.dto.api.projectwidget;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Widget position used for communication with the clients via webservices
 * (For example when a widget change of position)
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ProjectWidgetPositionResponse", description = "Position of the widget on the grid")
public class ProjectWidgetPositionResponseDto extends AbstractDto {

    /**
     * The start column of this widget
     */
    @ApiModelProperty(value = "The number of the column where the widget should start to be displayed")
    private int gridColumn;

    /**
     * The start row of the widget
     */
    @ApiModelProperty(value = "The number of the row where the widget should start to be displayed")
    private int gridRow;

    /**
     * The number of columns for this widget
     */
    @ApiModelProperty(value = "The number of columns taken by this widget")
    private int width;

    /**
     * The number of rows
     */
    @ApiModelProperty(value = "The number of rows taken by this widget")
    private int height;
}
