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
import io.suricate.monitoring.model.dto.api.asset.AssetResponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Project object used for communication with clients of the webservice
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ProjectResponse", description = "Describe a project/dashboard")
public class ProjectResponseDto extends AbstractDto {

    /**
     * The dashboard token
     */
    @ApiModelProperty(value = "The project token", required = true)
    private String token;

    /**
     * The project name
     */
    @ApiModelProperty(value = "The project name", required = true)
    private String name;

    /**
     * The grid properties for a dashboard
     */
    @ApiModelProperty(value = "The properties of the dashboard grid")
    private ProjectGridResponseDto gridProperties;

    /**
     * A representation by an image of the dashboard
     */
    @ApiModelProperty(value = "A representation by an image of the dashboard")
    private String screenshotToken;

    /**
     * Image of the dashboard
     */
    @ApiModelProperty(value = "Image of the dashboard")
    private AssetResponseDto image;

    /**
     * The libraries related
     */
    @ApiModelProperty(value = "The list of the related JS libraries used for the execution of the widgets", dataType = "java.util.List")
    private List<String> librariesToken = new ArrayList<>();
}
