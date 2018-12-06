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

package io.suricate.monitoring.model.dto.api.project;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Project object used to create a new project
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ProjectRequest", description = "Used to create or update a project")
public class ProjectRequestDto extends AbstractDto {

    /**
     * The project name
     */
    @ApiModelProperty(value = "The project name", required = true)
    private String name;
    /**
     * Number of column in the dashboard
     */
    @ApiModelProperty(value = "The number of columns in the dashboard")
    private Integer maxColumn;
    /**
     * The height for widgets contained
     */
    @ApiModelProperty(value = "The height in pixel of the widget")
    private Integer widgetHeight;
    /**
     * The global css for the dashboard
     */
    @ApiModelProperty(value = "The css style of the dashboard grid")
    private String cssStyle;
}
