/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Create/update project widget")
public class ProjectWidgetRequestDto extends AbstractDto {
    @Schema(description = "The start col number on the grid", example = "1")
    private int gridColumn = 1;

    @Schema(description = "The start row number on the grid", example = "1")
    private int gridRow = 1;

    @Schema(description = "The number of row taken on the grid", example = "1")
    private int height = 1;

    @Schema(description = "The number of col taken on the grid", example = "1")
    private int width = 1;

    @Schema(description = "The data of the last execution")
    private String data = "{}";

    @Schema(description = "The css for this instance of widget")
    private String customStyle;

    @Schema(description = "The configuration of this widget")
    private String backendConfig;

    @Schema(description = "The widgetId related to this project widget", example = "1")
    private Long widgetId;
}
