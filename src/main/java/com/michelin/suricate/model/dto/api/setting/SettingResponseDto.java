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
package com.michelin.suricate.model.dto.api.setting;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import com.michelin.suricate.model.enumeration.SettingType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Setting response DTO. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a setting")
public class SettingResponseDto extends AbstractDto {
    @Schema(description = "The setting id", example = "1")
    private Long id;

    @Schema(description = "The description/name of the setting")
    private String description;

    @Schema(description = "True if the setting have constrained values, or if it's a free field")
    private boolean constrained;

    @Schema(description = "The data type for this setting")
    private DataTypeEnum dataType;

    @Schema(description = "The setting type")
    private SettingType type;

    @Schema(description = "The possible values for this setting if it's a constrained field", type = "java.util.List")
    private List<AllowedSettingValueResponseDto> allowedSettingValues = new ArrayList<>();
}
