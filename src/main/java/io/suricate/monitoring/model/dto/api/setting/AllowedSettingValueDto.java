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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * The Allowed setting value DTO used for REST communication
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value = "AllowedSettingValue", description = "Describe the possible values for a setting")
public class AllowedSettingValueDto extends AbstractDto {

    /**
     * The setting id
     */
    @ApiModelProperty(value = "The setting value id", required = true)
    private Long id;

    /**
     * The title to display for the user
     */
    @ApiModelProperty(value = "The title displayed to the user", required = true)
    private String title;

    /**
     * The value of the entry (used in the code)
     */
    @ApiModelProperty(value = "The value that will be used on the code for this setting", required = true)
    private String value;

    /**
     * True if this setting is the default setting
     */
    @ApiModelProperty(value = "True if this value should be used as default", required = true)
    private boolean isDefault;

    /**
     * The related setting
     */
    @ApiModelProperty(value = "The related setting", required = true)
    private SettingDto setting;
}
