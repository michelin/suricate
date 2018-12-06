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

package io.suricate.monitoring.model.dto.api.project;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.dto.api.widget.WidgetDto;
import io.suricate.monitoring.model.enums.WidgetState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * Object representing a project widget used for communication with clients of the webservice
 * This is the instantiation of a widget
 * Link a widget with a dashboard/project
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value = "ProjectWidget", description = "Describe an instantiation of a widget")
public class ProjectWidgetDto extends AbstractDto {

    /**
     * The project widget id
     */
    @ApiModelProperty(value = "The project widget id")
    private Long id;

    /**
     * The data of the last execution
     */
    @ApiModelProperty(value = "The data of the last execution of this widget")
    private String data;

    /**
     * The position of this instance of widget in the grid
     */
    @ApiModelProperty(value = "The position of the widget on the grid")
    private ProjectWidgetPositionDto widgetPosition;

    /**
     * The css style for this instance
     */
    @ApiModelProperty(value = "The css for this instance of widget")
    private String customStyle;

    /**
     * The instantiation of the html widget template
     */
    @ApiModelProperty(value = "The html of the widget instantiate with the params")
    private String instantiateHtml;

    /**
     * Contains the configuration of the widget
     */
    @ApiModelProperty(value = "The configuration of this widget")
    private String backendConfig;

    /**
     * The log of the last nashorn execution
     */
    @ApiModelProperty(value = "The log of the execution")
    private String log;

    /**
     * The date of the last execution
     */
    @ApiModelProperty(value = "The last execution date")
    private Date lastExecutionDate;

    /**
     * The date of the last execution success of nashorn
     */
    @ApiModelProperty(value = "The last successful execution date")
    private Date lastSuccessDate;

    /**
     * The widget state {@link WidgetState}
     */
    @ApiModelProperty(value = "The current widget state")
    private WidgetState state;

    /**
     * The related project
     */
    @ApiModelProperty(value = "The related project")
    private ProjectResponseDto project;

    /**
     * The related widget
     */
    @ApiModelProperty(value = "The related widget")
    private WidgetDto widget;
}
