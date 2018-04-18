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

package io.suricate.monitoring.model.dto.project;

import io.suricate.monitoring.model.dto.AbstractDto;
import io.suricate.monitoring.model.dto.widget.WidgetDto;
import io.suricate.monitoring.model.enums.WidgetState;
import lombok.*;

import java.util.Date;

/**
 * Object representing a project widget used for communication with clients of the webservice
 * This is the instantiation of a widget
 * Link a widget with a dashboard/project
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class ProjectWidgetDto extends AbstractDto {

    /**
     * The project widget id
     */
    private Long id;

    /**
     * The data of the last execution
     */
    private String data;

    /**
     * The position of this instance of widget in the grid
     */
    private ProjectWidgetPositionDto widgetPosition;

    /**
     * The css style for this instance
     */
    private String customStyle;

    /**
     * The instantiation of the html widget template
     */
    private String instantiateHtml;

    /**
     * Contains the configuration of the widget
     */
    private String backendConfig;

    /**
     * The log of the last nashorn execution
     */
    private String log;

    /**
     * The date of the last execution
     */
    private Date lastExecutionDate;

    /**
     * The date of the last execution success of nashorn
     */
    private Date lastSuccessDate;

    /**
     * The widget state {@link WidgetState}
     */
    private WidgetState state;

    /**
     * The related project
     */
    private ProjectDto project;

    /**
     * The related widget
     */
    private WidgetDto widget;
}
