/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.model.dto.api.projectwidget;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.enumeration.WidgetStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Project widget response DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe an instantiation of a widget")
public class ProjectWidgetResponseDto extends AbstractDto {
    @Schema(description = "The project widget id", example = "1")
    private Long id;

    @Schema(description = "The data of the last execution of this widget")
    private String data;

    @Schema(description = "The position of the widget on the grid")
    private ProjectWidgetPositionResponseDto widgetPosition;

    @Schema(description = "The css for this instance of widget")
    private String customStyle;

    @Schema(description = "The html of the widget instantiate with the params")
    private String instantiateHtml;

    @Schema(description = "The configuration of this widget")
    private String backendConfig;

    @Schema(description = "The log of the execution")
    private String log;

    @Schema(description = "The last execution date")
    private Date lastExecutionDate;

    @Schema(description = "The last successful execution date")
    private Date lastSuccessDate;

    @Schema(description = "The current widget state")
    private WidgetStateEnum state;

    @Schema(description = "The related project token")
    private String projectToken;

    @Schema(description = "The related widget id", example = "1")
    private Long widgetId;

    @Schema(description = "The related widget technical name")
    private String widgetTechnicalName;

    @Schema(description = "The project grid id", example = "1")
    private Long gridId;
}
