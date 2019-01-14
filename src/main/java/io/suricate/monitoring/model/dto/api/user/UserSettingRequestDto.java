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

package io.suricate.monitoring.model.dto.api.user;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * The user setting DTO for REST communication
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "UserSettingResponse", description = "The setting saved for the user")
public class UserSettingRequestDto extends AbstractDto {

    /**
     * The allowed setting value
     */
    @ApiModelProperty(value = "The selected value Id if it's a constrained setting")
    @Nullable
    private Long allowedSettingValueId;

    /**
     * The unconstrained value
     */
    @ApiModelProperty(value = "The value typed by the user it's an unconstrained field")
    @Nullable
    private String unconstrainedValue;
}
