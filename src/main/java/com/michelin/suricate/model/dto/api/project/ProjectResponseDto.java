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

package com.michelin.suricate.model.dto.api.project;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.dto.api.asset.AssetResponseDto;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a project/dashboard")
public class ProjectResponseDto extends AbstractDto {
    @Schema(description = "The project token", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    @Schema(description = "The project name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "The properties of the dashboard grid")
    private GridPropertiesResponseDto gridProperties;

    @Schema(description = "A representation by an image of the dashboard")
    private String screenshotToken;

    @Schema(description = "In case of rotations, should the progress bar be displayed for the project")
    private boolean displayProgressBar;

    @Schema(description = "Image of the dashboard")
    private AssetResponseDto image;

    @Schema(description = "The list of the related JS libraries used for the execution of the widgets", type = "java.util.List")
    private List<String> librariesToken = new ArrayList<>();

    @Schema(description = "The grids", type = "java.util.List")
    private List<ProjectGridResponseDto> grids = new ArrayList<>();
}
