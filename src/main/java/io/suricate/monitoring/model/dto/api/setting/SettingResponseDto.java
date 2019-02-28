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

package io.suricate.monitoring.model.dto.api.setting;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.DataType;
import io.suricate.monitoring.model.enums.SettingType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Setting DTO used for REST communication
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "SettingResponse", description = "Describe a setting")
public class SettingResponseDto extends AbstractDto {
    /**
     * The setting id
     */
    @ApiModelProperty(value = "The setting id")
    private Long id;

    /**
     * The setting name/description
     */
    @ApiModelProperty(value = "The description/name of the setting")
    private String description;

    /**
     * Tell if the settings have constrained values
     */
    @ApiModelProperty(value = "True if the setting have constrained values, or if it's a free field")
    private boolean constrained;

    /**
     * The setting data type
     */
    @ApiModelProperty(value = "The data type for this setting")
    private DataType dataType;

    /**
     * The setting type
     */
    @ApiModelProperty(value = "The setting type")
    private SettingType type;

    /**
     * Hold the possible values (if we have a select setting for example)
     */
    @ApiModelProperty(value = "The possible values for this setting if it's a constrained field", dataType = "java.util.List")
    private List<AllowedSettingValueResponseDto> allowedSettingValues = new ArrayList<>();
}
