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

package com.michelin.suricate.model.dto.api.asset;

import com.michelin.suricate.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe an asset (Image, file, ...)")
public class AssetResponseDto extends AbstractDto {
    @Schema(description = "Id", example = "1")
    private Long id;

    @Schema(description = "The blob content")
    private byte[] content;

    @Schema(description = "The content type")
    private String contentType;

    @Schema(description = "The size of the asset", example = "100")
    private long size;
}
