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
package com.michelin.suricate.model.dto.api.category;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Category parameter response DTO. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a category parameter")
public class CategoryParameterResponseDto extends AbstractDto {
    @Schema(description = "The category parameter key")
    private String key;

    @Schema(description = "The category parameter value")
    private String value;

    @Schema(description = "The description")
    private String description;

    @Schema(description = "The export value")
    private boolean export;

    @Schema(description = "The category parameter data type")
    private DataTypeEnum dataType;

    @Schema(description = "Related category for this config")
    private CategoryResponseDto category;
}
