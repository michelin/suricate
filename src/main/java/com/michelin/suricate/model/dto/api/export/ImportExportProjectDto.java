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
package com.michelin.suricate.model.dto.api.export;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.dto.api.project.GridPropertiesResponseDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetPositionResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Import export project DTO. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Import a project")
public class ImportExportProjectDto extends AbstractDto {
    @Schema(description = "The list of grids")
    List<ImportExportProjectGridDto> grids = new ArrayList<>();

    @Schema(description = "The project token", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    @Schema(description = "The project name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "In case of rotations, should the progress bar be displayed for the project")
    private boolean displayProgressBar;

    @Schema(description = "Image of the dashboard")
    private ImportExportAssetDto image;

    @Schema(description = "The grid properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private GridPropertiesResponseDto gridProperties;

    /** Import export project grid DTO. */
    @Data
    public static class ImportExportProjectGridDto {
        @Schema(description = "The list of grids")
        List<ImportExportProjectWidgetDto> widgets = new ArrayList<>();

        @Schema(description = "The ID", example = "1")
        private Long id;

        @Schema(description = "The time", example = "15")
        private Integer time;

        /** Import export project widget DTO. */
        @Data
        public static class ImportExportProjectWidgetDto {
            @Schema(description = "The ID", example = "1")
            private Long id;

            @Schema(description = "The related widget technical name")
            private String widgetTechnicalName;

            @Schema(description = "The configuration of this widget")
            private String backendConfig;

            @Schema(description = "The position of the widget on the grid")
            private ProjectWidgetPositionResponseDto widgetPosition;
        }
    }
}
