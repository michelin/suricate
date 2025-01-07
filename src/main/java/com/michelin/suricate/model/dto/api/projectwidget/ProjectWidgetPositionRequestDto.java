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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Project widget position request DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Modify the position of a widget")
public class ProjectWidgetPositionRequestDto extends AbstractDto {
    @Schema(description = "The related project widget id", example = "1")
    private Long projectWidgetId;

    @Schema(description = "The number of the column where the widget should start to be displayed", example = "1")
    private int gridColumn;

    @Schema(description = "The number of the row where the widget should start to be displayed", example = "1")
    private int gridRow;

    @Schema(description = "The number of columns taken by this widget", example = "1")
    private int width;

    @Schema(description = "The number of rows taken by this widget", example = "1")
    private int height;
}
