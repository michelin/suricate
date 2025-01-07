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

package com.michelin.suricate.model.dto.api.widget;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Widget param response DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe the params for an instance of widget")
public class WidgetParamResponseDto extends AbstractDto {
    @Schema(description = "The param name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Describe how to set this param", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "HTML Default value to insert on the field")
    private String defaultValue;

    @Schema(description = "The type of this param define the HTML element to display",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private DataTypeEnum type;

    @Schema(description = "A regex to respect for the field")
    private String acceptFileRegex;

    @Schema(description = "An example of the usage of this field")
    private String usageExample;

    @Schema(description = "The usage tooltip of the parameter")
    private String usageTooltip;

    @Schema(description = "If the field is required or not", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean required = true;

    @Schema(description = "The list of possible values if the type is COMBO or MULTIPLE", type = "java.util.List")
    private List<WidgetParamValueResponseDto> values = new ArrayList<>();
}
