/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
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
 *
 */

package io.suricate.monitoring.model.dto.api.projectwidget;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Object representing a project widget used for communication with clients of the webservice
 * This is the instantiation of a widget
 * Link a widget with a dashboard/project
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ProjectWidgetRequest", description = "Create/update project widget")
public class ProjectWidgetRequestDto extends AbstractDto {

    /**
     * The start col number on the grid
     */
    @ApiModelProperty(value = "The start col number on the grid")
    private int gridColumn = 0;

    /**
     * The start row number on the grid
     */
    @ApiModelProperty(value = "The start row number on the grid")
    private int gridRow = 0;

    /**
     * Index of the grid the widget instance belongs
     */
    @ApiModelProperty(value = "Index of the grid the widget instance belongs")
    private int gridIndex;

    /**
     * The number of row taken on the grid
     */
    @ApiModelProperty(value = "The number of row taken on the grid")
    private int height = 1;

    /**
     * The number of col taken on the grid
     */
    @ApiModelProperty(value = "The number of col taken on the grid")
    private int width = 1;

    /**
     * The data of the last execution
     */
    @ApiModelProperty(value = "The data of the last execution")
    private String data = "{}";

    /**
     * The css style for this instance
     */
    @ApiModelProperty(value = "The css for this instance of widget")
    private String customStyle;

    /**
     * Contains the configuration of the widget
     */
    @ApiModelProperty(value = "The configuration of this widget")
    private String backendConfig;

    /**
     * The widgetId related to this project widget
     */
    @ApiModelProperty(value = "The widgetId related to this project widget")
    private Long widgetId;
}
