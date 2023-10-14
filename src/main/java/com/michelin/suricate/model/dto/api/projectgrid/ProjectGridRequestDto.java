/*
 *
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.michelin.suricate.model.dto.api.projectgrid;

import com.michelin.suricate.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Project grid request DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Create or update a project grid")
public class ProjectGridRequestDto extends AbstractDto {
    @Schema(description = "In case of rotations, should the progress bar be displayed for the project")
    private boolean displayProgressBar;

    @Schema(description = "The list of grids")
    private List<GridRequestDto> grids;

    /**
     * Grid request DTO.
     */
    @Data
    public static class GridRequestDto {
        @Schema(description = "The project grid id", example = "1")
        private Long id;

        @Schema(description = "The time", example = "30")
        private Integer time;
    }
}
