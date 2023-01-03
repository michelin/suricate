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

package io.suricate.monitoring.model.dto.api.user;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.dto.api.setting.AllowedSettingValueResponseDto;
import io.suricate.monitoring.model.dto.api.setting.SettingResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "The setting saved for the user")
public class UserSettingResponseDto extends AbstractDto {
    @Schema(description = "The id line", example = "1")
    private Long id;

    @Schema(description = "The user id related to this setting", example = "1")
    private Long userId;

    @Schema(description = "The related setting")
    private SettingResponseDto setting;

    @Schema(description = "The selected value if it's a constrained setting")
    private AllowedSettingValueResponseDto settingValue;

    @Schema(description = "The value typed by the user it's an unconstrained field")
    private String unconstrainedValue;
}
