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

package io.suricate.monitoring.model.dto.api.widget;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Library used for communication with the clients via webservices
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "LibraryResponse", description = "Describe a JS Library")
public class LibraryResponseDto extends AbstractDto {

    /**
     * The library id
     */
    @ApiModelProperty(value = "The database id", example = "1")
    private Long id;

    /**
     * The library technical name
     */
    @ApiModelProperty(value = "A unique technical name")
    private String technicalName;

    /**
     * The related asset
     */
    @ApiModelProperty(value = "The related asset token for this library")
    private String assetToken;
}
