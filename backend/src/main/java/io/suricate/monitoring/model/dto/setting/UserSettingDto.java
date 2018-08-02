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

package io.suricate.monitoring.model.dto.setting;

import io.suricate.monitoring.model.dto.AbstractDto;
import io.suricate.monitoring.model.dto.user.UserDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * The user setting DTO for REST communication
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value = "UserSetting", description = "The setting saved for the user")
public class UserSettingDto extends AbstractDto {

    /**
     * The user setting id
     */
    @ApiModelProperty(value = "The id line")
    private Long id;

    /**
     * The related user
     */
    @ApiModelProperty(value = "The user related to this setting")
    private UserDto user;

    /**
     * The setting reference
     */
    @ApiModelProperty(value = "The related setting")
    private SettingDto setting;

    /**
     * The allowed setting value
     */
    @ApiModelProperty(value = "The selected value if it's a constrained setting")
    private AllowedSettingValueDto settingValue;

    /**
     * The unconstrained value
     */
    @ApiModelProperty(value = "The value typed by the user it's an unconstrained field")
    private String unconstrainedValue;
}
