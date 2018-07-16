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

package io.suricate.monitoring.model.dto;

import io.suricate.monitoring.model.dto.widget.WidgetDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * Library used for communication with the clients via webservices
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value = "Library", description = "Describe a JS Library")
public class LibraryDto extends AbstractDto {

    /**
     * The library id
     */
    @ApiModelProperty(value = "The database id")
    private Long id;

    /**
     * The library technical name
     */
    @ApiModelProperty(value = "A unique technical name")
    private String technicalName;

    /**
     * The related asset
     */
    @ApiModelProperty(value = "The related asset for this library")
    private AssetDto asset;

    /**
     * List of widgets related to it
     */
    @ApiModelProperty(value = "The list of widgets that use this library")
    private List<WidgetDto> widgets;
}
