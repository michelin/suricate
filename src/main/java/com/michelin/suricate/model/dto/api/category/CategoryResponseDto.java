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

package com.michelin.suricate.model.dto.api.category;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.dto.api.asset.AssetResponseDto;
import com.michelin.suricate.model.dto.api.widget.WidgetResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Category response DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a widget category response")
public class CategoryResponseDto extends AbstractDto {
    @Schema(description = "The category id", example = "1")
    private Long id;

    @Schema(description = "Category name")
    private String name;

    @Schema(description = "Category technical name, should be unique in table")
    private String technicalName;

    @Schema(description = "Asset token")
    private String assetToken;

    @Schema(description = "The image")
    private AssetResponseDto image;

    @Schema(description = "Category parameters")
    private List<CategoryParameterResponseDto> categoryParameters;

    @Schema(description = "The widgets", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<WidgetResponseDto> widgets = new ArrayList<>();
}
