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

package io.suricate.monitoring.model.dto.api.project;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Create or update a project")
public class ProjectRequestDto extends AbstractDto {
    @Schema(description = "The project name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "The number of columns in the dashboard", example = "5")
    private Integer maxColumn;

    @Schema(description = "The height in pixel of the widget", example = "350")
    private Integer widgetHeight;

    @Schema(description = "The css style of the dashboard grid")
    private String cssStyle;
}
