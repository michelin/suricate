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

package com.michelin.suricate.model.dto.api.widgetconfiguration;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.dto.api.category.CategoryResponseDto;
import com.michelin.suricate.model.enums.DataTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a configuration")
public class WidgetConfigurationResponseDto extends AbstractDto {
    @Schema(description = "The configuration key")
    private String key;

    @Schema(description = "The configuration value")
    private String value;

    @Schema(description = "Configuration data type")
    private DataTypeEnum dataType;

    @Schema(description = "Related category for this config")
    private CategoryResponseDto category;
}
