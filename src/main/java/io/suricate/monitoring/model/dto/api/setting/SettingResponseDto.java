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

package io.suricate.monitoring.model.dto.api.setting;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.suricate.monitoring.model.enums.SettingType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
